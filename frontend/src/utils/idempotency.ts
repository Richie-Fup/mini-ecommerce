/**
 * Idempotency key generation utility
 */

export function generateIdempotencyKey(): string {
  // Prefer crypto-quality randomness in H5.
  const c: any = typeof crypto !== 'undefined' ? crypto : null
  if (c?.randomUUID) return c.randomUUID()
  if (c?.getRandomValues) {
    const bytes = new Uint8Array(16)
    c.getRandomValues(bytes)
    return Array.from(bytes)
      .map(b => b.toString(16).padStart(2, '0'))
      .join('')
  }
  // Last resort fallback.
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

