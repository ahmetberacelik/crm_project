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

export type TicketResponse = {
  id: number;
  issue: string;
  priority: string;
  isActive: boolean;
  createdDate: string;
  closedDate?: string | null;
  creator?: {
    id: number;
    name?: string;
    surname?: string;
    email?: string;
    type?: string;
  };
  assignments?: Array<{
    assignmentId: number;
    status: string;
    assignedDate: string;
    completedDate?: string | null;
    assignee?: {
      id: number;
      name?: string;
      type?: string;
    };
  }>;
};

export async function getTicketById(id: number) {
  const res = await ticketApi.get<TicketResponse>(`/api/tickets/${id}`);
  return res.data;
}

export type TicketQuery = {
  page?: number;
  size?: number;
  sort?: string; // e.g. 'createdDate,desc'
};

export async function getMyAssignments(params: TicketQuery = {}) {
  const { page = 0, size = 20, sort = 'createdDate,desc' } = params;
  const res = await ticketApi.get<TicketResponse[]>(`/api/tickets/my-assignments`, { params: { page, size, sort } });
  return res.data;
}

export async function getDepartmentPool(params: TicketQuery = {}) {
  const { page = 0, size = 20, sort = 'createdDate,desc' } = params;
  const res = await ticketApi.get<TicketResponse[]>(`/api/tickets/pool`, { params: { page, size, sort } });
  return res.data;
}

export async function claimTicket(ticketId: number) {
  const res = await ticketApi.post<TicketResponse>(`/api/tickets/${ticketId}/assignments/claim`);
  return res.data;
}

export async function closeTicket(ticketId: number) {
  const res = await ticketApi.post<TicketResponse>(`/api/tickets/${ticketId}/assignments/close`);
  return res.data;
}

export async function reassignTicket(ticketId: number, newDepartmentId: number) {
  const res = await ticketApi.post<TicketResponse>(`/api/tickets/${ticketId}/assignments/reassign`, { newDepartmentId });
  return res.data;
}



