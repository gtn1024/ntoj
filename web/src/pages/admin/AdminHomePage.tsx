import React from 'react'
import { MarkdownArticle } from '../../components/MarkdownArticle.tsx'

export const AdminHomePage: React.FC = () => {
  const data = `## Welcome to NTOJ`
  return (
    <div className="p-4">
      <MarkdownArticle data={data} />
    </div>
  )
}

export default AdminHomePage
