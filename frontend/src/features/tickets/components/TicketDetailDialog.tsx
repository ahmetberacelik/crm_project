import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { claimTicket, closeTicket, getTicketById, reassignTicket } from '../api/tickets.api';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Stack, Typography, Chip, TextField } from '@mui/material';
import { useQuery as useRQ } from '@tanstack/react-query';
import { Autocomplete } from '@mui/material';
import { getDepartmentsTree, type DepartmentItem } from '../../departments/api/departments.api';

export const TicketDetailDialog: React.FC<{ open: boolean; onClose: () => void; ticketId: number | null }> = ({ open, onClose, ticketId }) => {
  const qc = useQueryClient();
  const { data, isLoading, error } = useQuery({
    queryKey: ['ticket', ticketId],
    queryFn: () => getTicketById(ticketId as number),
    enabled: open && typeof ticketId === 'number',
  });
  const [deptId, setDeptId] = React.useState<string>('');
  const deptsQ = useRQ({ queryKey: ['departments'], queryFn: getDepartmentsTree, enabled: open });

  const claimMut = useMutation({ mutationFn: () => claimTicket(ticketId as number), onSuccess: () => qc.invalidateQueries({ queryKey: ['ticket', ticketId] }) });
  const closeMut = useMutation({ mutationFn: () => closeTicket(ticketId as number), onSuccess: () => qc.invalidateQueries({ queryKey: ['ticket', ticketId] }) });
  const reassignMut = useMutation({ mutationFn: () => reassignTicket(ticketId as number, Number(deptId)), onSuccess: () => { setDeptId(''); qc.invalidateQueries({ queryKey: ['ticket', ticketId] }); } });

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>Ticket Detayı</DialogTitle>
      <DialogContent>
        {isLoading && <div>Yükleniyor...</div>}
        {error && <div>Hata oluştu.</div>}
        {data && (
          <Stack spacing={1} sx={{ mt: 1 }}>
            <Typography variant="subtitle1">#{data.id} • {data.issue}</Typography>
            <Typography><b>Öncelik:</b> {data.priority}</Typography>
            <Typography><b>Durum:</b> {data.isActive ? 'Aktif' : 'Pasif'}</Typography>
            <Typography><b>Oluşturulma:</b> {data.createdDate}</Typography>
            {data.creator && <Typography><b>Oluşturan:</b> {data.creator.name} {data.creator.surname} ({data.creator.email})</Typography>}
            <Typography sx={{ mt: 1 }}><b>Atamalar:</b></Typography>
            <Stack direction="row" spacing={1} flexWrap="wrap">
              {data.assignments?.map(a => (
                <Chip key={a.assignmentId} label={`${a.status} • ${a.assignee?.name ?? 'N/A'}`} />
              ))}
            </Stack>
            <Autocomplete options={deptsQ.data ?? []} getOptionLabel={(o: DepartmentItem) => o.name} loading={deptsQ.isLoading} onChange={(_, val) => setDeptId(val ? String(val.departmentId) : '')} renderInput={(params) => <TextField {...params} label="Yeni Departman" />} />
          </Stack>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Kapat</Button>
        <Button onClick={() => claimMut.mutate()} disabled={!ticketId || claimMut.isPending}>Üstlen</Button>
        <Button onClick={() => closeMut.mutate()} disabled={!ticketId || closeMut.isPending}>Kapat</Button>
        <Button onClick={() => reassignMut.mutate()} disabled={!ticketId || !deptId || reassignMut.isPending}>Yönlendir</Button>
      </DialogActions>
    </Dialog>
  );
};


