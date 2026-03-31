import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect } from 'vitest'
import { NoteEditor } from './NoteEditor'

function makeWrapper(path: string, initialEntry: string) {
  const qc = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return ({ children }: { children: React.ReactNode }) => (
    <MemoryRouter initialEntries={[initialEntry]}>
      <QueryClientProvider client={qc}>
        <Routes>
          <Route path={path} element={children} />
        </Routes>
      </QueryClientProvider>
    </MemoryRouter>
  )
}

describe('NoteEditor - create mode', () => {
  const wrapper = makeWrapper('/engineers/:id/notes/new', '/engineers/1/notes/new')

  it('renders new note heading', () => {
    render(<NoteEditor />, { wrapper })
    expect(screen.getByText('New note')).toBeInTheDocument()
  })

  it('has a textarea for note content', () => {
    render(<NoteEditor />, { wrapper })
    expect(screen.getByRole('textbox')).toBeInTheDocument()
  })

  it('has a date input defaulting to today', () => {
    render(<NoteEditor />, { wrapper })
    const today = new Date().toISOString().split('T')[0]
    expect(screen.getByDisplayValue(today)).toBeInTheDocument()
  })

  it('save button is disabled when content is empty', () => {
    render(<NoteEditor />, { wrapper })
    expect(screen.getByRole('button', { name: /save note/i })).toBeDisabled()
  })

  it('save button enables after typing content', async () => {
    render(<NoteEditor />, { wrapper })
    await userEvent.type(screen.getByRole('textbox'), 'Test note content')
    expect(screen.getByRole('button', { name: /save note/i })).not.toBeDisabled()
  })
})

describe('NoteEditor - edit mode', () => {
  const wrapper = makeWrapper('/notes/:noteId/edit', '/notes/1/edit')

  it('renders edit note heading', () => {
    render(<NoteEditor />, { wrapper })
    expect(screen.getByText('Edit note')).toBeInTheDocument()
  })
})
