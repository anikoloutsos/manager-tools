import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect } from 'vitest'
import { Dashboard } from './Dashboard'

function wrapper({ children }: { children: React.ReactNode }) {
  const qc = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return (
    <MemoryRouter>
      <QueryClientProvider client={qc}>{children}</QueryClientProvider>
    </MemoryRouter>
  )
}

describe('Dashboard', () => {
  it('renders loading state initially', () => {
    render(<Dashboard />, { wrapper })
    expect(screen.getByText(/loading engineers/i)).toBeInTheDocument()
  })

  it('renders engineer cards after load', async () => {
    render(<Dashboard />, { wrapper })
    expect(await screen.findByText('Alice Martin')).toBeInTheDocument()
    expect(screen.getByText('Bob Chen')).toBeInTheDocument()
  })

  it('shows role for each engineer', async () => {
    render(<Dashboard />, { wrapper })
    expect(await screen.findByText('Senior Engineer')).toBeInTheDocument()
  })

  it('links to engineer detail page', async () => {
    render(<Dashboard />, { wrapper })
    const link = await screen.findByRole('link', { name: /alice martin/i })
    expect(link).toHaveAttribute('href', '/engineers/1')
  })
})
