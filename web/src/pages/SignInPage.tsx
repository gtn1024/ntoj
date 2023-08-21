import React from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import type { TabsProps } from 'antd'
import { Button, Form, Input, Tabs, message } from 'antd'
import type { AxiosError } from 'axios'
import c from 'classnames'
import type { HttpResponse } from '../lib/Http'
import { http } from '../lib/Http'
import { useUserStore } from '../stores/useUserStore'
import { setToken } from '../lib/token.ts'
import { useLayout } from '../hooks/useLayout.ts'

interface SignInProps {
  username: string
  password: string
}

interface SignUpProps {
  username: string
  password: string
  password2: string
  email: string
  realName: string
}

interface SignInResponse {
  token: string
  user: CurrentUser
}

export const SignInPage: React.FC = () => {
  const [search] = useSearchParams()
  const nav = useNavigate()
  const userStore = useUserStore()
  const redirect = search.get('redirect') || '/'
  const { isMobile } = useLayout()
  const login = async (props: SignInProps) => {
    const res = await http.get<SignInResponse>('/auth/login', { ...props })
    const { token, user } = res.data.data
    setToken(token)
    userStore.setUser(user)
  }
  const onSignInSubmit = (values: SignInProps) => {
    login(values)
      .then(() => {
        void message.success('登录成功')
        nav(redirect)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '登录失败')
        throw err
      })
  }

  const onSignUpSubmit = (values: SignUpProps) => {
    void http.post('/auth/signup', { ...values })
      .then(() => {
        login({ username: values.username, password: values.password })
          .then(() => {
            void message.success('注册成功')
            nav(redirect)
          })
          .catch((err: AxiosError<HttpResponse>) => {
            void message.error(err.response?.data.message ?? '登录失败')
            throw err
          })
          .catch((err: AxiosError<HttpResponse>) => {
            void message.error(err.response?.data.message ?? '注册失败')
            throw err
          })
      })
  }

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '登录',
      children: (
        <Form
          name="basic"
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 18 }}
          style={{ maxWidth: 600 }}
          onFinish={onSignInSubmit}
          autoComplete="off"
        >
          <Form.Item
            label="用户名"
            name="username"
            rules={[
              { required: true, message: '请输入用户名！' },
              { min: 6, message: '用户名至少为6位！' },
              { max: 16, message: '用户名最长为16位！' },
              { pattern: /^[a-zA-Z0-9]+$/, message: '用户名只能包含字母或数字！' },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="密码"
            name="password"
            rules={[{ required: true, message: '请输入密码！' }]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item wrapperCol={{ offset: 10, span: 16 }}>
             <Button type="primary" htmlType="submit">
              登录
             </Button>
          </Form.Item>
        </Form>
      ),
    },
    {
      key: '2',
      label: '注册',
      children: (
        <Form
          name="basic"
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 18 }}
          style={{ maxWidth: 600 }}
          onFinish={onSignUpSubmit}
          autoComplete="off"
        >
          <Form.Item
            label="用户名"
            name="username"
            rules={[
              { required: true, message: '请输入用户名！' },
              { min: 6, message: '用户名至少为6位！' },
              { max: 16, message: '用户名最长为16位！' },
              { pattern: /^[a-zA-Z0-9]+$/, message: '用户名只能包含字母或数字！' },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="密码"
            name="password"
            rules={[
              { required: true, message: '请输入密码！' },
              { min: 6, message: '密码至少为6位！' },
            ]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            label="确认密码"
            name="password2"
            rules={[
              {
                required: true,
                message: '请确认密码！',
              },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value)
                    return Promise.resolve()

                  return Promise.reject(new Error('两次密码不一致！'))
                },
              }),
            ]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            label="电子邮箱"
            name="email"
            rules={[
              { required: true, message: '请输入电子邮箱！' },
              { type: 'email', message: '你输入的不是合法的电子邮箱！' },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="真实姓名"
            name="realName"
            rules={[
              { required: true, message: '请输入真实姓名！' },
              { max: 4, message: '真实姓名最长为4个字符！' },
            ]}
          >
            <Input maxLength={4} />
          </Form.Item>

          <Form.Item wrapperCol={{ offset: 10, span: 16 }}>
            <Button type="primary" htmlType="submit">
              注册
            </Button>
          </Form.Item>
        </Form>
      ),
    },
  ]
  return (
    <div className={c('h-screen', 'flex', 'justify-center', 'items-center', !isMobile && 'bg-[#f5f5f5]')}>
       <div className={c('flex bg-white', !isMobile && 'rounded shadow')}>
        <div className={c('p-4', !isMobile && 'min-w-[480px]', !isMobile && 'min-h-[500px]')}>
          <div className="text-2xl font-bold text-center py-4">
            NTOJ
          </div>
          <div className="px-8">
            <Tabs defaultActiveKey="1" centered items={items} />
          </div>
        </div>
       </div>
    </div>
  )
}
