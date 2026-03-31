import { Routes, Route } from 'react-router-dom'
import { Dashboard } from '@/pages/Dashboard'
import { EngineerDetail } from '@/pages/EngineerDetail'
import { NoteEditor } from '@/pages/NoteEditor'
import { PreMeetingSummary } from '@/pages/PreMeetingSummary'

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white border-b border-gray-200 px-6 py-4">
        <a href="/" className="text-xl font-semibold text-gray-900">
          Chapter Manager
        </a>
      </nav>
      <main className="max-w-5xl mx-auto px-6 py-8">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/engineers/:id" element={<EngineerDetail />} />
          <Route path="/engineers/:id/notes/new" element={<NoteEditor />} />
          <Route path="/notes/:noteId/edit" element={<NoteEditor />} />
          <Route path="/engineers/:id/summary" element={<PreMeetingSummary />} />
        </Routes>
      </main>
    </div>
  )
}
