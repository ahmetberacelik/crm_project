import { departmentApi } from '../../../shared/libs/axios/axiosClient';

export type DepartmentItem = {
  departmentId: number;
  name: string;
  parentDepartmentId?: number | null;
};

export async function getDepartmentsTree() {
  const res = await departmentApi.get<DepartmentItem[]>(`/api/departments/tree`);
  return res.data;
}


