import React, { useEffect, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { FormInstance } from 'antd'
import {
  Button,
  Form,
  Input,
  message,
} from 'antd'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

export const AdminArticleEditPage: React.FC = () => {
  const nav = useNavigate()
  const { id } = useParams()
  const formRef = useRef<FormInstance>(null)
  useEffect(() => {
    if (id) {
      http.get<AdminDto.Article>(`/admin/article/${id}`)
        .then((res) => {
          formRef.current?.setFieldsValue({
            title: res.data.data.title,
            content: res.data.data.content,
          })
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取文章失败')
          throw err
        })
    }
  }, [id, formRef])

  const onSubmit = (v: any) => {
    http.patch(`/admin/article/${id}`, { ...v })
      .then(() => {
        void message.success('修改文章成功')
        nav('/admin/article')
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '修改文章失败')
        throw err
      })
  }
  return (
    <div className="h-[calc(100vh-64px)] w-full flex justify-between">
      <div className="w-full overflow-y-auto p-4">
        <h2 className="text-xl">
          修改文章
        </h2>
        <Form
          name="basic"
          layout="vertical"
          onFinish={onSubmit}
          autoComplete="off"
          ref={formRef}
        >
          <Form.Item label="标题" rules={[{ required: true, message: '请输入标题！' }]} name="title" className="grow">
            <Input />
          </Form.Item>

          <Form.Item label="内容" rules={[{ required: true, message: '请输入内容！' }]} name="content" className="grow">
            <Input.TextArea rows={20} />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              修改
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  )
}
