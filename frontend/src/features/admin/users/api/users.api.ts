import { departmentApi, userApi } from '../../../../shared/libs/axios/axiosClient';

export type AdminPerson = {
  personId: number;
  name: string;
  surname: string;
  email: string;
  phone?: string;
  departmentId: number;
};

export async function listAllPersons() {
  const res = await departmentApi.get<AdminPerson[]>('/api/persons/all');
  return res.data;
}

export async function updatePerson(id: number, payload: Partial<Omit<AdminPerson, 'personId'>>) {
  const res = await departmentApi.put<AdminPerson>(`/api/persons/${id}`, payload);
  return res.data;
}

export async function deactivatePerson(id: number) {
  const res = await departmentApi.delete(`/api/persons/${id}`);
  return res.data;
}

export type RegisterUserPayload = {
  name: string;
  surname: string;
  email: string;
  phone?: string;
  departmentId: number;
  userName: string;
  password: string;
};

export async function registerUser(payload: RegisterUserPayload) {
  const res = await userApi.post('/api/auth/register', payload);
  return res.data;
}


