import { useState, useEffect } from 'react'
import { useParams, useNavigate, useLocation } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { notesApi } from '@/api/notes'
import type { Note } from '@/api/types'

export function NoteEditor() {
  const { id, noteId } = useParams<{ id?: string; noteId?: string }>()
  const engineerId = id ? Number(id) : undefined
  const navigate = useNavigate()
  const location = useLocation()
  const queryClient = useQueryClient()

  const existingNote = location.state as Note | null

  const [content, setContent] = useState(existingNote?.content ?? '')
  const [meetingDate, setMeetingDate] = useState(
    existingNote?.meetingDate ?? new Date().toISOString().split('T')[0]
  )

  useEffect(() => {
    if (existingNote) {
      setContent(existingNote.content)
      setMeetingDate(existingNote.meetingDate)
    }
  }, [existingNote])

  const createMutation = useMutation({
    mutationFn: ({ eid, content, meetingDate }: { eid: number; content: string; meetingDate: string }) =>
      notesApi.create(eid, { content, meetingDate }),
    onSuccess: (_, vars) => {
      queryClient.invalidateQueries({ queryKey: ['notes', vars.eid] })
      queryClient.invalidateQueries({ queryKey: ['engineers'] })
      navigate(`/engineers/${vars.eid}`)
    },
  })

  const updateMutation = useMutation({
    mutationFn: ({ nid, content, meetingDate }: { nid: number; content: string; meetingDate: string }) =>
      notesApi.update(nid, { content, meetingDate }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notes'] })
      queryClient.invalidateQueries({ queryKey: ['engineers'] })
      navigate(-1)
    },
  })

  const isEditMode = !!noteId
  const isLoading = createMutation.isPending || updateMutation.isPending

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!content.trim()) return
    if (isEditMode) {
      updateMutation.mutate({ nid: Number(noteId), content, meetingDate })
    } else if (engineerId) {
      createMutation.mutate({ eid: engineerId, content, meetingDate })
    }
  }

  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">
        {isEditMode ? 'Edit note' : 'New note'}
      </h1>

      <form onSubmit={handleSubmit} className="flex flex-col gap-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Meeting date
          </label>
          <input
            type="date"
            value={meetingDate}
            onChange={e => setMeetingDate(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Notes
          </label>
          <textarea
            value={content}
            onChange={e => setContent(e.target.value)}
            placeholder="What did you discuss? What was decided? Any action items?"
            rows={14}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg text-sm font-mono leading-relaxed focus:outline-none focus:ring-2 focus:ring-blue-500 resize-y"
            required
          />
          <p className="text-xs text-gray-400 mt-1">Use blank lines between sections.</p>
        </div>

        <div className="flex gap-3">
          <button
            type="submit"
            disabled={isLoading || !content.trim()}
            className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isLoading ? 'Saving...' : 'Save note'}
          </button>
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="px-5 py-2 text-sm text-gray-600 hover:text-gray-900 transition-colors"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}
