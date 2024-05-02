import React from 'react'
import CodeMirror from '@uiw/react-codemirror'
import { loadLanguage } from '@uiw/codemirror-extensions-langs'
import type { LanguageName } from '@uiw/codemirror-extensions-langs/src'

interface EditorProps {
  language: string
  editorHeight?: number
  value: string
  setValue: (value: string) => void
  className?: string
  theme?: 'light' | 'dark'
  fontSize?: number
}

export const CodeMirrorEditor: React.FC<EditorProps> = ({ language, editorHeight, value, setValue, className, theme, fontSize }) => {
  return (
    <div className={className}>
      <CodeMirror
        style={{
          fontSize: `${fontSize}px` ?? '14px',
        }}
        value={value}
        height={`${editorHeight}px`}
        theme={theme}
        onChange={setValue}
        extensions={[loadLanguage(language as LanguageName)!]}
      />
    </div>
  )
}
