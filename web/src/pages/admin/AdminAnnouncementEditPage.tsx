import type { FormInstance } from 'antd'
import { Button, Form, Input, Switch, message } from 'antd'
import React, { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { RichEditor } from '../../components/RichEditor.tsx'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

interface Params {
  title: string
  content: string
  visible: boolean
}

export const AdminAnnouncementEditPage: React.FC = () => {
  const [content, setContent] = useState<string>('')
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const nav = useNavigate()
  const formRef = useRef<FormInstance>(null)
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<Announcement>(`/admin/announcement/${id}`)
        .then((res) => {
          formRef?.current?.setFieldsValue({
            title: res.data.data.title ?? '',
            visible: res.data.data.visible ?? false,
          })
          setContent(res.data.data.content ?? '')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取公告失败')
          throw err
        })
    }
  }, [mode, id, formRef])

  const onSubmit = (v: Params) => {
    if (mode === '新建') {
      http.post<Announcement>('/admin/announcement', { ...v, content })
        .then(() => {
          void message.success('发布成功')
          nav('/admin/announcement')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '发布失败')
          throw err
        })
    } else {
      http.patch<void>(`/admin/announcement/${id ?? 0}`, { ...v, content })
        .then(() => {
          void message.success('修改成功')
          nav('/admin/announcement')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '修改失败')
          throw err
        })
    }
  }

  return (
    <div p-4>
      <Form
        name="basic"
        layout='vertical'
        onFinish={onSubmit}
        autoComplete="off"
        ref={formRef}
      >
        <Form.Item label="标题" rules={[{ required: true, message: '请输入标题！' }]} name="title">
          <Input/>
        </Form.Item>

        <Form.Item
          label="内容"
          name="content"
        >
          <RichEditor height='49vh' data={content} setData={setContent}/>
        </Form.Item>

        <Form.Item
          label="是否可见"
          name="visible"
          valuePropName="checked"
        >
          <Switch/>
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit">
            {mode === '新建' ? '发布' : '修改'}
          </Button>
        </Form.Item>
      </Form>
    </div>
  )
}

export default AdminAnnouncementEditPage
