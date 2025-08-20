import React from 'react';
import { useAuthStore } from '../../../shared/stores/auth.store';
import { useQuery } from '@tanstack/react-query';
import { getDepartmentById, getPersonById } from '../api/profile.api';
import { Alert, Card, CardContent, Stack, Typography, Avatar, Grid, Chip } from '@mui/material';

export const ProfilePage: React.FC = () => {
  const userId = useAuthStore((s) => s.userId);
  const personId = Number(userId);

  const personQ = useQuery({ queryKey: ['person', personId], queryFn: () => getPersonById(personId), enabled: Number.isFinite(personId) });
  const deptQ = useQuery({
    queryKey: ['department', personQ.data?.departmentId],
    queryFn: () => getDepartmentById(personQ.data!.departmentId),
    enabled: Boolean(personQ.data?.departmentId),
  });

  if (!userId) return <Alert severity="info">Giriş yapınız.</Alert>;
  if (personQ.isLoading) return <div>Yükleniyor...</div>;
  if (personQ.error) return <Alert severity="error">Kullanıcı bilgileri yüklenemedi.</Alert>;

  const p = personQ.data!;
  const initials = `${p.name?.[0] ?? ''}${p.surname?.[0] ?? ''}`.toUpperCase();
  return (
    <Card>
      <CardContent>
        <Grid container spacing={2} alignItems="center">
          <Grid item>
            <Avatar sx={{ width: 72, height: 72, bgcolor: 'primary.main' }}>{initials || 'U'}</Avatar>
          </Grid>
          <Grid item xs>
            <Typography variant="h6">{p.name} {p.surname}</Typography>
            <Stack direction="row" spacing={1} sx={{ mt: 0.5 }}>
              <Chip label={deptQ.data?.name ?? 'Departman'} size="small" />
              <Chip label={`ID: ${p.personId}`} size="small" variant="outlined" />
            </Stack>
          </Grid>
        </Grid>

        <Stack spacing={0.5} sx={{ mt: 2 }}>
          <Typography color="text.secondary">E-posta</Typography>
          <Typography>{p.email}</Typography>
          {p.phone && (
            <>
              <Typography color="text.secondary" sx={{ mt: 1 }}>Telefon</Typography>
              <Typography>{p.phone}</Typography>
            </>
          )}
        </Stack>
      </CardContent>
    </Card>
  );
};


