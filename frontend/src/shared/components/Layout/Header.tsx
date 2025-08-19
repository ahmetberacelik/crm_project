import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { logout } from '../../../features/auth/api/auth.api';
import { useAuthStore } from '../../../shared/stores/auth.store';

export const Header: React.FC = () => {
  const navigate = useNavigate();
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          CRM
        </Typography>
        <Box>
          {isLoggedIn && (
            <Button color="inherit" onClick={handleLogout}>
              Çıkış Yap
            </Button>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};


