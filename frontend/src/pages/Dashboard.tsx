import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { engineersApi } from '@/api/engineers'
import type { Engineer } from '@/api/types'

function EngineerCard({ engineer }: { engineer: Engineer }) {
  return (
    <Link
      to={`/engineers/${engineer.id}`}
      className="block bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md hover:border-gray-300 transition-all"
    >
      <div className="flex items-start justify-between">
        <div>
          <h2 className="font-semibold text-gray-900 text-lg">{engineer.name}</h2>
          <p className="text-sm text-gray-500 mt-0.5">{engineer.role}</p>
        </div>
        <div className="text-right">
          {engineer.lastNoteDate ? (
            <span className="text-xs text-gray-400">
              Last note: {new Date(engineer.lastNoteDate).toLocaleDateString()}
            </span>
          ) : (
            <span className="text-xs text-gray-300">No notes yet</span>
          )}
        </div>
      </div>
    </Link>
  )
}

export function Dashboard() {
  const { data: engineers, isLoading, isError } = useQuery({
    queryKey: ['engineers'],
    queryFn: engineersApi.getAll,
  })

  if (isLoading) return <p className="text-gray-500">Loading engineers...</p>
  if (isError) return <p className="text-red-500">Failed to load engineers.</p>

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Your Team</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {engineers?.map(engineer => (
          <EngineerCard key={engineer.id} engineer={engineer} />
        ))}
      </div>
    </div>
  )
}
