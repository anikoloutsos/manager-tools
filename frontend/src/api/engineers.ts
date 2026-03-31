import axios from 'axios'
import type { Engineer, CreateEngineerRequest } from './types'

const base = '/api/engineers'

export const engineersApi = {
  getAll: () => axios.get<Engineer[]>(base).then(r => r.data),
  getById: (id: number) => axios.get<Engineer>(`${base}/${id}`).then(r => r.data),
  create: (data: CreateEngineerRequest) => axios.post<Engineer>(base, data).then(r => r.data),
  update: (id: number, data: CreateEngineerRequest) => axios.put<Engineer>(`${base}/${id}`, data).then(r => r.data),
  delete: (id: number) => axios.delete(`${base}/${id}`),
}
