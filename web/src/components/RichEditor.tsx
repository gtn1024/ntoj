import React, { useEffect, useState } from 'react'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-react'
import type { IDomEditor, IEditorConfig } from '@wangeditor/editor'
import { http } from '../lib/Http.tsx'

interface Props {
  mode?: 'simple' | 'default'
  height?: string | number
  data: string
  setData: (data: string) => void
}

interface FileUpload {
  filename: string
  url: string
  hash: string
}
export const RichEditor: React.FC<Props> = (props) => {
  const [editor, setEditor] = useState<IDomEditor | null>(null) // TS 语法

  useEffect(() => {
    return () => {
      if (editor == null)
        return
      editor.destroy()
      setEditor(null)
    }
  }, [editor])
  const editorConfig: Partial<IEditorConfig> = {
    MENU_CONF: {
      uploadImage: {
        async customUpload(file: File, insertFn: (url: string, alt?: string, href?: string) => void) { // TS 语法
          const formData = new FormData()
          formData.append('file', file)
          const res = await http.post<FileUpload>('/admin/file', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          })
          insertFn(res.data.data.url)
        },
      },
    },
  }

  return (
    <>
      <div style={{ border: '1px solid #ccc' }}>
        <Toolbar
          editor={editor}
          mode={props.mode ?? 'simple'}
          style={{ borderBottom: '1px solid #ccc' }}
        />
        <Editor
          value={props.data}
          defaultConfig={editorConfig}
          onCreated={setEditor}
          onChange={editor => props.setData(editor.getHtml())}
          mode="default"
          style={{ height: props.height }}
        />
      </div>
    </>
  ) }
