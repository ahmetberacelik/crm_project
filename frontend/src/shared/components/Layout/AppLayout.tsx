import React from 'react';
import { Container, Box, Stack } from '@mui/material';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

export const AppLayout: React.FC<{ children: React.ReactNode; hideSidebar?: boolean }> = ({ children, hideSidebar = false }) => {
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const SIDEBAR_WIDTH = 240;
  return (
    <Box>
      <Header onMenuClick={() => setMobileOpen(true)} />
      <Box sx={{ display: 'flex' }}>
        {!hideSidebar && (
          <Sidebar width={SIDEBAR_WIDTH} mobileOpen={mobileOpen} onClose={() => setMobileOpen(false)} />
        )}
        <Box component="main" sx={{ flexGrow: 1, p: 3, ml: hideSidebar ? 0 : { xs: 0, md: `${SIDEBAR_WIDTH}px` } }}>
          <Container maxWidth={false} disableGutters>{children}</Container>
        </Box>
      </Box>
    </Box>
  );
};


