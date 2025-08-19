import React from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getTicketById } from '../api/tickets.api';
import { Alert, Card, CardContent, Chip, Stack, Typography } from '@mui/material';

export const TicketDetailPage: React.FC = () => {
  const params = useParams();
  const id = Number(params.id);

  const { data, isLoading, error } = useQuery({
    queryKey: ['ticket', id],
    queryFn: () => getTicketById(id),
    enabled: Number.isFinite(id),
  });

  if (!Number.isFinite(id)) return <Alert severity="error">Geçersiz ID</Alert>;
  if (isLoading) return <div>Yükleniyor...</div>;
  if (error) return <Alert severity="error">Ticket yüklenemedi.</Alert>;
  if (!data) return <Alert severity="warning">Kayıt bulunamadı.</Alert>;

  return (
    <Card>
      <CardContent>
        <Stack spacing={1}>
          <Typography variant="h6">Ticket #{data.id}</Typography>
          <Typography><b>Öncelik:</b> {data.priority}</Typography>
          <Typography><b>Durum:</b> {data.isActive ? 'Aktif' : 'Pasif'}</Typography>
          <Typography><b>Oluşturulma:</b> {data.createdDate}</Typography>
          {data.creator && (
            <Typography><b>Oluşturan:</b> {data.creator.name} {data.creator.surname} ({data.creator.email})</Typography>
          )}
          <Typography sx={{ mt: 1 }}><b>Açıklama:</b> {data.issue}</Typography>

          <Typography sx={{ mt: 2 }}><b>Atamalar:</b></Typography>
          <Stack direction="row" spacing={1} flexWrap="wrap">
            {data.assignments?.map(a => (
              <Chip key={a.assignmentId} label={`${a.status} • ${a.assignee?.name ?? 'N/A'}`} />
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
};


