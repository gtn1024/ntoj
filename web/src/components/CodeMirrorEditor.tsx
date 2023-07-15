import React from 'react'
import CodeMirror from '@uiw/react-codemirror'
import { cpp } from '@codemirror/lang-cpp'
import { python } from '@codemirror/lang-python'
import { java } from '@codemirror/lang-java'

interface Props {
  height?: string
  value: string
  setValue: (value: string) => void
}

export const CodeMirrorEditor: React.FC<Props> = (props) => {
  return (
    <>
      <div>
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
