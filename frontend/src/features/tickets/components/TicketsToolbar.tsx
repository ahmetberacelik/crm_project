import React from 'react';
import { Box, Button, Chip, Paper, Stack, TextField, ToggleButton, ToggleButtonGroup, Typography } from '@mui/material';

export const TicketsToolbar: React.FC<{ title: string; onNew?: () => void; }> = ({ title, onNew }) => {
  const [status, setStatus] = React.useState<string | null>('ALL');
  const [category, setCategory] = React.useState<string | null>('ALL');
  return (
    <Paper sx={{ p: 2, mb: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
      <Typography variant="h6" sx={{ flexGrow: 1 }}>{title}</Typography>
      <ToggleButtonGroup size="small" value={status} exclusive onChange={(_, v) => setStatus(v)}>
        <ToggleButton value="ALL">Durum: Tümü</ToggleButton>
        <ToggleButton value="OPEN">Açık</ToggleButton>
        <ToggleButton value="CLOSED">Kapalı</ToggleButton>
      </ToggleButtonGroup>
      <ToggleButtonGroup size="small" value={category} exclusive onChange={(_, v) => setCategory(v)}>
        <ToggleButton value="ALL">Kategori: Tümü</ToggleButton>
        <ToggleButton value="IT">IT</ToggleButton>
        <ToggleButton value="HR">İK</ToggleButton>
      </ToggleButtonGroup>
      <TextField size="small" placeholder="Ara: konu veya ID" />
      {onNew && <Button variant="contained" onClick={onNew}>Yeni Ticket</Button>}
    </Paper>
  );
};


