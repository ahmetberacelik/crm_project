import React from 'react';
import { Container, Paper } from '@mui/material';
import { PublicTicketForm } from '../components/PublicTicketForm';
import { AppLayout } from '../../../shared/components/Layout/AppLayout';

export const CreatePublicTicketPage: React.FC = () => {
  return (
    <AppLayout>
      <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', py: 6 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <PublicTicketForm />
        </Paper>
      </Container>
    </AppLayout>
  );
};


