import Taro from '@tarojs/taro'
import type { ProblemDetail } from '../types/types'
import { is2xx } from '../utils/common'

/**
 * API client for making HTTP requests
 */
export async function requestApiData<T>(
  opts: Parameters<typeof Taro.request>[0]
): Promise<T> {
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
  return res?.data as T
}

