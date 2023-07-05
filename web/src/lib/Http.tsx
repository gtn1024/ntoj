import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosRequestHeaders, AxiosResponse } from 'axios'
import axios from 'axios'
import { message } from 'antd'
import { clearToken, getToken } from './token.ts'

export interface HttpResponse<T = unknown> {
  code: number
  message: string
  data: T
}

export interface L<T = unknown> {
  total: number
  page: number
  list: T[]
}

export type JSONValue = string | number | null | boolean | JSONValue[] | { [key: string]: JSONValue }

export class Http {
  instance: AxiosInstance

  constructor(baseURL: string) {
    this.instance = axios.create({
      baseURL,
    })
  }

  get<R = unknown>(
    url: string,
    query?: Record<string, string | boolean | number | undefined>,
    config?: Omit<AxiosRequestConfig, 'url' | 'params' | 'method'>,
  ) {
    return this.instance.request<HttpResponse<R>>({ url, params: query, method: 'GET', ...config })
  }

  post<R = unknown>(
    url: string,
    data?: Record<string, JSONValue> | FormData,
    config?: Omit<AxiosRequestConfig, 'url' | 'data' | 'method'>,
  ) {
    return this.instance.request<HttpResponse<R>>({ url, data, method: 'POST', ...config })
  }

  patch<R = unknown>(
    url: string,
    data?: Record<string, JSONValue>,
    config?: Omit<AxiosRequestConfig, 'url' | 'data' | 'method'>,
  ) {
    return this.instance.request<HttpResponse<R>>({ url, data, method: 'PATCH', ...config })
  }

  delete<R = unknown>(
    url: string,
    query?: Record<string, string>,
    config?: Omit<AxiosRequestConfig, 'url' | 'params' | 'method'>,
  ) {
    return this.instance.request<HttpResponse<R>>({ url, params: query, method: 'DELETE', ...config })
  }
}

export const http = new Http('/api')
http.instance.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    if (!config.headers)
      config.headers = {} as AxiosRequestHeaders

    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
http.instance.interceptors.response.use(
  (response: AxiosResponse<HttpResponse | Blob>) => {
    if (response.data instanceof Blob) {
      // download file
      const url = window.URL.createObjectURL(response.data)
      const link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      // eslint-disable-next-line @typescript-eslint/no-unsafe-argument, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      link.setAttribute('download', (response.headers['content-disposition']).split(';')[1].split('=')[1])
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }
    return response
  },
  (error: AxiosError<HttpResponse>) => {
    if (error.response?.status === 401) {
      void message.error(error.response?.data.message ?? 'Unauthorized')
      clearToken()
    }

    throw error
  },
)
