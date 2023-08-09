import type { FormInstance } from 'antd'
import { Button, Form, Input, Select, Switch, message } from 'antd'
import React, { useEffect, useRef } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

const { Option } = Select

interface Params {
  languageName: string
  compileCommand?: string
  executeCommand?: string
  type: LanguageType
  enabled: boolean
}

export const AdminLanguageEditPage: React.FC = () => {
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const nav = useNavigate()
  const formRef = useRef<FormInstance>(null)
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<Language>(`/admin/language/${id}`)
        .then((res) => {
          formRef?.current?.setFieldsValue({
            languageName: res.data.data.languageName ?? '',
            compileCommand: res.data.data.compileCommand ?? '',
            executeCommand: res.data.data.executeCommand ?? '',
            type: res.data.data.type ?? 'CPP',
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
      http.post<Language>('/admin/language', { ...v })
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
        <div className='p-4'>
            <Form
                name="basic"
                layout='vertical'
                onFinish={onSubmit}
                autoComplete="off"
                ref={formRef}
            >
                <Form.Item label="显示名称" rules={[{ required: true, message: '请输入名称！' }]} name="languageName">
                    <Input/>
                </Form.Item>

                <Form.Item label="编译命令" name="compileCommand">
                    <Input/>
                </Form.Item>

                <Form.Item label="运行命令" name="executeCommand">
                    <Input/>
                </Form.Item>

                <Form.Item label="类型" rules={[{ required: true, message: '请选择类型！' }]} name="type">
                    <Select>
                        <Option value="CPP">CPP</Option>
                        <Option value="JAVA">JAVA</Option>
                        <Option value="PYTHON">PYTHON</Option>
                        <Option value="OTHER">OTHER</Option>
                    </Select>
                </Form.Item>

                <Form.Item
                    label="是否启用"
                    name="enabled"
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
