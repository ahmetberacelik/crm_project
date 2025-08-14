import React from 'react';
import { Container, Box } from '@mui/material';
import { Header } from './Header';

export const AppLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <Box>
      <Header />
      <Container sx={{ py: 3 }}>{children}</Container>
    </Box>
  );
};


