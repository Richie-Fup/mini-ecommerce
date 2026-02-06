import Taro from '@tarojs/taro'
import { Input, Text, View } from '@tarojs/components'
import { useEffect, useMemo, useState, type FormEvent } from 'react'
import { API_BASE_URL } from '../../config'
import './index.scss'

const LOW_STOCK_THRESHOLD = 3

type Product = {
  id: number
  name: string
  price: number
  stock: number
}

type CreateOrderResponse = {
  orderId: number
  totalPrice: number
}

type ApiResponse<T> = {
  status: 'OK' | 'CREATED' | string
  data: T
  timestamp?: string
}

type ProblemDetail = {
  detail?: string
  title?: string
  status?: number
  path?: string
  timestamp?: string
}

type OrderDetails = {
  id: number
  productId: number
  quantity: number
  unitPrice: number
  totalPrice: number
  createdAt: string
}

function cx(...classes: Array<string | false | null | undefined>) {
  return classes.filter(Boolean).join(' ')
}

function asInt(input: string): number | null {
  const n = Number(input)
  if (!Number.isFinite(n)) return null
  const i = Math.floor(n)
  return i > 0 ? i : null
}

function formatMoney(n: number): string {
  if (!Number.isFinite(n)) return String(n)
  return `$${n.toFixed(2)}`
}

function extractErrorMessage(err: unknown): string {
  if (err instanceof Error && err.message) return err.message
  if (typeof err === 'string') return err
  try {
    return JSON.stringify(err)
  } catch {
    return 'Unknown error'
  }
}

function extractProblemDetail(err: unknown): string | null {
  // Best-effort: Spring ProblemDetail might be at err.response.data.detail
  if (typeof err !== 'object' || err === null) return null
  const anyErr = err as any
  const detail = anyErr?.response?.data?.detail
  return typeof detail === 'string' && detail ? detail : null
}

function is2xx(statusCode: number | undefined) {
  return typeof statusCode === 'number' && statusCode >= 200 && statusCode < 300
}

async function requestApiData<T>(opts: Parameters<typeof Taro.request>[0]): Promise<T> {
  const res = await Taro.request<any>(opts)
  // In weapp, Taro.request resolves even when HTTP status is 4xx/5xx.
  if (!is2xx((res as any).statusCode)) {
    const pd: ProblemDetail | undefined = res?.data
    const msg =
      (typeof pd?.detail === 'string' && pd.detail) ||
      (typeof pd?.title === 'string' && pd.title) ||
      `Request failed (status ${(res as any).statusCode ?? 'unknown'})`
    throw new Error(msg)
  }
  const body: ApiResponse<T> | undefined = res?.data
  return body?.data as T
}

function generateIdempotencyKey(): string {
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

export default function IndexPage() {
  const isWeb = Taro.getEnv() === Taro.ENV_TYPE.WEB
  const [products, setProducts] = useState<Product[]>([])
  const [loadingProducts, setLoadingProducts] = useState(false)
  const [placing, setPlacing] = useState(false)

  const [selectedProductId, setSelectedProductId] = useState<number | null>(null)
  const [qtyInput, setQtyInput] = useState<string>('1')
  const [idempotencyKey, setIdempotencyKey] = useState<string | null>(null)

  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [orderDetails, setOrderDetails] = useState<OrderDetails | null>(null)

  const productsById = useMemo(() => {
    const m = new Map<number, Product>()
    products.forEach(p => m.set(p.id, p))
    return m
  }, [products])

  const selectedProduct = selectedProductId == null ? null : productsById.get(selectedProductId) ?? null
  const qty = asInt(qtyInput) ?? 0
  const canPlace =
    !!selectedProduct &&
    qty > 0 &&
    qty <= selectedProduct.stock &&
    !placing

  async function loadProducts() {
    setLoadingProducts(true)
    setErrorMessage(null)
    try {
      const data = await requestApiData<Product[]>({
        url: `${API_BASE_URL}/products`,
        method: 'GET'
      })
      setProducts(data ?? [])
    } catch (e: unknown) {
      setErrorMessage(extractErrorMessage(e))
    } finally {
      setLoadingProducts(false)
    }
  }

  function selectProduct(productId: number) {
    setSelectedProductId(productId)
    setQtyInput('1')
    setSuccessMessage(null)
    setErrorMessage(null)
    setOrderDetails(null)
    setIdempotencyKey(null)
  }

  function clampQty(next: number, max: number) {
    if (next < 1) return 1
    if (next > max) return max
    return next
  }

  function stepQty(delta: number) {
    if (!selectedProduct) return
    const next = clampQty((asInt(qtyInput) ?? 1) + delta, Math.max(1, selectedProduct.stock))
    setQtyInput(String(next))
  }

  async function fetchOrderDetails(orderId: number) {
    try {
      const data = await requestApiData<OrderDetails>({
        url: `${API_BASE_URL}/orders/${orderId}`,
        method: 'GET'
      })
      setOrderDetails(data ?? null)
    } catch {
      // optional UX improvement only
    }
  }

  async function placeSelectedOrder() {
    setSuccessMessage(null)
    setErrorMessage(null)
    setOrderDetails(null)

    if (!selectedProduct) {
      setErrorMessage('Please select a product first.')
      return
    }
    if (!qty || qty <= 0) {
      setErrorMessage('Please input a valid quantity (positive integer).')
      return
    }
    if (qty > selectedProduct.stock) {
      setErrorMessage(`Only ${selectedProduct.stock} left in stock.`)
      return
    }

    // Generate once per attempt; keep it on failure so retries don't duplicate orders.
    const key = idempotencyKey ?? generateIdempotencyKey()
    if (!idempotencyKey) setIdempotencyKey(key)

    setPlacing(true)
    try {
      const data = await requestApiData<CreateOrderResponse>({
        url: `${API_BASE_URL}/orders`,
        method: 'POST',
        data: { productId: selectedProduct.id, quantity: qty },
        header: { 'content-type': 'application/json', 'Idempotency-Key': key }
      })
      setSuccessMessage(
        `Order confirmed · ID ${data.orderId} · Total ${formatMoney(data.totalPrice)}`
      )
      await loadProducts()
      await fetchOrderDetails(data.orderId)
      setIdempotencyKey(null)
    } catch (e: unknown) {
      setErrorMessage(extractProblemDetail(e) ?? extractErrorMessage(e))
    } finally {
      setPlacing(false)
    }
  }

  useEffect(() => {
    void loadProducts()
  }, [])

  return (
    <View className="page">
      <View className="hero">
        <View className="brandRow">
          <View className="col">
            <Text className="brandTitle">
              Mini Commerce
            </Text>
            <Text className="brandSub">Select a product, choose quantity, and place an order.</Text>
          </View>
          <View>
            <View
              className={cx('chipBtn', loadingProducts && 'chipBtnDisabled')}
              onClick={() => {
                if (!loadingProducts) void loadProducts()
              }}
            >
              <Text>Refresh</Text>
            </View>
          </View>
        </View>
        <Text className="tiny">Backend: {API_BASE_URL}</Text>
      </View>

      <Text className="sectionTitle">Products</Text>
      <View className="grid">
        {products.map(p => {
          const isSelected = selectedProductId === p.id
          const out = p.stock <= 0
          const low = p.stock > 0 && p.stock <= LOW_STOCK_THRESHOLD
          return (
            <View className={cx('card', isSelected && 'cardSelected')} key={p.id}>
              <View className="row">
                <View className="col">
                  <Text className="name">{p.name}</Text>
                  <View className="metaRow">
                    <Text className="price">{formatMoney(p.price)}</Text>
                    {out ? (
                      <Text className="badge badgeOut">Out of stock</Text>
                    ) : low ? (
                      <Text className="badge badgeLow">Low stock · {p.stock}</Text>
                    ) : (
                      <Text className="badge">In stock · {p.stock}</Text>
                    )}
                  </View>
                  <Text className="metaHint">Tap “Select” to configure quantity & checkout.</Text>
                </View>
                <View
                  className={cx('ghostBtn', out && 'ghostBtnDisabled')}
                  onClick={() => {
                    if (!out) selectProduct(p.id)
                  }}
                >
                  <Text>{isSelected ? 'Selected' : 'Select'}</Text>
                </View>
              </View>
            </View>
          )
        })}
      </View>

      {successMessage && (
        <View className="msg success">
          <Text>{successMessage}</Text>
          {orderDetails && (
            <View>
              <Text className="metaHint">
                Qty {orderDetails.quantity} · Unit {formatMoney(orderDetails.unitPrice)} ·{' '}
                Created {new Date(orderDetails.createdAt).toLocaleString()}
              </Text>
            </View>
          )}
        </View>
      )}
      {errorMessage && (
        <View className="msg error">
          <Text>{errorMessage}</Text>
        </View>
      )}

      <View className="orderBar">
        <View className="orderCard">
          <Text className="orderTitle">Checkout</Text>
          <Text className="orderSub">
            {selectedProduct
              ? `${selectedProduct.name} · ${formatMoney(selectedProduct.price)} · Stock ${selectedProduct.stock}`
              : 'Select a product to begin.'}
          </Text>

          <View className="stepper">
            <View
              className={`stepBtn ${!selectedProduct || qty <= 1 ? 'stepBtnDisabled' : ''}`}
              onClick={() => {
                if (selectedProduct && qty > 1) stepQty(-1)
              }}
            >
              <Text>-</Text>
            </View>
            {isWeb ? (
              // Use native input in H5 to avoid WebComponent input vertical alignment issues.
              <input
                className="qtyInputNative"
                type="number"
                inputMode="numeric"
                value={qtyInput}
                placeholder="Qty"
                onInput={(e: FormEvent<HTMLInputElement>) =>
                  setQtyInput((e.target as HTMLInputElement).value ?? '')
                }
                disabled={!selectedProduct}
              />
            ) : (
              <Input
                className="qtyInput"
                type="number"
                value={qtyInput}
                placeholder="Qty"
                onInput={e => setQtyInput(e.detail.value)}
                disabled={!selectedProduct}
              />
            )}
            <View
              className={`stepBtn ${!selectedProduct || (selectedProduct ? qty >= selectedProduct.stock : true) ? 'stepBtnDisabled' : ''}`}
              onClick={() => {
                if (selectedProduct && qty < selectedProduct.stock) stepQty(1)
              }}
            >
              <Text>+</Text>
            </View>
          </View>

          <View className="orderFooter">
            <Text className="total">
              Total:{' '}
              {selectedProduct && qty > 0
                ? formatMoney(selectedProduct.price * qty)
                : '—'}
            </Text>
            <View
              className={cx('primaryBtn', 'primaryBtnGold', !canPlace && 'primaryBtnDisabled')}
              onClick={() => {
                if (canPlace) void placeSelectedOrder()
              }}
            >
              <Text>{placing ? 'Placing…' : 'Place order'}</Text>
            </View>
          </View>
        </View>
      </View>
    </View>
  )
}


