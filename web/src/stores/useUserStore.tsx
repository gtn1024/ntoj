import { create } from 'zustand'

interface UserStore {
  user: CurrentUser
  setUser: (user: Partial<CurrentUser>) => void
  clearUser: () => void
}

export const useUserStore = create<UserStore>(set => ({
  user: {},
  setUser: (user) => {
    set(state => ({
      ...state,
      user: {
        ...state.user,
        ...user,
        iPermission: BigInt(user.permission || '0'),
      },
    }))
  },
  clearUser: () => {
    set(state => ({
      ...state,
      user: {},
    }))
  },
}))
