import React from 'react';
import { Container, Paper } from '@mui/material';
import { PublicTicketForm } from '../components/PublicTicketForm';
import { AppLayout } from '../../../shared/components/Layout/AppLayout';
import { useAuthStore } from '../../../shared/stores/auth.store';

export const CreatePublicTicketPage: React.FC = () => {
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  return (
    <AppLayout hideSidebar>
      <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', py: 6 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <PublicTicketForm />
        </Paper>
      </Container>
    </AppLayout>
  );
};


