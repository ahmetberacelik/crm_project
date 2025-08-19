import React from 'react';
import { Container, Paper } from '@mui/material';
import { PublicTicketForm } from '../components/PublicTicketForm';

export const CreatePublicTicketPage: React.FC = () => {
  return (
    <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', py: 6 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <PublicTicketForm />
      </Paper>
    </Container>
  );
};


