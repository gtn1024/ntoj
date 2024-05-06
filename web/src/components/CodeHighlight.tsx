import React, { useEffect, useState } from 'react'
import { codeToHtml } from 'shiki'

export const CodeHighlight: React.FC<{ code?: string, lang?: string }> = ({ code, lang }) => {
  const [html, setHtml] = useState<string>('')

  useEffect(() => {
    if (!code)
      return
    codeToHtml(code, {
      lang: lang || 'plaintext',
      theme: 'vitesse-light',
    })
      .then((html) => {
        setHtml(html)
      })
  }, [code, lang])

  return (
    <div dangerouslySetInnerHTML={{ __html: html }} />
  )
}
