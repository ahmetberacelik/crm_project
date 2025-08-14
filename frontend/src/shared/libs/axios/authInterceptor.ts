import type { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { userApi, departmentApi, ticketApi, bareUserApi } from './axiosClient';
import { useAuthStore } from '../../stores/auth.store';

function attachAuthHeader(config: InternalAxiosRequestConfig, token: string | null) {
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}

async function refreshTokenFlow(): Promise<boolean> {
  const { refreshToken, setTokens, clear } = useAuthStore.getState();
  if (!refreshToken) return false;
  try {
    const res = await bareUserApi.post('/api/auth/refresh', { refreshToken });
    const { accessToken: newAccessToken, refreshToken: newRefreshToken } = res.data ?? {};
    if (newAccessToken && newRefreshToken) {
      setTokens({ accessToken: newAccessToken, refreshToken: newRefreshToken });
      return true;
    }
  } catch {
    // ignore
  }
  clear();
  return false;
}

function setupClient(client: AxiosInstance) {
  client.interceptors.request.use((config) => {
    const { accessToken } = useAuthStore.getState();
    return attachAuthHeader(config, accessToken);
  });

  client.interceptors.response.use(
    (res) => res,
    async (error: AxiosError) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
      const status = error.response?.status;

      if (status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        const ok = await refreshTokenFlow();
        if (ok) {
          const { accessToken } = useAuthStore.getState();
          attachAuthHeader(originalRequest, accessToken);
          return client.request(originalRequest);
        }
      }
      return Promise.reject(error);
    }
  );
}

export function setupAuthInterceptors() {
  setupClient(userApi);
  setupClient(departmentApi);
  setupClient(ticketApi);
}


