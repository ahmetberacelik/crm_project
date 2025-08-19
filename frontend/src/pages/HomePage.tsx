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
      <Stack spacing={2}>
        <Typography variant="h5">Hoş geldiniz</Typography>
        {!isLoggedIn ? (
          <Stack direction="row" spacing={1}>
            <Button variant="contained" onClick={() => navigate('/public/tickets/create')}>Public Ticket Oluştur</Button>
            <Button variant="outlined" onClick={() => navigate('/login')}>Giriş Yap</Button>
          </Stack>
        ) : (
          <Stack direction="row" spacing={1}>
            <Button variant="contained" onClick={() => navigate('/tickets/create')}>Çalışan Ticket Oluştur</Button>
            <Button variant="outlined" onClick={() => navigate('/tickets/my')}>Benim Üstlendiklerim</Button>
            <Button variant="outlined" onClick={() => navigate('/tickets/pool')}>Departman Havuzu</Button>
          </Stack>
        )}
      </Stack>
    </AppLayout>
  );
};


