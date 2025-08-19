import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../../../shared/stores/auth.store';

export const PublicOnlyGuard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  if (isLoggedIn) return <Navigate to="/tickets/create" replace />;
  return <>{children}</>;
};


