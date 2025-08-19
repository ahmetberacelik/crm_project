import { ticketApi } from '../../../shared/libs/axios/axiosClient';

export type Category = {
  id: number;
  key: string;
  displayName: string;
  targetDepartmentId: number;
  isActive: boolean;
};

export async function getCategories() {
  const res = await ticketApi.get<Category[]>('/api/public/categories');
  return res.data;
}

export type CreatePublicTicketRequest = {
  name: string;
  surname: string;
  email: string;
  phone?: string;
  issue: string;
  priority: string;
  category: string; // use category.key
};

export async function createPublicTicket(payload: CreatePublicTicketRequest) {
  const res = await ticketApi.post('/api/tickets/public', payload);
  return res.data;
}


