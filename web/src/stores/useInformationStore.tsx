import { create } from 'zustand'

export interface Information {
  name?: string

  beian?: string
}

interface InformationStore {
  information: Information
  setInformation: (information: Partial<Information>) => void
}

export const useInformationStore = create<InformationStore>(set => ({
  information: {},
  setInformation: (information) => {
    set(state => ({
      ...state,
      information: {
        ...state.information,
        ...information,
      },
    }))
  },
}))
