import { useMemo } from 'react'
import { useUserStore } from '../stores/useUserStore.tsx'

export function useUserPermission() {
  const userStore = useUserStore()
  const user = useMemo(() => {
    return userStore.user
  }, [userStore])
  return useMemo(() => {
    return user.iPermission || 0n
  }, [user])
}
