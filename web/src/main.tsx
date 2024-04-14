import React from 'react'
import ReactDOM from 'react-dom/client'
import { App } from './App.tsx'
import './main.scss'
import 'virtual:uno.css'
import { NProgress } from './components/NProgress.tsx'

const div = document.getElementById('root') as HTMLElement

// 屏蔽浏览器的保存快捷键
document.onkeydown = (e) => {
  if (e.key === 's' && (e.ctrlKey || e.metaKey)) {
    e.preventDefault()
  }
}

const root = ReactDOM.createRoot(div)
root.render(
  <React.StrictMode>
    <React.Suspense fallback={<NProgress />}>
      <App />
    </React.Suspense>
  </React.StrictMode>,
)
