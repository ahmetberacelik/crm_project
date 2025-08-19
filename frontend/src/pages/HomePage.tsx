import React from 'react';
import { AppLayout } from '../shared/components/Layout/AppLayout';
import { Stack } from '@mui/material';
import { useLocation } from 'react-router-dom';
import { useAuthStore } from '../shared/stores/auth.store';
import { CreateEmployeeTicketPage } from '../features/tickets/pages/CreateEmployeeTicketPage';
import { MyAssignmentsPage } from '../features/tickets/pages/MyAssignmentsPage';
import { DepartmentPoolPage } from '../features/tickets/pages/DepartmentPoolPage';

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


