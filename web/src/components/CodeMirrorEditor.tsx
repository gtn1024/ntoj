import React from 'react'
import CodeMirror from '@uiw/react-codemirror'
import { cpp } from '@codemirror/lang-cpp'
import { python } from '@codemirror/lang-python'
import { java } from '@codemirror/lang-java'

interface EditorProps {
  height?: string
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
          height={props.height}
          theme="dark"
          onChange={props.setValue}
          extensions={[cpp(), java(), python()]}
        />
      </div>
    </>
  )
}
