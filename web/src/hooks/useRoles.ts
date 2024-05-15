import useSWR from 'swr'
import { http } from '../lib/Http.tsx'

interface Role {
  name: string
  permission: string
}

export function useRoles() {
  const { data: roles, error, mutate, isLoading, isValidating } = useSWR(`/roles`, async (path) => {
    return http.get<Array<Role>>(path)
      .then((res) => {
        const data = res.data.data
        const r: Record<string, bigint> = {}
        for (const role of data) {
          r[role.name] = BigInt(role.permission)
        }
        return r
      })
  })

  return {
    roles,
    error,
    mutate,
    isLoading,
    isValidating,
  }
}
