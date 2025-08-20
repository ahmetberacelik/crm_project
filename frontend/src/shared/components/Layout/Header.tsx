import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, Stack, IconButton, Avatar, Menu, MenuItem, Tooltip } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { getPersonById } from '../../../features/profile/api/profile.api';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';
import { logout } from '../../../features/auth/api/auth.api';
import { useAuthStore } from '../../../shared/stores/auth.store';

export const Header: React.FC<{ onMenuClick?: () => void }> = ({ onMenuClick }) => {
  const navigate = useNavigate();
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const userId = useAuthStore((s) => s.userId);
  const personId = Number(userId);
  const personQ = useQuery({
    queryKey: ['me', personId],
    queryFn: () => getPersonById(personId),
    enabled: isLoggedIn && Number.isFinite(personId),
  });

  const handleLogout = async () => {
    await logout();
    navigate('/', { replace: true });
  };

  const handleOpenMenu = (e: React.MouseEvent<HTMLElement>) => setAnchorEl(e.currentTarget);
  const handleCloseMenu = () => setAnchorEl(null);

  return (
    <AppBar position="static">
      <Toolbar>
        <IconButton color="inherit" sx={{ mr: 1, display: { xs: 'inline-flex', md: 'none' } }} onClick={onMenuClick}>
          <MenuIcon />
        </IconButton>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1, cursor: 'pointer' }} onClick={() => navigate('/')}>CRM</Typography>
        <Stack direction="row" spacing={1}></Stack>
        <Box sx={{ ml: 2 }}>
          {!isLoggedIn ? (
            <Button color="inherit" onClick={() => navigate('/login')}>Giriş Yap</Button>
          ) : (
            <>
              <Tooltip title="Hesap">
                <IconButton onClick={handleOpenMenu} size="small" sx={{ p: 0 }}>
                  <Avatar sx={{ width: 36, height: 36, bgcolor: 'primary.main' }}>
                    {(personQ.data && `${personQ.data.name?.[0] ?? ''}${personQ.data.surname?.[0] ?? ''}`.toUpperCase()) || 'U'}
                  </Avatar>
                </IconButton>
              </Tooltip>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleCloseMenu}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                transformOrigin={{ vertical: 'top', horizontal: 'right' }}
              >
                <MenuItem onClick={() => { handleCloseMenu(); navigate('/profile'); }}>Profilim</MenuItem>
                <MenuItem onClick={() => { handleCloseMenu(); handleLogout(); }}>Çıkış Yap</MenuItem>
              </Menu>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};


