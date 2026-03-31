import axios from 'axios'
import type { Note, CreateNoteRequest } from './types'

export const notesApi = {
  getByEngineer: (engineerId: number) =>
    axios.get<Note[]>(`/api/engineers/${engineerId}/notes`).then(r => r.data),

  getSummary: (engineerId: number) =>
    axios.get<Note[]>(`/api/engineers/${engineerId}/notes/summary`).then(r => r.data),

  search: (engineerId: number, q: string) =>
    axios.get<Note[]>(`/api/engineers/${engineerId}/notes/search`, { params: { q } }).then(r => r.data),

  create: (engineerId: number, data: CreateNoteRequest) =>
    axios.post<Note>(`/api/engineers/${engineerId}/notes`, data).then(r => r.data),

  update: (noteId: number, data: CreateNoteRequest) =>
    axios.put<Note>(`/api/notes/${noteId}`, data).then(r => r.data),

  delete: (noteId: number) => axios.delete(`/api/notes/${noteId}`),
}
