import React from 'react';
import { AppLayout } from '../shared/components/Layout/AppLayout';
import { Stack } from '@mui/material';
import { useLocation } from 'react-router-dom';
import { useAuthStore } from '../shared/stores/auth.store';
import { CreateEmployeeTicketPage } from '../features/tickets/pages/CreateEmployeeTicketPage';
import { MyAssignmentsPage } from '../features/tickets/pages/MyAssignmentsPage';
import { DepartmentPoolPage } from '../features/tickets/pages/DepartmentPoolPage';
import { AdminCategoriesPage } from '../features/admin/categories/pages/AdminCategoriesPage';
import { AdminDepartmentsPage } from '../features/admin/departments/pages/AdminDepartmentsPage';
import { AdminUsersPage } from '../features/admin/users/pages/AdminUsersPage';

export const HomePage: React.FC = () => {
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  const { search } = useLocation();
  const view = new URLSearchParams(search).get('view');

  const Content = () => {
    if (!isLoggedIn) return <></>;
    switch (view) {
      case 'create':
        return <CreateEmployeeTicketPage />;
      case 'my':
        return <MyAssignmentsPage />;
      case 'pool':
        return <DepartmentPoolPage />;
      case 'admin-categories':
        return <AdminCategoriesPage />;
      case 'admin-departments':
        return <AdminDepartmentsPage />;
      case 'admin-users':
        return <AdminUsersPage />;
      default:
        return <></>;
    }
  };

  return (
    <AppLayout>
      <Stack spacing={2}>
        <Content />
      </Stack>
    </AppLayout>
  );
};


