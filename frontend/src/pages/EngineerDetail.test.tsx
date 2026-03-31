import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect } from 'vitest'
import { EngineerDetail } from './EngineerDetail'

function wrapper({ children }: { children: React.ReactNode }) {
  const qc = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return (
    <MemoryRouter initialEntries={['/engineers/1']}>
      <QueryClientProvider client={qc}>
        <Routes>
          <Route path="/engineers/:id" element={children} />
        </Routes>
      </QueryClientProvider>
    </MemoryRouter>
  )
}

describe('EngineerDetail', () => {
  it('renders engineer name after load', async () => {
    render(<EngineerDetail />, { wrapper })
    expect(await screen.findByRole('heading', { name: 'Alice Martin' })).toBeInTheDocument()
  })

  it('renders notes for the engineer', async () => {
    render(<EngineerDetail />, { wrapper })
    expect(await screen.findByText(/Discussed career goals/i)).toBeInTheDocument()
  })

  it('shows new note button', async () => {
    render(<EngineerDetail />, { wrapper })
    await screen.findByRole('heading', { name: 'Alice Martin' })
    expect(screen.getByRole('link', { name: /new note/i })).toBeInTheDocument()
  })

  it('shows pre-meeting summary button', async () => {
    render(<EngineerDetail />, { wrapper })
    await screen.findByRole('heading', { name: 'Alice Martin' })
    expect(screen.getByRole('link', { name: /pre-meeting summary/i })).toBeInTheDocument()
  })

  it('renders search input', async () => {
    render(<EngineerDetail />, { wrapper })
    await screen.findByRole('heading', { name: 'Alice Martin' })
    expect(screen.getByPlaceholderText(/search notes/i)).toBeInTheDocument()
  })
})
