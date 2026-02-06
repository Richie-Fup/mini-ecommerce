/**
 * Common utility functions
 */

export function cx(...classes: Array<string | false | null | undefined>): string {
  return classes.filter(Boolean).join(' ')
}

export function is2xx(statusCode: number | undefined): boolean {
  return typeof statusCode === 'number' && statusCode >= 200 && statusCode < 300
}

