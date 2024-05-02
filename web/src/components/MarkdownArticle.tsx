import React from 'react'
import { mdit } from '../lib/mdit.ts'

interface Props {
  data: string
}

export const MarkdownArticle: React.FC<Props> = ({ data }) => {
  const html = mdit.render(data)
  return (
    <div dangerouslySetInnerHTML={{ __html: html }} />
  )
}
