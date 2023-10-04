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

export const CodeMirrorEditor: React.FC<EditorProps> = (props) => {
  return (
    <>
      <div className={props.className}>
        <CodeMirror
          style={{
            fontSize: `${props.fontSize}px` ?? '14px',
          }}
          value={props.value}
          height={`${props.editorHeight}px`}
          theme={props.theme}
          onChange={props.setValue}
          extensions={[loadLanguage(props.language as LanguageName)!]}
        />
      </div>
    </>
  )
}
