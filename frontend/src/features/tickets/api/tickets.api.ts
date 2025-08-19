import { ticketApi } from '../../../shared/libs/axios/axiosClient';

export type CreateEmployeeTicketRequest = {
  issue: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  category: string; // category key
};

export async function createEmployeeTicket(payload: CreateEmployeeTicketRequest) {
  const res = await ticketApi.post('/api/tickets', payload);
  return res.data;
}



