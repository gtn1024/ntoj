import React from 'react'
import { mdit } from '../lib/mdit.ts'

interface Props {
  data: string
}

export const MarkdownArticle: React.FC<Props> = (props) => {
  const html = mdit.render(props.data)
  return (
    <>
      <div dangerouslySetInnerHTML={{ __html: html }} />
    </>
  )
}
