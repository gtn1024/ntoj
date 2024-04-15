import type { FormInstance } from 'antd'
import { Button, Form, Input, InputNumber, Switch, message } from 'antd'
import React, { useEffect, useRef } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

interface Params {
  languageName: string
  compileCommand?: string
  executeCommand?: string
  enabled: boolean
  memoryLimitRate: number
  timeLimitRate: number
  sourceFilename: string
  targetFilename: string
}

export const AdminLanguageEditPage: React.FC = () => {
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const nav = useNavigate()
  const formRef = useRef<FormInstance>(null)
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<AdminDto.Language>(`/admin/language/${id}`)
        .then((res) => {
          formRef?.current?.setFieldsValue({
            languageName: res.data.data.languageName ?? '',
            compileCommand: res.data.data.compileCommand ?? '',
            executeCommand: res.data.data.executeCommand ?? '',
            memoryLimitRate: res.data.data.memoryLimitRate ?? 1,
            timeLimitRate: res.data.data.timeLimitRate ?? 1,
            sourceFilename: res.data.data.sourceFilename ?? '',
            targetFilename: res.data.data.targetFilename ?? '',
            enabled: res.data.data.enabled ?? false,
          })
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取语言失败')
          throw err
        })
    }
  }, [mode, id, formRef])

  const onSubmit = (v: Params) => {
    if (mode === '新建') {
      http.post<AdminDto.Language>('/admin/language', { ...v })
        .then(() => {
          void message.success('发布成功')
          nav('/admin/language')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '发布失败')
          throw err
        })
    } else {
      http.patch<void>(`/admin/language/${id ?? 0}`, { ...v })
        .then(() => {
          void message.success('修改成功')
          nav('/admin/language')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '修改失败')
          throw err
        })
    }
  }

  return (
    <div className="p-4">
      <Form
        name="basic"
        layout="vertical"
        onFinish={onSubmit}
        autoComplete="off"
        ref={formRef}
      >
        <Form.Item label="显示名称" rules={[{ required: true, message: '请输入名称！' }]} name="languageName">
          <Input />
        </Form.Item>

        <Form.Item label="编译命令" name="compileCommand">
          <Input />
        </Form.Item>

        <Form.Item label="运行命令" name="executeCommand">
          <Input />
        </Form.Item>

        <Form.Item label="时间倍率" name="timeLimitRate" initialValue={1}>
          <InputNumber min={1} max={10} />
        </Form.Item>

        <Form.Item label="空间倍率" name="memoryLimitRate" initialValue={1}>
          <InputNumber min={1} max={10} />
        </Form.Item>

        <Form.Item label="源代码文件名" name="sourceFilename">
          <Input />
        </Form.Item>

        <Form.Item label="产物文件名" name="targetFilename">
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

export default AdminLanguageEditPage
