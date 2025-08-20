import { departmentApi } from '../../../shared/libs/axios/axiosClient';

export type Person = {
  personId: number;
  name: string;
  surname: string;
  email: string;
  phone?: string;
  departmentId: number;
};

export type Department = {
  departmentId: number;
  name: string;
  parentDepartmentId?: number | null;
};

export async function getPersonById(id: number) {
  const res = await departmentApi.get<Person>(`/api/persons/${id}`);
  return res.data;
}

export async function getDepartmentById(id: number) {
  const res = await departmentApi.get<Department>(`/api/departments/${id}`);
  return res.data;
}


