import { http, HttpResponse } from 'msw'
import type { Engineer, Note } from '@/api/types'

const engineers: Engineer[] = [
  { id: 1, name: 'Alice Martin', role: 'Senior Engineer', joinedAt: '2020-03-01', lastNoteDate: '2024-03-15' },
  { id: 2, name: 'Bob Chen', role: 'Engineer', joinedAt: '2021-06-15', lastNoteDate: null },
]

const notes: Note[] = [
  { id: 1, engineerId: 1, content: 'Discussed career goals and next steps', meetingDate: '2024-03-15', createdAt: '2024-03-15T10:00:00', updatedAt: '2024-03-15T10:00:00' },
  { id: 2, engineerId: 1, content: 'Reviewed Q1 performance', meetingDate: '2024-01-10', createdAt: '2024-01-10T10:00:00', updatedAt: '2024-01-10T10:00:00' },
]

export const handlers = [
  http.get('/api/engineers', () => HttpResponse.json(engineers)),

  http.get('/api/engineers/:id', ({ params }) => {
    const engineer = engineers.find(e => e.id === Number(params.id))
    if (!engineer) return new HttpResponse(null, { status: 404 })
    return HttpResponse.json(engineer)
  }),

  http.post('/api/engineers', async ({ request }) => {
    const body = await request.json() as Partial<Engineer>
    const created: Engineer = { id: 99, name: body.name ?? '', role: body.role ?? '', joinedAt: body.joinedAt ?? null, lastNoteDate: null }
    return HttpResponse.json(created, { status: 201 })
  }),

  http.get('/api/engineers/:id/notes', ({ params }) => {
    return HttpResponse.json(notes.filter(n => n.engineerId === Number(params.id)))
  }),

  http.get('/api/engineers/:id/notes/summary', ({ params }) => {
    return HttpResponse.json(notes.filter(n => n.engineerId === Number(params.id)).slice(0, 5))
  }),

  http.get('/api/engineers/:id/notes/search', ({ params, request }) => {
    const url = new URL(request.url)
    const q = url.searchParams.get('q')?.toLowerCase() ?? ''
    return HttpResponse.json(
      notes.filter(n => n.engineerId === Number(params.id) && n.content.toLowerCase().includes(q))
    )
  }),

  http.post('/api/engineers/:id/notes', async ({ params, request }) => {
    const body = await request.json() as Partial<Note>
    const created: Note = { id: 99, engineerId: Number(params.id), content: body.content ?? '', meetingDate: body.meetingDate ?? '', createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() }
    return HttpResponse.json(created, { status: 201 })
  }),

  http.put('/api/notes/:id', async ({ params, request }) => {
    const body = await request.json() as Partial<Note>
    const note = notes.find(n => n.id === Number(params.id))
    if (!note) return new HttpResponse(null, { status: 404 })
    return HttpResponse.json({ ...note, ...body })
  }),

  http.delete('/api/notes/:id', () => new HttpResponse(null, { status: 204 })),
]
