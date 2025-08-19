import React from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { getCategories, createPublicTicket } from '../api/public.api';
import { Alert, Button, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { publicTicketSchema, type PublicTicketFormValues } from '../validation/publicTicket.schema';

export const PublicTicketForm: React.FC = () => {
  const { data: categories, isLoading, error } = useQuery({ queryKey: ['categories'], queryFn: getCategories });
  const [info, setInfo] = React.useState<string | null>(null);

  const { register, handleSubmit, formState: { errors }, reset, control } = useForm<PublicTicketFormValues>({
    resolver: zodResolver(publicTicketSchema),
    defaultValues: { name: '', surname: '', email: '', phone: '', issue: '', priority: 'MEDIUM', category: '' },
  });

  const mutation = useMutation({
    mutationFn: createPublicTicket,
    onSuccess: () => { setInfo('Talebiniz alındı.'); reset(); },
    onError: (e: any) => setInfo(e?.response?.data?.message || 'İşlem başarısız'),
  });

  if (isLoading) return <div>Yükleniyor...</div>;
  if (error) return <Alert severity="error">Kategoriler yüklenemedi.</Alert>;

  return (
    <form onSubmit={handleSubmit((values) => mutation.mutate(values))}>
      <Stack spacing={2} sx={{ width: 500 }}>
        <Typography variant="h6">Destek Talebi Oluştur</Typography>
        {info && <Alert severity="success">{info}</Alert>}

        <TextField label="Ad" {...register('name')} error={!!errors.name} helperText={errors.name?.message} required />
        <TextField label="Soyad" {...register('surname')} error={!!errors.surname} helperText={errors.surname?.message} required />
        <TextField label="E-posta" type="email" {...register('email')} error={!!errors.email} helperText={errors.email?.message} required />
        <TextField label="Telefon" {...register('phone')} />
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
            <TextField
              label="Kategori"
              select
              {...field}
              error={!!errors.category}
              helperText={errors.category?.message}
              SelectProps={{ displayEmpty: true }}
              required
            >
              <MenuItem value="">Kategori seçiniz</MenuItem>
              {categories?.filter(c => c.isActive).map((c) => (
                <MenuItem key={c.id} value={c.key}>{c.displayName}</MenuItem>
              ))}
            </TextField>
          )}
        />
        <TextField label="Açıklama" multiline minRows={4} {...register('issue')} error={!!errors.issue} helperText={errors.issue?.message} required />
        <Button type="submit" variant="contained" disabled={mutation.isPending}>Gönder</Button>
      </Stack>
    </form>
  );
};


