import { ticketApi } from '../../../../shared/libs/axios/axiosClient';

export type AdminCategory = {
  id: number;
  key: string;
  displayName: string;
  targetDepartmentId: number;
  isActive: boolean;
};

export async function getAllCategories() {
  const res = await ticketApi.get<AdminCategory[]>('/api/management/categories');
  return res.data;
}

export type CreateCategoryPayload = {
  categoryKey: string;
  displayName: string;
  targetDepartmentId: number;
};

export async function createCategory(payload: CreateCategoryPayload) {
  const res = await ticketApi.post<AdminCategory>('/api/management/categories', payload);
  return res.data;
}

export async function updateCategoryStatus(id: number, isActive: boolean) {
  const res = await ticketApi.patch<AdminCategory>(`/api/management/categories/${id}/status`, { isActive });
  return res.data;
}


