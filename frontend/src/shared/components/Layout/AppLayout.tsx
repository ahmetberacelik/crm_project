import React from 'react';
import { Container, Box, Stack } from '@mui/material';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

export const AppLayout: React.FC<{ children: React.ReactNode; hideSidebar?: boolean }> = ({ children, hideSidebar = false }) => {
  const [mobileOpen, setMobileOpen] = React.useState(false);
  return (
    <Box>
      <Header onMenuClick={() => setMobileOpen(true)} />
      <Box sx={{ display: 'flex' }}>
        {!hideSidebar && (
          <Sidebar mobileOpen={mobileOpen} onClose={() => setMobileOpen(false)} />
        )}
        <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
          <Container maxWidth="lg">{children}</Container>
        </Box>
      </Box>
    </Box>
  );
};


