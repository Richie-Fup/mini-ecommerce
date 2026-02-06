/**
 * API endpoint paths
 */
export const API_PATHS = {
  PRODUCTS: '/products',
  ORDERS: '/orders',
  ORDER_BY_ID: (id: number) => `/orders/${id}`,
} as const

/**
 * API header names
 */
export const API_HEADERS = {
  IDEMPOTENCY_KEY: 'Idempotency-Key',
  CONTENT_TYPE: 'content-type',
} as const

