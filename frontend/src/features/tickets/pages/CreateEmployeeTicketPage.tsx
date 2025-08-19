import React from 'react';
import { Container, Paper } from '@mui/material';
import { TicketForm } from '../components/TicketForm';

export const CreateEmployeeTicketPage: React.FC = () => {
  return (
    <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', py: 6 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <TicketForm />
      </Paper>
    </Container>
  );
};



