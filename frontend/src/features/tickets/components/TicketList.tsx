import React from 'react';
import { Card, CardActionArea, CardContent, Chip, Stack, Typography } from '@mui/material';
import type { TicketResponse } from '../api/tickets.api';
import { useNavigate } from 'react-router-dom';

export const TicketList: React.FC<{ items: TicketResponse[] }> = ({ items }) => {
  const navigate = useNavigate();
  if (!items?.length) return <Typography>Liste boş</Typography>;
  return (
    <Stack spacing={2}>
      {items.map(t => (
        <Card key={t.id}>
          <CardActionArea onClick={() => navigate(`/tickets/${t.id}`)}>
            <CardContent>
              <Stack spacing={0.5}>
                <Typography variant="subtitle1">#{t.id} • {t.issue.slice(0, 80)}</Typography>
                <Stack direction="row" spacing={1}>
                  <Chip label={t.priority} size="small" />
                  <Chip label={t.isActive ? 'AKTİF' : 'PASİF'} size="small" color={t.isActive ? 'success' : 'default'} />
                </Stack>
              </Stack>
            </CardContent>
          </CardActionArea>
        </Card>
      ))}
    </Stack>
  );
};


