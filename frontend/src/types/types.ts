/**
 * API response types matching backend DTOs
 */

export type Product = {
  id: number
  name: string
  price: number
  stock: number
}

export type CreateOrderResponse = {
  orderId: number
  totalPrice: number
}

export type OrderDetails = {
  id: number
  productId: number
  quantity: number
  unitPrice: number
  totalPrice: number
  createdAt: string
}

export type ProblemDetail = {
  detail?: string
  title?: string
  status?: number
  path?: string
  timestamp?: string
}

