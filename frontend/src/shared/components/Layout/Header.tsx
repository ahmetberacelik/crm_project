import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, Stack } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { logout } from '../../../features/auth/api/auth.api';
import { useAuthStore } from '../../../shared/stores/auth.store';

export const Header: React.FC = () => {
  const navigate = useNavigate();
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());

  const handleLogout = async () => {
    await logout();
    navigate('/', { replace: true });
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1, cursor: 'pointer' }} onClick={() => navigate('/')}>CRM</Typography>
        <Stack direction="row" spacing={1}>
          {!isLoggedIn && (
            <Button color="inherit" onClick={() => navigate('/public/tickets/create')}>Public Ticket</Button>
          )}
          {isLoggedIn && (
            <>
              <Button color="inherit" onClick={() => navigate('/tickets/create')}>Çalışan Ticket</Button>
              <Button color="inherit" onClick={() => navigate('/tickets/my')}>Benim Üstlendiklerim</Button>
              <Button color="inherit" onClick={() => navigate('/tickets/pool')}>Departman Havuzu</Button>
            </>
          )}
        </Stack>
        <Box sx={{ ml: 2 }}>
          {!isLoggedIn ? (
            <Button color="inherit" onClick={() => navigate('/login')}>Giriş Yap</Button>
          ) : (
            <Button color="inherit" onClick={handleLogout}>Çıkış Yap</Button>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};


