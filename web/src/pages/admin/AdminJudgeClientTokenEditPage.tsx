import type { FormInstance } from 'antd'
import { Button, Form, Input, Switch, message } from 'antd'
import React, { useEffect, useRef } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

interface Params {
  name: string
}

export const AdminJudgeClientTokenEditPage: React.FC = () => {
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const nav = useNavigate()
  const formRef = useRef<FormInstance>(null)
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<AdminDto.JudgeClientToken>(`/admin/judge_client_token/${id}`)
        .then((res) => {
          formRef?.current?.setFieldsValue({
            name: res.data.data.name ?? '',
            enabled: res.data.data.enabled ?? false,
          })
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取Token失败')
          throw err
        })
    }
  }, [mode, id, formRef])

  const onSubmit = (v: Params) => {
    if (mode === '新建') {
      http.post<AdminDto.JudgeClientToken>('/admin/judge_client_token', { ...v })
        .then(() => {
          void message.success('发布成功')
          nav('/admin/judge_client_token')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '发布失败')
          throw err
        })
    } else {
      http.patch<void>(`/admin/judge_client_token/${id ?? 0}`, { ...v })
        .then(() => {
          void message.success('修改成功')
          nav('/admin/judge_client_token')
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
        layout="vertical"
        onFinish={onSubmit}
        autoComplete="off"
        ref={formRef}
      >
        <Form.Item label="名称" rules={[{ required: true, message: '请输入名称！' }]} name="name">
          <Input />
        </Form.Item>

        <Form.Item
          label="是否启用"
          name="enabled"
          valuePropName="checked"
        >
          <Switch />
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

export default AdminJudgeClientTokenEditPage
