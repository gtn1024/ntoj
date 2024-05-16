import React, { useEffect, useMemo, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
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
import { useRoles } from '../../hooks/useRoles.ts'

export const AdminUserEditPage: React.FC = () => {
  const nav = useNavigate()
  const { id } = useParams()
  const formRef = useRef<FormInstance>(null)
  const { roles } = useRoles()
  const rolesOption = useMemo(() => {
    if (!roles) {
      return []
    }
    return Object.keys(roles).filter(key => key !== 'guest').map(key => ({
      label: key,
      value: key,
    }))
  }, [roles])
  useEffect(() => {
    http.get<AdminDto.AdminUser>(`/admin/user/${id}`)
      .then((res) => {
        formRef?.current?.setFieldsValue({
          ...res.data.data,
        })
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取用户失败')
        throw err
      })
  }, [id, formRef])

  const onSubmit = (v: any) => {
    http.patch(`/admin/user/${id}/setRole?role=${v.userRole}`)
      .then(() => {
        void message.success('修改用户成功')
        nav('/admin/user')
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '修改用户失败')
        throw err
      })
  }

  return (
    <div className="h-[calc(100vh-64px)] w-full flex justify-between">
      <div className="w-full overflow-y-auto p-4">
        <h2 className="text-xl">
          修改用户
        </h2>
        <Form
          name="basic"
          layout="vertical"
          onFinish={onSubmit}
          autoComplete="off"
          ref={formRef}
        >
          <Form.Item label="用户名" name="username" className="grow">
            <Input disabled />
          </Form.Item>

          <Form.Item label="用户权限" name="userRole" initialValue="default">
            <Select
              options={rolesOption}
            />
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

export default AdminUserEditPage
