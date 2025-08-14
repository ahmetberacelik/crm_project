import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../../features/auth/pages/LoginPage';
import { HomePage } from '../../pages/HomePage';
import { AuthGuard } from './guards/AuthGuard';
import { RoleGuard } from './guards/RoleGuard';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<HomePage />} />
      {/* Örnek korumalı rota (ileride sayfa eklenecek) */}
      <Route
        path="/protected"
        element={
          <AuthGuard>
            <RoleGuard allow={["ROLE_USER", "ROLE_ADMIN"] as any}>
              <div>Protected content</div>
            </RoleGuard>
          </AuthGuard>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};


