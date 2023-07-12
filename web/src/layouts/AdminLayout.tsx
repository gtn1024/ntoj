import React, { useEffect, useState } from 'react'
import {
  AlertOutlined,
  HomeOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined, QuestionOutlined,
} from '@ant-design/icons'
import type { MenuProps } from 'antd'
import { Button, Layout, Menu, Space, theme } from 'antd'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useUserStore } from '../stores/useUserStore.tsx'

const { Header, Sider, Content } = Layout

export const AdminLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)
  const [current, setCurrent] = useState('')

  const { pathname } = useLocation()
  const {
    token: { colorBgContainer },
  } = theme.useToken()
  const { user } = useUserStore()
  const nav = useNavigate()
  const items: MenuProps['items'] = [
    {
      key: '/admin/',
      icon: <HomeOutlined/>,
      label: '首页',
    },
    {
      key: '/admin/announcement',
      icon: <AlertOutlined />,
      label: '公告',
    },
    {
      key: '/admin/problem',
      icon: <QuestionOutlined />,
      label: '题目',
    },
  ]

  useEffect(() => {
    if (pathname.includes('/admin/announcement')) {
      return setCurrent('/admin/announcement')
    }
    if (pathname.includes('/admin/problem')) {
      return setCurrent('/admin/problem')
    }
    if (pathname.includes('/admin/')) {
      return setCurrent('/admin/')
    }
    return setCurrent('')
  }, [pathname])

  const onClickMenu: MenuProps['onClick'] = (e) => {
    nav(e.key)
  }
  return (
    <Layout className="min-h-screen">
      <Sider theme="light" trigger={null} collapsible collapsed={collapsed}>
        {/* <div className="logo" /> */}
        <Menu
          mode="inline"
          selectedKeys={[current]}
          items={items}
          onClick={onClickMenu}
        />
      </Sider>
      <Layout className="flex">
        <Header className="flex justify-between" style={{ padding: 0, background: colorBgContainer }}>
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined/> : <MenuFoldOutlined/>}
            onClick={() => setCollapsed(!collapsed)}
            style={{
              fontSize: '16px',
              width: 64,
              height: 64,
            }}
          />
          <div className="mr-4">
            <Space>
              <span> {user.username} </span>
              <Link to='/'>
                返回主站
              </Link>
            </Space>
          </div>
        </Header>
        <Content
          style={{
            margin: '24px 16px',
            padding: 24,
            minHeight: 280,
            background: colorBgContainer,
          }}
        >
          <Outlet/>
        </Content>
      </Layout>
    </Layout>
  )
}
