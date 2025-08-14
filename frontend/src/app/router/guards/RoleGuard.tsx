import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../../../shared/stores/auth.store';
import type { Role } from '../../../shared/types/auth';

interface Props {
  children: React.ReactNode;
  allow: Role[];
  fallbackTo?: string; // default '/'
}

export const RoleGuard: React.FC<Props> = ({ children, allow, fallbackTo = '/' }) => {
  const roles = useAuthStore((s) => s.roles);
  const allowed = roles.some((r) => allow.includes(r));
  if (!allowed) return <Navigate to={fallbackTo} replace />;
  return <>{children}</>;
};


