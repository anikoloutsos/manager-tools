import { useParams, Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { engineersApi } from '@/api/engineers'
import { notesApi } from '@/api/notes'
import type { Note } from '@/api/types'

function SummaryNote({ note, index }: { note: Note; index: number }) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5">
      <div className="flex items-center justify-between mb-3">
        <span className="text-xs font-semibold text-blue-600 uppercase tracking-wide">
          #{index + 1}
        </span>
        <span className="text-sm text-gray-500">
          {new Date(note.meetingDate).toLocaleDateString('en-GB', { day: 'numeric', month: 'long', year: 'numeric' })}
        </span>
      </div>
      <p className="text-gray-800 text-sm leading-relaxed whitespace-pre-wrap">{note.content}</p>
    </div>
  )
}

export function PreMeetingSummary() {
  const { id } = useParams<{ id: string }>()
  const engineerId = Number(id)

  const { data: engineer } = useQuery({
    queryKey: ['engineers', engineerId],
    queryFn: () => engineersApi.getById(engineerId),
  })

  const { data: notes, isLoading } = useQuery({
    queryKey: ['notes', engineerId, 'summary'],
    queryFn: () => notesApi.getSummary(engineerId),
  })

  return (
    <div className="max-w-2xl">
      <div className="flex items-center gap-2 text-sm text-gray-500 mb-6">
        <Link to="/" className="hover:text-gray-900">Team</Link>
        <span>/</span>
        <Link to={`/engineers/${engineerId}`} className="hover:text-gray-900">
          {engineer?.name ?? '...'}
        </Link>
        <span>/</span>
        <span className="text-gray-900">Pre-meeting summary</span>
      </div>

      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Pre-meeting summary</h1>
        {engineer && (
          <p className="text-gray-500 mt-1">
            Last {notes?.length ?? 0} notes with {engineer.name}
          </p>
        )}
      </div>

      {isLoading ? (
        <p className="text-gray-500">Loading...</p>
      ) : notes?.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-400">No notes yet for this engineer.</p>
          <Link
            to={`/engineers/${engineerId}/notes/new`}
            className="mt-4 inline-block text-sm text-blue-600 hover:text-blue-800"
          >
            Add the first note
          </Link>
        </div>
      ) : (
        <div className="flex flex-col gap-4">
          {notes?.map((note, i) => (
            <SummaryNote key={note.id} note={note} index={i} />
          ))}
        </div>
      )}

      <div className="mt-8">
        <Link
          to={`/engineers/${engineerId}`}
          className="text-sm text-gray-500 hover:text-gray-900"
        >
          ← Back to {engineer?.name}
        </Link>
      </div>
    </div>
  )
}
