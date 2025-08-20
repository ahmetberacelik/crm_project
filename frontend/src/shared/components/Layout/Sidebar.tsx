import React from 'react';
import { Box, Divider, Drawer, List, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography } from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';
import InventoryIcon from '@mui/icons-material/Inventory';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import CategoryIcon from '@mui/icons-material/Category';
import ApartmentIcon from '@mui/icons-material/Apartment';
import PeopleAltIcon from '@mui/icons-material/PeopleAlt';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../../shared/stores/auth.store';

export const Sidebar: React.FC<{ mobileOpen?: boolean; onClose?: () => void; width?: number }> = ({ mobileOpen = false, onClose, width = 240 }) => {
	const navigate = useNavigate();
	const { pathname } = useLocation();
	const roles = useAuthStore((s) => s.roles);

	const items = [
		{ label: 'Ticket Oluştur', view: 'create' },
		{ label: 'Benim Üstlendiklerim', view: 'my' },
		{ label: 'Departman Havuzu', view: 'pool' },
	];

	const isAdmin = roles.includes('ROLE_ADMIN');

	const content = (
		<>
			<Toolbar>
				<Typography variant="subtitle1" sx={{ fontWeight: 700, color: '#fff' }}>CRM Portal</Typography>
			</Toolbar>
			<Divider sx={{ borderColor: 'rgba(255,255,255,0.2)' }} />
			<List sx={{ color: '#fff' }}>
				{items.map((it) => (
					<ListItemButton
						key={it.view}
						selected={new URLSearchParams(location.search).get('view') === it.view && pathname === '/'}
						onClick={() => { navigate({ pathname: '/', search: `?view=${it.view}` }); onClose?.(); }}
						sx={{
							'&.Mui-selected': { backgroundColor: 'rgba(255,255,255,0.15)' },
							'&:hover': { backgroundColor: 'rgba(255,255,255,0.1)' },
							color: '#fff',
						}}
					>
						<ListItemIcon sx={{ color: '#fff', minWidth: 36 }}>
							{it.view === 'create' ? <AddCircleOutlineIcon /> : it.view === 'my' ? <AssignmentIndIcon /> : <InventoryIcon />}
						</ListItemIcon>
						<ListItemText primary={it.label} />
					</ListItemButton>
				))}
			</List>
			{isAdmin && (
				<>
					<Divider sx={{ borderColor: 'rgba(255,255,255,0.2)', my: 1 }} />
					<Toolbar sx={{ minHeight: 40 }}>
						<AdminPanelSettingsIcon sx={{ color: '#fff', mr: 1 }} />
						<Typography variant="body2" sx={{ color: '#fff', opacity: 0.9 }}>Admin</Typography>
					</Toolbar>
					<List sx={{ color: '#fff' }}>
						<ListItemButton onClick={() => { navigate({ pathname: '/', search: '?view=admin-categories' }); onClose?.(); }}>
							<ListItemIcon sx={{ color: '#fff', minWidth: 36 }}><CategoryIcon /></ListItemIcon>
							<ListItemText primary="Kategoriler" />
						</ListItemButton>
						<ListItemButton onClick={() => { navigate({ pathname: '/', search: '?view=admin-departments' }); onClose?.(); }}>
							<ListItemIcon sx={{ color: '#fff', minWidth: 36 }}><ApartmentIcon /></ListItemIcon>
							<ListItemText primary="Departmanlar" />
						</ListItemButton>
						<ListItemButton onClick={() => { navigate({ pathname: '/', search: '?view=admin-users' }); onClose?.(); }}>
							<ListItemIcon sx={{ color: '#fff', minWidth: 36 }}><PeopleAltIcon /></ListItemIcon>
							<ListItemText primary="Kullanıcılar" />
						</ListItemButton>
					</List>
				</>
			)}
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


