export type Role = 'ROLE_USER' | 'ROLE_ADMIN';

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userId: string | null; // JWT sub (personId == userId)
  roles: Role[]; // Derived from JWT 'roles' claim
}


