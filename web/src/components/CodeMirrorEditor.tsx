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
}

export const CodeMirrorEditor: React.FC<EditorProps> = (props) => {
  return (
    <>
      <div className={props.className}>
        <CodeMirror
          value={props.value}
          height={`${props.editorHeight}px`}
          theme="dark"
          onChange={props.setValue}
          extensions={[loadLanguage(props.language as LanguageName)!]}
        />
      </div>
    </>
  )
}
