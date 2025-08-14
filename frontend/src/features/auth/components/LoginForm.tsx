import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema, type LoginFormValues } from '../validation/login.schema';
import { login } from '../api/auth.api';
import { Button, Stack, TextField, Typography, Alert } from '@mui/material';

interface Props {
  onSuccess?: () => void;
}

export const LoginForm: React.FC<Props> = ({ onSuccess }) => {
  const [error, setError] = React.useState<string | null>(null);
  const [loading, setLoading] = React.useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { userName: '', password: '' },
  });

  const onSubmit = async (values: LoginFormValues) => {
    setError(null);
    setLoading(true);
    try {
      await login(values);
      onSuccess?.();
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Giriş başarısız.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <Stack spacing={2} sx={{ width: 360 }}>
        <Typography variant="h5">Giriş Yap</Typography>

        {error && <Alert severity="error">{error}</Alert>}

        <TextField
          label="Kullanıcı Adı"
          {...register('userName')}
          error={!!errors.userName}
          helperText={errors.userName?.message}
          autoComplete="username"
          fullWidth
        />

        <TextField
          label="Şifre"
          type="password"
          {...register('password')}
          error={!!errors.password}
          helperText={errors.password?.message}
          autoComplete="current-password"
          fullWidth
        />

        <Button type="submit" variant="contained" disabled={loading}>
          {loading ? 'Gönderiliyor...' : 'Giriş Yap'}
        </Button>
      </Stack>
    </form>
  );
};


