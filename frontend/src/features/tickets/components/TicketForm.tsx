import React from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { getCategories } from '../../public/api/public.api';
import { employeeTicketSchema, type EmployeeTicketFormValues } from '../validation/employeeTicket.schema';
import { createEmployeeTicket } from '../api/tickets.api';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Alert, Button, MenuItem, Stack, TextField, Typography } from '@mui/material';

export const TicketForm: React.FC = () => {
  const { data: categories, isLoading, error } = useQuery({ queryKey: ['categories'], queryFn: getCategories });
  const [info, setInfo] = React.useState<string | null>(null);

  const { control, register, handleSubmit, formState: { errors }, reset } = useForm<EmployeeTicketFormValues>({
    resolver: zodResolver(employeeTicketSchema),
    defaultValues: { issue: '', priority: 'MEDIUM', category: '' },
  });

  const mutation = useMutation({
    mutationFn: createEmployeeTicket,
    onSuccess: () => { setInfo('Ticket oluşturuldu.'); reset(); },
    onError: (e: any) => setInfo(e?.response?.data?.message || 'İşlem başarısız'),
  });

  if (isLoading) return <div>Yükleniyor...</div>;
  if (error) return <Alert severity="error">Kategoriler yüklenemedi.</Alert>;

  return (
    <form onSubmit={handleSubmit((values) => mutation.mutate(values))}>
      <Stack spacing={2} sx={{ width: 600 }}>
        <Typography variant="h6">Çalışan Ticket Oluştur</Typography>
        {info && <Alert severity={info.includes('başarısız') ? 'error' : 'success'}>{info}</Alert>}

        <Controller
          name="priority"
          control={control}
          render={({ field }) => (
            <TextField label="Öncelik" select {...field} error={!!errors.priority} helperText={errors.priority?.message}>
              {['LOW','MEDIUM','HIGH'].map((p) => (<MenuItem key={p} value={p}>{p}</MenuItem>))}
            </TextField>
          )}
        />

        <Controller
          name="category"
          control={control}
          render={({ field }) => (
            <TextField label="Kategori" select {...field} error={!!errors.category} helperText={errors.category?.message}>
              <MenuItem value="">Kategori seçiniz</MenuItem>
              {categories?.filter(c => c.isActive).map((c) => (
                <MenuItem key={c.id} value={c.key}>{c.displayName}</MenuItem>
              ))}
            </TextField>
          )}
        />

        <TextField label="Açıklama" multiline minRows={4} {...register('issue')} error={!!errors.issue} helperText={errors.issue?.message} />

        <Button type="submit" variant="contained" disabled={mutation.isPending}>Gönder</Button>
      </Stack>
    </form>
  );
};


