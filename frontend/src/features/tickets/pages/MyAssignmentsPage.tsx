import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { getMyAssignments } from '../api/tickets.api';
import { Alert, Pagination, Stack, Typography } from '@mui/material';
import { TicketsTable } from '../components/TicketsTable';

export const MyAssignmentsPage: React.FC = () => {
  const [page, setPage] = React.useState(0);
  const { data, isLoading, error } = useQuery({
    queryKey: ['my-assignments', page],
    queryFn: () => getMyAssignments({ page, size: 20, sort: 'createdDate,desc' }),
  });

  if (isLoading) return <div>Yükleniyor...</div>;
  if (error) return <Alert severity="error">Liste yüklenemedi.</Alert>;

  return (
    <Stack spacing={2}>
      <Typography variant="h6">Benimkiler</Typography>
      <TicketsTable items={data ?? []} />
      <Pagination page={page + 1} onChange={(_, p) => setPage(p - 1)} count={10} />
    </Stack>
  );
};


