import { useLocalStorage } from 'react-use'

interface CodemirrorConfig {
  theme?: 'light' | 'dark'
  fontSize?: number
}

export function useCodemirrorConfig() {
  const [codemirrorConfig, setCodemirrorConfig] = useLocalStorage<CodemirrorConfig>('codemirror-config', {
    theme: 'light',
    fontSize: 14,
  })
  return { codemirrorConfig, setCodemirrorConfig }
}
