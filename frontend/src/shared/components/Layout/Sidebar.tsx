import React from 'react';
import { Box, Divider, Drawer, List, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography } from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';
import InventoryIcon from '@mui/icons-material/Inventory';
import { useLocation, useNavigate } from 'react-router-dom';

export const Sidebar: React.FC<{ mobileOpen?: boolean; onClose?: () => void; width?: number }> = ({ mobileOpen = false, onClose, width = 240 }) => {
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const items = [
    { label: 'Ticket Oluştur', path: '/tickets/create' },
    { label: 'Benim Üstlendiklerim', path: '/tickets/my' },
    { label: 'Departman Havuzu', path: '/tickets/pool' },
  ];

  const content = (
    <>
      <Toolbar>
        <Typography variant="subtitle1" sx={{ fontWeight: 700, color: '#fff' }}>CRM Portal</Typography>
      </Toolbar>
      <Divider sx={{ borderColor: 'rgba(255,255,255,0.2)' }} />
      <List sx={{ color: '#fff' }}>
        {items.map((it) => (
          <ListItemButton
            key={it.path}
            selected={pathname.startsWith(it.path)}
            onClick={() => { navigate(it.path); onClose?.(); }}
            sx={{
              '&.Mui-selected': { backgroundColor: 'rgba(255,255,255,0.15)' },
              '&:hover': { backgroundColor: 'rgba(255,255,255,0.1)' },
              color: '#fff',
            }}
          >
            <ListItemIcon sx={{ color: '#fff', minWidth: 36 }}>
              {it.path.includes('/create') ? <AddCircleOutlineIcon /> : it.path.includes('/my') ? <AssignmentIndIcon /> : <InventoryIcon />}
            </ListItemIcon>
            <ListItemText primary={it.label} />
          </ListItemButton>
        ))}
      </List>
    </>
  );

  return (
    <>
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', md: 'block' },
          '& .MuiDrawer-paper': { width, boxSizing: 'border-box', backgroundColor: 'primary.main', border: 'none' },
        }}
        open
      >
        {content}
      </Drawer>
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={onClose}
        ModalProps={{ keepMounted: true }}
        sx={{ display: { xs: 'block', md: 'none' }, '& .MuiDrawer-paper': { width, backgroundColor: 'primary.main', color: '#fff' } }}
      >
        {content}
      </Drawer>
    </>
  );
};


