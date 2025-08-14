import React from 'react';
import { Container, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { LoginForm } from '../components/LoginForm';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <LoginForm onSuccess={() => navigate('/')} />
      </Paper>
    </Container>
  );
};


