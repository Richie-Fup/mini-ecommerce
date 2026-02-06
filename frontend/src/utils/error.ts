/**
 * Error handling utility functions
 */

export function extractErrorMessage(err: unknown): string {
  if (err instanceof Error && err.message) return err.message
  if (typeof err === 'string') return err
  try {
    return JSON.stringify(err)
  } catch {
    return 'Unknown error'
  }
}

export function extractProblemDetail(err: unknown): string | null {
  // Best-effort: Spring ProblemDetail might be at err.response.data.detail
  if (typeof err !== 'object' || err === null) return null
  const anyErr = err as any
  const detail = anyErr?.response?.data?.detail
  return typeof detail === 'string' && detail ? detail : null
}

