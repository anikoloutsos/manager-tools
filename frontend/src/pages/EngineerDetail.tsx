import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { engineersApi } from '@/api/engineers'
import { notesApi } from '@/api/notes'
import type { Note } from '@/api/types'

function NoteCard({ note, onDelete }: { note: Note; onDelete: (id: number) => void }) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5">
      <div className="flex items-center justify-between mb-3">
        <span className="text-sm font-medium text-gray-500">
          {new Date(note.meetingDate).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}
        </span>
        <div className="flex gap-2">
          <Link
            to={`/notes/${note.id}/edit`}
            state={note}
            className="text-xs text-blue-600 hover:text-blue-800"
          >
            Edit
          </Link>
          <button
            onClick={() => onDelete(note.id)}
            className="text-xs text-red-500 hover:text-red-700"
          >
            Delete
          </button>
        </div>
      </div>
      <p className="text-gray-800 whitespace-pre-wrap text-sm leading-relaxed">{note.content}</p>
    </div>
  )
}

export function EngineerDetail() {
  const { id } = useParams<{ id: string }>()
  const engineerId = Number(id)
  const queryClient = useQueryClient()
  const [searchQuery, setSearchQuery] = useState('')
  const [isSearching, setIsSearching] = useState(false)

  const { data: engineer, isLoading: loadingEngineer } = useQuery({
    queryKey: ['engineers', engineerId],
    queryFn: () => engineersApi.getById(engineerId),
  })

  const { data: notes, isLoading: loadingNotes } = useQuery({
    queryKey: ['notes', engineerId, searchQuery],
    queryFn: () =>
      searchQuery
        ? notesApi.search(engineerId, searchQuery)
        : notesApi.getByEngineer(engineerId),
  })

  const deleteMutation = useMutation({
    mutationFn: notesApi.delete,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['notes', engineerId] }),
  })

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setIsSearching(!!searchQuery)
  }

  const clearSearch = () => {
    setSearchQuery('')
    setIsSearching(false)
  }

  if (loadingEngineer) return <p className="text-gray-500">Loading...</p>
  if (!engineer) return <p className="text-red-500">Engineer not found.</p>

  return (
    <div>
      <div className="flex items-center gap-2 text-sm text-gray-500 mb-6">
        <Link to="/" className="hover:text-gray-900">Team</Link>
        <span>/</span>
        <span className="text-gray-900">{engineer.name}</span>
      </div>

      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{engineer.name}</h1>
          <p className="text-gray-500 mt-0.5">{engineer.role}</p>
        </div>
        <div className="flex gap-2">
          <Link
            to={`/engineers/${engineerId}/summary`}
            className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors"
          >
            Pre-meeting summary
          </Link>
          <Link
            to={`/engineers/${engineerId}/notes/new`}
            className="px-4 py-2 text-sm bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
          >
            + New note
          </Link>
        </div>
      </div>

      <form onSubmit={handleSearch} className="flex gap-2 mb-6">
        <input
          type="text"
          value={searchQuery}
          onChange={e => setSearchQuery(e.target.value)}
          placeholder="Search notes..."
          className="flex-1 px-4 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          className="px-4 py-2 text-sm bg-gray-800 text-white rounded-lg hover:bg-gray-900"
        >
          Search
        </button>
        {isSearching && (
          <button
            type="button"
            onClick={clearSearch}
            className="px-4 py-2 text-sm text-gray-600 hover:text-gray-900"
          >
            Clear
          </button>
        )}
      </form>

      {loadingNotes ? (
        <p className="text-gray-500">Loading notes...</p>
      ) : notes?.length === 0 ? (
        <p className="text-gray-400 text-sm">
          {isSearching ? 'No notes match your search.' : 'No notes yet. Start by adding one.'}
        </p>
      ) : (
        <div className="flex flex-col gap-4">
          {notes?.map(note => (
            <NoteCard key={note.id} note={note} onDelete={id => deleteMutation.mutate(id)} />
          ))}
        </div>
      )}
    </div>
  )
}
