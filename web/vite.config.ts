import type { PluginOption } from 'vite'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import visualizer from 'rollup-plugin-visualizer'
import UnoCSS from 'unocss/vite'

// https://vitejs.dev/config/
export default defineConfig(({ command }) => ({
  define: {
    isDev: command === 'serve',
  },
  plugins: [
    UnoCSS(),
    react(),
    visualizer() as PluginOption,
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
        rewrite: path => path.replace(/^\/api/, ''),
      },
      '/upload': {
        target: 'http://localhost:18080',
        changeOrigin: true,
      },
    },
  },
}))
