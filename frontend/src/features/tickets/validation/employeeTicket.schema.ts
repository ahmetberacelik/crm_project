import { z } from 'zod';

export const employeeTicketSchema = z.object({
  issue: z.string().min(10, 'Açıklama en az 10 karakter olmalıdır'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH'], { required_error: 'Öncelik seçiniz' }),
  category: z.string().min(1, 'Kategori seçiniz'),
});

export type EmployeeTicketFormValues = z.infer<typeof employeeTicketSchema>;



