import React from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { createDepartment, deleteDepartment, listDepartments, updateDepartment } from '../api/departments.api';
import { Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Paper, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

export const AdminDepartmentsPage: React.FC = () => {
  const qc = useQueryClient();
  const listQ = useQuery({ queryKey: ['admin','departments'], queryFn: listDepartments });

  const [open, setOpen] = React.useState<null | 'create' | { id: number; name: string; parentDepartmentId?: number | null }>(null);
  const [form, setForm] = React.useState<{ name: string; parentDepartmentId?: string }>({ name: '', parentDepartmentId: '' });
  const [info, setInfo] = React.useState<string | null>(null);

  const createMut = useMutation({
    mutationFn: () => createDepartment({ name: form.name, parentDepartmentId: form.parentDepartmentId ? Number(form.parentDepartmentId) : null }),
    onSuccess: () => { setOpen(null); setForm({ name: '', parentDepartmentId: '' }); qc.invalidateQueries({ queryKey: ['admin','departments'] }); },
    onError: (e: any) => setInfo(e?.response?.data?.message || 'İşlem başarısız'),
  });
  const updateMut = useMutation({
    mutationFn: () => updateDepartment((open as any).id, { name: form.name, parentDepartmentId: form.parentDepartmentId ? Number(form.parentDepartmentId) : null }),
    onSuccess: () => { setOpen(null); setForm({ name: '', parentDepartmentId: '' }); qc.invalidateQueries({ queryKey: ['admin','departments'] }); },
    onError: (e: any) => setInfo(e?.response?.data?.message || 'İşlem başarısız'),
  });
  const deleteMut = useMutation({
    mutationFn: (id: number) => deleteDepartment(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin','departments'] }),
  });

  if (listQ.isLoading) return <div>Yükleniyor...</div>;
  if (listQ.error) return <Alert severity="error">Departmanlar yüklenemedi.</Alert>;

  const startEdit = (d: { id: number; name: string; parentDepartmentId?: number | null }) => {
    setOpen({ id: d.id, name: d.name, parentDepartmentId: d.parentDepartmentId ?? undefined });
    setForm({ name: d.name, parentDepartmentId: d.parentDepartmentId ? String(d.parentDepartmentId) : '' });
  };

  return (
    <Stack spacing={2}>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="h6">Departmanlar</Typography>
        <Button startIcon={<AddIcon />} variant="contained" onClick={() => { setOpen('create'); setForm({ name: '', parentDepartmentId: '' }); }}>Yeni Departman</Button>
      </Stack>

      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Ad</TableCell>
              <TableCell>Üst Departman</TableCell>
              <TableCell align="right">İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {listQ.data?.map((d) => (
              <TableRow key={d.departmentId} hover>
                <TableCell>#{d.departmentId}</TableCell>
                <TableCell>{d.name}</TableCell>
                <TableCell>{d.parentDepartmentId ?? '-'}</TableCell>
                <TableCell align="right">
                  <IconButton size="small" onClick={() => startEdit({ id: d.departmentId, name: d.name, parentDepartmentId: d.parentDepartmentId })}><EditIcon fontSize="small" /></IconButton>
                  <IconButton size="small" color="error" onClick={() => deleteMut.mutate(d.departmentId)}><DeleteIcon fontSize="small" /></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={Boolean(open)} onClose={() => setOpen(null)} fullWidth maxWidth="sm">
        <DialogTitle>{open === 'create' ? 'Yeni Departman' : 'Departman Güncelle'}</DialogTitle>
        <DialogContent>
          {info && <Alert severity="error" sx={{ mb: 2 }}>{info}</Alert>}
          <Stack spacing={2} sx={{ mt: 1 }}>
            <TextField label="Ad" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            <TextField label="Üst Departman ID" value={form.parentDepartmentId} onChange={(e) => setForm({ ...form, parentDepartmentId: e.target.value })} />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(null)}>İptal</Button>
          {open === 'create' ? (
            <Button onClick={() => createMut.mutate()} disabled={!form.name}>Oluştur</Button>
          ) : (
            <Button onClick={() => updateMut.mutate()} disabled={!form.name}>Güncelle</Button>
          )}
        </DialogActions>
      </Dialog>
    </Stack>
  );
};


