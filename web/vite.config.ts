import {defineConfig, splitVendorChunkPlugin} from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig(({command}) => ({
  define: {
    isDev: command === 'serve'
  },
  plugins: [
    react(),
    splitVendorChunkPlugin(),
  ],
  server: {
    port: 2023,
    fs: {
      strict: true,
    },
    proxy: {
      '/api': {
        target: 'http://localhost:18080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
      '/upload': {
        target: 'http://localhost:18080',
        changeOrigin: true,
      },
    },
  },
}))
