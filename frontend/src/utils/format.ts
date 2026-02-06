/**
 * Formatting utility functions
 */

export function formatMoney(n: number): string {
  if (!Number.isFinite(n)) return String(n)
  return `$${n.toFixed(2)}`
}

export function asInt(input: string): number | null {
  const n = Number(input)
  if (!Number.isFinite(n)) return null
  const i = Math.floor(n)
  return i > 0 ? i : null
}

