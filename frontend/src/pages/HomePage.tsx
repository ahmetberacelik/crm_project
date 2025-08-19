import React from 'react';
import { AppLayout } from '../shared/components/Layout/AppLayout';
import { Button, Stack, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../shared/stores/auth.store';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  return (
    <AppLayout>
      <Stack spacing={2}></Stack>
    </AppLayout>
  );
};


