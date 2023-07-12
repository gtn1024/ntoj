import React, { useEffect, useState } from 'react'
import {
  AlertOutlined,
  HomeOutlined,
  MenuFoldOutlined, MenuOutlined,
  MenuUnfoldOutlined, QuestionOutlined,
} from '@ant-design/icons'
import type { MenuProps } from 'antd'
import { Button, Dropdown, Layout, Menu, theme } from 'antd'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useUserStore } from '../stores/useUserStore.tsx'
import { useLayout } from '../hooks/useLayout.ts'

const { Header, Sider, Content } = Layout

export const AdminLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)
  const [current, setCurrent] = useState('')
  const { isMobile, breakpoint } = useLayout()
  useEffect(() => {
    setCollapsed(breakpoint === 'sm')
  }, [breakpoint])

  const { pathname } = useLocation()
  const {
    token: { colorBgContainer },
  } = theme.useToken()
  const { user } = useUserStore()
  const nav = useNavigate()
  const items: MenuProps['items'] = [
    {
      key: '/admin',
      icon: <HomeOutlined/>,
      label: '首页',
    },
    {
      key: '/admin/announcement',
      icon: <AlertOutlined/>,
      label: '公告',
    },
    {
      key: '/admin/problem',
      icon: <QuestionOutlined/>,
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
    if (pathname.includes('/admin')) {
      return setCurrent('/admin')
    }
    return setCurrent('')
  }, [pathname])

  const onClickMenu: MenuProps['onClick'] = (e) => {
    nav(e.key)
  }
  return (
    <Layout className="h-screen">
      {
        !isMobile && (
          <Sider theme="light" trigger={null} collapsible collapsed={collapsed}>
            {/* <div className="logo" /> */}
            <Menu
              style={{ height: '100vh', overflowY: 'auto' }}
              mode="inline"
              selectedKeys={[current]}
              items={items}
              onClick={onClickMenu}
            />
          </Sider>
        )
      }
      <Layout className="flex">
        <Header className="flex justify-between" style={{ padding: 0, background: colorBgContainer }}>
          {
            !isMobile
              ? (
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
                )
              : (
                <Dropdown menu={{
                  items,
                  onClick: onClickMenu,
                  selectable: true,
                  selectedKeys: [current],
                }} trigger={['click']}>
                  <Button
                    type="text"
                    icon={<MenuOutlined/>}
                    style={{
                      fontSize: '16px',
                      width: 64,
                      height: 64,
                    }}
                  />
                </Dropdown>
                )
          }
          <div className="mr-4">
            <span className="mr-2">{user.username}</span>
            <Link to='/'>
              返回主站
            </Link>
          </div>
        </Header>
        <Content
          style={{
            padding: 24,
            minHeight: 280,
            background: colorBgContainer,
            overflowY: 'auto',
          }}
        >
          <Outlet/>
        </Content>
      </Layout>
    </Layout>
  )
}
