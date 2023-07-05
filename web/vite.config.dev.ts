import type { UserConfigExport } from 'vite'
import { mergeConfig } from 'vite'
import baseConfig from './vite.config'

export default mergeConfig(
  {
    mode: 'development',
    server: {
      fs: {
        strict: true,
      },
      proxy: {
        '/api': {
          target: 'http://localhost:18080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
        '/assets': {
          target: 'http://localhost:18080',
          changeOrigin: true,
        },
      },
    },
  } as UserConfigExport,
  baseConfig,
)
