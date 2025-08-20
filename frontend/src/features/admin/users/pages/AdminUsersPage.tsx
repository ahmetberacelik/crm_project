import React from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { deactivatePerson, listAllPersons, registerUser, updatePerson } from '../api/users.api';
import { getDepartmentsTree } from '../../../departments/api/departments.api';
import { Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Paper, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

export const AdminUsersPage: React.FC = () => {
  const qc = useQueryClient();
  const listQ = useQuery({ queryKey: ['admin','users'], queryFn: listAllPersons });
  const deptsQ = useQuery({ queryKey: ['departments'], queryFn: getDepartmentsTree });

  const [open, setOpen] = React.useState<null | 'create' | { id: number; name: string; surname: string; email: string; phone?: string; departmentId: number }>(null);
  const [form, setForm] = React.useState<any>({ name: '', surname: '', email: '', phone: '', departmentId: '', userName: '', password: '' });
  const [info, setInfo] = React.useState<string | null>(null);

  const registerMut = useMutation({
    mutationFn: () => registerUser({
      name: form.name,
      surname: form.surname,
      email: form.email,
      phone: form.phone || undefined,
      departmentId: Number(form.departmentId),
      userName: form.userName,
      password: form.password,
    }),
    onSuccess: () => { setOpen(null); setForm({ name: '', surname: '', email: '', phone: '', departmentId: '', userName: '', password: '' }); qc.invalidateQueries({ queryKey: ['admin','users'] }); },
    onError: (e: any) => setInfo(e?.response?.data?.message || 'İşlem başarısız'),
  });

  const updateMut = useMutation({
    mutationFn: () => updatePerson((open as any).id, {
      name: form.name,
      surname: form.surname,
      email: form.email,
      phone: form.phone || undefined,
      departmentId: Number(form.departmentId),
    }),
    onSuccess: () => { setOpen(null); qc.invalidateQueries({ queryKey: ['admin','users'] }); },
  });
  const deleteMut = useMutation({ mutationFn: (id: number) => deactivatePerson(id), onSuccess: () => qc.invalidateQueries({ queryKey: ['admin','users'] }) });

  if (listQ.isLoading) return <div>Yükleniyor...</div>;
  if (listQ.error) return <Alert severity="error">Kullanıcılar yüklenemedi.</Alert>;

  const startEdit = (u: any) => {
    setOpen({ id: u.personId, name: u.name, surname: u.surname, email: u.email, phone: u.phone, departmentId: u.departmentId });
    setForm({ name: u.name, surname: u.surname, email: u.email, phone: u.phone || '', departmentId: String(u.departmentId) });
  };

  return (
    <Stack spacing={2}>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="h6">Kullanıcılar</Typography>
        <Button startIcon={<AddIcon />} variant="contained" onClick={() => { setOpen('create'); setForm({ name: '', surname: '', email: '', phone: '', departmentId: '', userName: '', password: '' }); }}>Yeni Kullanıcı</Button>
      </Stack>

      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Ad</TableCell>
              <TableCell>Soyad</TableCell>
              <TableCell>E-posta</TableCell>
              <TableCell>Departman</TableCell>
              <TableCell align="right">İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {listQ.data?.map((u) => (
              <TableRow key={u.personId} hover>
                <TableCell>#{u.personId}</TableCell>
                <TableCell>{u.name}</TableCell>
                <TableCell>{u.surname}</TableCell>
                <TableCell>{u.email}</TableCell>
                <TableCell>{u.departmentId}</TableCell>
                <TableCell align="right">
                  <IconButton size="small" onClick={() => startEdit(u)}><EditIcon fontSize="small" /></IconButton>
                  <IconButton size="small" color="error" onClick={() => deleteMut.mutate(u.personId)}><DeleteIcon fontSize="small" /></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={Boolean(open)} onClose={() => setOpen(null)} fullWidth maxWidth="sm">
        <DialogTitle>{open === 'create' ? 'Yeni Kullanıcı' : 'Kullanıcı Düzenle'}</DialogTitle>
        <DialogContent>
          {info && <Alert severity="error" sx={{ mb: 2 }}>{info}</Alert>}
          <Stack spacing={2} sx={{ mt: 1 }}>
            <TextField label="Ad" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            <TextField label="Soyad" value={form.surname} onChange={(e) => setForm({ ...form, surname: e.target.value })} />
            <TextField label="E-posta" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
            <TextField label="Telefon" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
            <TextField label="Departman" select SelectProps={{ native: true }} value={form.departmentId} onChange={(e) => setForm({ ...form, departmentId: e.target.value })}>
              <option value="" disabled>Seçiniz</option>
              {deptsQ.data?.map(d => (
                <option key={d.departmentId} value={d.departmentId}>{d.name}</option>
              ))}
            </TextField>
            {open === 'create' && (
              <>
                <TextField label="Kullanıcı Adı" value={form.userName} onChange={(e) => setForm({ ...form, userName: e.target.value })} />
                <TextField label="Şifre" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
              </>
            )}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(null)}>İptal</Button>
          {open === 'create' ? (
            <Button onClick={() => registerMut.mutate()} disabled={!form.name || !form.surname || !form.email || !form.departmentId || !form.userName || !form.password}>Oluştur</Button>
          ) : (
            <Button onClick={() => updateMut.mutate()} disabled={!form.name || !form.surname || !form.email || !form.departmentId}>Güncelle</Button>
          )}
        </DialogActions>
      </Dialog>
    </Stack>
  );
};


