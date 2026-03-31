export interface Engineer {
  id: number
  name: string
  role: string
  joinedAt: string | null
  lastNoteDate: string | null
}

export interface Note {
  id: number
  engineerId: number
  content: string
  meetingDate: string
  createdAt: string
  updatedAt: string
}

export interface CreateEngineerRequest {
  name: string
  role: string
  joinedAt?: string
}

export interface CreateNoteRequest {
  content: string
  meetingDate: string
}
