import React from 'react'
import ReactDOM from 'react-dom/client'
import { App } from './App.tsx'

const div = document.getElementById('root') as HTMLElement

const root = ReactDOM.createRoot(div)
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
