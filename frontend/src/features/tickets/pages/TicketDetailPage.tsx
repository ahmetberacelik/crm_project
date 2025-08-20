import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { claimTicket, closeTicket, getTicketById, reassignTicket } from '../api/tickets.api';
import { Alert, Card, CardActions, CardContent, Chip, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField, Button, Typography, Autocomplete } from '@mui/material';
import { useQuery as useRQ } from '@tanstack/react-query';
import { getDepartmentsTree, type DepartmentItem } from '../../departments/api/departments.api';

export const TicketDetailPage: React.FC = () => {
  const params = useParams();
  const id = Number(params.id);

  const { data, isLoading, error } = useQuery({
    queryKey: ['ticket', id],
    queryFn: () => getTicketById(id),
    enabled: Number.isFinite(id),
  });

  const qc = useQueryClient();
  const [openReassign, setOpenReassign] = React.useState(false);
  const [deptId, setDeptId] = React.useState<string>('');
  const deptsQ = useRQ({ queryKey: ['departments'], queryFn: getDepartmentsTree });
  const navigate = useNavigate();

  const claimMut = useMutation({
    mutationFn: () => claimTicket(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['ticket', id] }),
  });
  const closeMut = useMutation({
    mutationFn: () => closeTicket(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['ticket', id] }),
  });
  const reassignMut = useMutation({
    mutationFn: () => reassignTicket(id, Number(deptId)),
    onSuccess: () => { setOpenReassign(false); setDeptId(''); qc.invalidateQueries({ queryKey: ['ticket', id] }); },
  });

  if (!Number.isFinite(id)) return <Alert severity="error">Geçersiz ID</Alert>;
  if (isLoading) return <div>Yükleniyor...</div>;
  if (error) return <Alert severity="error">Ticket yüklenemedi.</Alert>;
  if (!data) return <Alert severity="warning">Kayıt bulunamadı.</Alert>;

  return (
    <>
    <Card>
      <CardActions sx={{ px: 2, pt: 2, justifyContent: 'space-between' }}>
        <Button size="small" onClick={() => navigate(-1)}>Geri</Button>
        <Button size="small" onClick={() => navigate('/')}>Ana Menü</Button>
      </CardActions>
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
      <CardActions sx={{ px: 2, pb: 2 }}>
        <Button variant="contained" size="small" onClick={() => claimMut.mutate()} disabled={claimMut.isPending}>Üstlen</Button>
        <Button variant="outlined" size="small" onClick={() => closeMut.mutate()} disabled={closeMut.isPending}>Kapat</Button>
        <Button variant="text" size="small" onClick={() => setOpenReassign(true)}>Yönlendir</Button>
      </CardActions>
    </Card>

    <Dialog open={openReassign} onClose={() => setOpenReassign(false)}>
      <DialogTitle>Departmana Yönlendir</DialogTitle>
      <DialogContent>
        <Autocomplete
          options={deptsQ.data ?? []}
          getOptionLabel={(o: DepartmentItem) => o.name}
          loading={deptsQ.isLoading}
          onChange={(_, val) => setDeptId(val ? String(val.departmentId) : '')}
          renderInput={(params) => <TextField {...params} label="Yeni Departman" sx={{ mt: 1 }} fullWidth />} />
      </DialogContent>
      <DialogActions>
        <Button onClick={() => setOpenReassign(false)}>İptal</Button>
        <Button onClick={() => reassignMut.mutate()} disabled={!deptId || reassignMut.isPending}>Gönder</Button>
      </DialogActions>
    </Dialog>
    </>
  );
};


