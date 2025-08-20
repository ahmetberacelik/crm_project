import { departmentApi } from '../../../../shared/libs/axios/axiosClient';

export type AdminDepartment = {
  departmentId: number;
  name: string;
  parentDepartmentId?: number | null;
};

export async function listDepartments() {
  // Using tree endpoint which returns flat list in our backend
  const res = await departmentApi.get<AdminDepartment[]>('/api/departments/tree');
  return res.data;
}

export async function createDepartment(payload: { name: string; parentDepartmentId?: number | null }) {
  const res = await departmentApi.post<AdminDepartment>('/api/departments', payload);
  return res.data;
}

export async function updateDepartment(id: number, payload: { name: string; parentDepartmentId?: number | null }) {
  const res = await departmentApi.put<AdminDepartment>(`/api/departments/${id}`, payload);
  return res.data;
}

export async function deleteDepartment(id: number) {
  const res = await departmentApi.delete(`/api/departments/${id}`);
  return res.data;
}


