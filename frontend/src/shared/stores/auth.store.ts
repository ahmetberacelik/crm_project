import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { jwtDecode } from 'jwt-decode';
import type { AuthState, Role } from '../types/auth';

interface AuthActions {
  setTokens: (tokens: { accessToken: string; refreshToken: string }) => void;
  clear: () => void;
  isLoggedIn: () => boolean;
}

type Store = AuthState & AuthActions;

type JwtPayload = {
  sub?: string; // userId as string
  roles?: string; // comma-separated roles, e.g., "ROLE_USER,ROLE_ADMIN"
};

function parseRoles(rolesString?: string): Role[] {
  if (!rolesString) return [];
  return rolesString
    .split(',')
    .map((r) => r.trim())
    .filter((r): r is Role => r === 'ROLE_USER' || r === 'ROLE_ADMIN');
}

export const useAuthStore = create<Store>()(
  persist(
    (set, get) => ({
      accessToken: null,
      refreshToken: null,
      userId: null,
      roles: [],

      setTokens: ({ accessToken, refreshToken }) => {
        let userId: string | null = null;
        let roles: Role[] = [];
        try {
          const payload = jwtDecode<JwtPayload>(accessToken);
          userId = payload.sub ?? null;
          roles = parseRoles(payload.roles);
        } catch {
          // If decode fails, keep minimal state to allow refresh flow to recover
          userId = null;
          roles = [];
        }
        set({ accessToken, refreshToken, userId, roles });
      },

      clear: () => set({ accessToken: null, refreshToken: null, userId: null, roles: [] }),

      isLoggedIn: () => Boolean(get().accessToken),
    }),
    {
      name: 'auth-store',
      version: 1,
      partialize: (state) => ({
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        userId: state.userId,
        roles: state.roles,
      }),
    }
  )
);


