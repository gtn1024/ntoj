import useSWR from 'swr'
import { http } from '../lib/Http.tsx'

interface LanguageStructure {
  display: string
  execute: string
  highlight?: string
  compileTimeLimit?: number
  disabled: boolean
  compile?: string
  editor?: string
  source: string
  target: string
  timeLimitRate: number
  memoryLimitRate: number
}

export function useLanguages() {
  const { data: languages, error, mutate, isLoading, isValidating } = useSWR(`/language`, async (path) => {
    return http.get<Record<string, LanguageStructure>>(path)
      .then((res) => {
        return res.data.data
      })
  })

  return {
    languages,
    error,
    mutate,
    isLoading,
    isValidating,
  }
}
