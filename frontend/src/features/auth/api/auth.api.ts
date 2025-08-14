import { userApi } from '../../../shared/libs/axios/axiosClient';
import { useAuthStore } from '../../../shared/stores/auth.store';

type LoginRequest = {
  userName: string;
  password: string;
};

type AuthResponse = {
  accessToken?: string; // backend returns both token and accessToken for compatibility
  token?: string;
  refreshToken: string;
};

export async function login(request: LoginRequest) {
  const res = await userApi.post<AuthResponse>('/api/auth/login', request);
  const data = res.data;
  const accessToken = data.accessToken ?? data.token;
  if (!accessToken || !data.refreshToken) {
    throw new Error('Invalid auth response');
  }
  useAuthStore.getState().setTokens({ accessToken, refreshToken: data.refreshToken });
}

export async function logout() {
  try {
    await userApi.post('/api/auth/logout');
  } finally {
    useAuthStore.getState().clear();
  }
}


