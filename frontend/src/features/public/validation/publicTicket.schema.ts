import { z } from 'zod';

export const publicTicketSchema = z.object({
  name: z.string().min(2, 'Ad en az 2 karakter olmalıdır'),
  surname: z.string().min(2, 'Soyad en az 2 karakter olmalıdır'),
  email: z.string().email('Geçerli bir e-posta giriniz'),
  phone: z.string().optional().or(z.literal('')),
  issue: z.string().min(10, 'Açıklama en az 10 karakter olmalıdır'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH'], {
    required_error: 'Öncelik seçiniz',
  }),
  category: z.string().min(1, 'Kategori seçiniz'),
});

export type PublicTicketFormValues = z.infer<typeof publicTicketSchema>;


