import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../../features/auth/pages/LoginPage';
import { HomePage } from '../../pages/HomePage';
import { LandingPage } from '../../pages/LandingPage';
import { AuthGuard } from './guards/AuthGuard';
import { RoleGuard } from './guards/RoleGuard';
import { CreatePublicTicketPage } from '../../features/public/pages/CreatePublicTicketPage';
import { CreateEmployeeTicketPage } from '../../features/tickets/pages/CreateEmployeeTicketPage';
import { TicketDetailPage } from '../../features/tickets/pages/TicketDetailPage';
import { MyAssignmentsPage } from '../../features/tickets/pages/MyAssignmentsPage';
import { DepartmentPoolPage } from '../../features/tickets/pages/DepartmentPoolPage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/public/tickets/create" element={<CreatePublicTicketPage />} />
      <Route
        path="/tickets/create"
        element={
          <AuthGuard>
            <RoleGuard allow={["ROLE_USER", "ROLE_ADMIN"] as any}>
              <CreateEmployeeTicketPage />
            </RoleGuard>
          </AuthGuard>
        }
      />
      <Route
        path="/tickets/:id"
        element={
          <AuthGuard>
            <RoleGuard allow={["ROLE_USER", "ROLE_ADMIN"] as any}>
              <TicketDetailPage />
            </RoleGuard>
          </AuthGuard>
        }
      />
      <Route
        path="/tickets/my"
        element={
          <AuthGuard>
            <RoleGuard allow={["ROLE_USER", "ROLE_ADMIN"] as any}>
              <MyAssignmentsPage />
            </RoleGuard>
          </AuthGuard>
        }
      />
      <Route
        path="/tickets/pool"
        element={
          <AuthGuard>
            <RoleGuard allow={["ROLE_USER", "ROLE_ADMIN"] as any}>
              <DepartmentPoolPage />
            </RoleGuard>
          </AuthGuard>
        }
      />
      <Route path="/" element={<LandingPage />} />
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


