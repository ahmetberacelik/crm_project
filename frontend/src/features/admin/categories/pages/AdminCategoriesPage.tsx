import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { createCategory, getAllCategories, updateCategoryStatus } from '../api/categories.api';
import { getDepartmentsTree } from '../../../departments/api/departments.api';
import {
  Alert, Button, Chip, Dialog, DialogActions, DialogContent, DialogTitle,
  Grid, IconButton, Paper, Stack, Switch, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  TextField, Typography
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';

export const AdminCategoriesPage: React.FC = () => {
  const qc = useQueryClient();
  const listQ = useQuery({ queryKey: ['admin','categories'], queryFn: getAllCategories });
  const deptsQ = useQuery({ queryKey: ['departments'], queryFn: getDepartmentsTree });
  const [open, setOpen] = React.useState(false);
  const [form, setForm] = React.useState({ categoryKey: '', displayName: '', targetDepartmentId: '' });
  const [info, setInfo] = React.useState<string | null>(null);

  const createMut = useMutation({
    mutationFn: () => createCategory({
      categoryKey: form.categoryKey,
      displayName: form.displayName,
      targetDepartmentId: Number(form.targetDepartmentId),
    }),
    onSuccess: () => { setOpen(false); setForm({ categoryKey: '', displayName: '', targetDepartmentId: '' }); qc.invalidateQueries({ queryKey: ['admin','categories'] }); },
    onError: (e: any) => setInfo(e?.response?.data?.message || 'İşlem başarısız'),
  });

  const statusMut = useMutation({
    mutationFn: ({ id, isActive }: { id: number; isActive: boolean }) => updateCategoryStatus(id, isActive),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin','categories'] }),
  });

  if (listQ.isLoading) return <div>Yükleniyor...</div>;
  if (listQ.error) return <Alert severity="error">Kategoriler yüklenemedi.</Alert>;

  return (
    <Stack spacing={2}>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="h6">Kategoriler</Typography>
        <Button startIcon={<AddIcon />} variant="contained" onClick={() => setOpen(true)}>Yeni Kategori</Button>
      </Stack>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Key</TableCell>
              <TableCell>Ad</TableCell>
              <TableCell>Hedef Departman</TableCell>
              <TableCell>Durum</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {listQ.data?.map((c) => (
              <TableRow key={c.id} hover>
                <TableCell>#{c.id}</TableCell>
                <TableCell><Chip label={c.key} /></TableCell>
                <TableCell>{c.displayName}</TableCell>
                <TableCell>{c.targetDepartmentId}</TableCell>
                <TableCell>
                  <Switch checked={c.isActive} onChange={(e) => statusMut.mutate({ id: c.id, isActive: e.target.checked })} />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Yeni Kategori</DialogTitle>
        <DialogContent>
          {info && <Alert severity="error" sx={{ mb: 2 }}>{info}</Alert>}
          <Stack spacing={2} sx={{ mt: 1 }}>
            <TextField label="Key" value={form.categoryKey} onChange={(e) => setForm({ ...form, categoryKey: e.target.value })} />
            <TextField label="Görünen Ad" value={form.displayName} onChange={(e) => setForm({ ...form, displayName: e.target.value })} />
            <TextField
              label="Hedef Departman"
              select
              SelectProps={{ native: true }}
              value={form.targetDepartmentId}
              onChange={(e) => setForm({ ...form, targetDepartmentId: e.target.value })}
            >
              <option value="" disabled>Seçiniz</option>
              {deptsQ.data?.map(d => (
                <option key={d.departmentId} value={d.departmentId}>{d.name}</option>
              ))}
            </TextField>
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>İptal</Button>
          <Button onClick={() => createMut.mutate()} disabled={!form.categoryKey || !form.displayName || !form.targetDepartmentId}>Oluştur</Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
};


