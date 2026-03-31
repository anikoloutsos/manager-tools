import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect } from 'vitest'
import { PreMeetingSummary } from './PreMeetingSummary'

function wrapper({ children }: { children: React.ReactNode }) {
  const qc = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return (
    <MemoryRouter initialEntries={['/engineers/1/summary']}>
      <QueryClientProvider client={qc}>
        <Routes>
          <Route path="/engineers/:id/summary" element={children} />
        </Routes>
      </QueryClientProvider>
    </MemoryRouter>
  )
}

describe('PreMeetingSummary', () => {
  it('renders heading', () => {
    render(<PreMeetingSummary />, { wrapper })
    expect(screen.getByRole('heading', { name: 'Pre-meeting summary' })).toBeInTheDocument()
  })

  it('renders notes after load', async () => {
    render(<PreMeetingSummary />, { wrapper })
    expect(await screen.findByText(/Discussed career goals/i)).toBeInTheDocument()
  })

  it('shows engineer name in breadcrumb', async () => {
    render(<PreMeetingSummary />, { wrapper })
    const matches = await screen.findAllByText('Alice Martin')
    expect(matches.length).toBeGreaterThan(0)
  })

  it('shows back link to engineer page', async () => {
    render(<PreMeetingSummary />, { wrapper })
    await screen.findByText('Alice Martin')
    expect(screen.getByRole('link', { name: /back to alice martin/i })).toBeInTheDocument()
  })
})
