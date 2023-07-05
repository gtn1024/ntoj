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
  build: {
    chunkSizeWarningLimit: 750,
    rollupOptions: {
      output: {
        manualChunks: {
          'antd': ['antd', '@ant-design/icons'],
          'http': ['axios', 'swr'],
          'react': ['react', 'react-dom', 'react-router-dom'],
          'markdown': ['markdown-it'],
          'mathjax': ['markdown-it-mathjax3'],
          'editor': ['@wangeditor/editor', '@wangeditor/editor-for-react'],
        },
      },
    },
  },
}))
