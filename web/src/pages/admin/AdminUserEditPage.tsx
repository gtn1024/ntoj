import React, { useEffect, useRef } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { FormInstance } from 'antd'
import {
  Button,
  Form,
  Input,
  Select,
  message,
} from 'antd'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

export const AdminUserEditPage: React.FC = () => {
  const nav = useNavigate()
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const formRef = useRef<FormInstance>(null)
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<AdminDto.User>(`/admin/user/${id}`)
        .then((res) => {
          formRef?.current?.setFieldsValue({
            ...res.data.data,
          })
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取用户失败')
          throw err
        })
    }
  }, [mode, id, formRef])

  const onSubmit = (v: any) => {
    const params = {
      ...v,
    }
    if (mode === '新建') {
      http.post('/admin/user', { ...params })
        .then(() => {
          void message.success('新建用户成功')
          nav('/admin/user')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '新建用户失败')
          throw err
        })
    } else {
      http.patch(`/admin/user/${id}`, { ...params })
        .then(() => {
          void message.success('修改用户成功')
          nav('/admin/user')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '修改用户失败')
          throw err
        })
    }
  }

  return (
    <>
      <div h="[calc(100vh-64px)]" w-full flex justify-between>
        <div w-full overflow-y-auto p-4>
          <h2 text-xl>
            {mode}
            用户
          </h2>
          <Form
            name="basic"
            layout="vertical"
            onFinish={onSubmit}
            autoComplete="off"
            ref={formRef}
          >
            <Form.Item label="用户名" rules={[{ required: true, message: '请输入用户名！' }]} name="username" className="grow">
              <Input />
            </Form.Item>

            <Form.Item
              label={mode === '新建' ? '密码' : '修改密码'}
              rules={mode === '新建' ? [{ required: true, message: '请输入密码！' }] : undefined}
              name="password"
              className="grow"
            >
              <Input.Password />
            </Form.Item>

            <Form.Item label="真实姓名" rules={[{ required: true, message: '请输入真实姓名！' }]} name="realName" className="grow">
              <Input />
            </Form.Item>

            <Form.Item label="电子邮箱" rules={[{ required: true, message: '请输入电子邮箱！' }]} name="email" className="grow">
              <Input />
            </Form.Item>

            <Form.Item label="用户权限" name="role" initialValue="USER">
              <Select
                options={[
                  { value: 'BANNED', label: '已封禁' },
                  { value: 'USER', label: '普通用户' },
                  { value: 'ADMIN', label: '管理员' },
                  { value: 'SUPER_ADMIN', label: '超级管理员' },
                ]}
              />
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit">
                {mode === '新建' ? '新建' : '修改'}
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
    </>
  )
}

export default AdminUserEditPage
