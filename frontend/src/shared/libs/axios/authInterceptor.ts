import type { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { userApi, departmentApi, ticketApi, bareUserApi } from './axiosClient';
import { useAuthStore } from '../../stores/auth.store';
import { jwtDecode } from 'jwt-decode';

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

type JwtPayload = { exp?: number };
function isExpiringSoon(token: string | null, skewMs = 60_000): boolean {
  if (!token) return false;
  try {
    const { exp } = jwtDecode<JwtPayload>(token);
    if (!exp) return false;
    const expiresAtMs = exp * 1000;
    return Date.now() + skewMs >= expiresAtMs;
  } catch { return false; }
}

let refreshingPromise: Promise<boolean> | null = null;
async function ensureFreshAccessToken(): Promise<void> {
  if (!isExpiringSoon(useAuthStore.getState().accessToken)) return;
  if (!refreshingPromise) {
    refreshingPromise = refreshTokenFlow().finally(() => { refreshingPromise = null; });
  }
  await refreshingPromise;
}

function setupClient(client: AxiosInstance) {
  client.interceptors.request.use(async (config) => {
    await ensureFreshAccessToken();
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


