import React, { useEffect, useState } from 'react'
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
      label: '首页',
    },
    {
      key: '/admin/announcement',
      label: '公告',
    },
    {
      key: '/admin/problem',
      label: '题目',
    },
    {
      key: '/admin/contest',
      label: '竞赛',
    },
    {
      key: '/admin/article',
      label: '文章',
    },
    {
      key: '/admin/user',
      label: '用户',
    },
    {
      key: '/admin/language',
      label: '语言',
    },
    {
      key: '/admin/judge_client_token',
      label: '评测机',
    },
  ]

  if (user.role !== 'SUPER_ADMIN') {
    items.pop()
    items.pop()
    items.pop()
  }

  useEffect(() => {
    if (pathname.includes('/admin/announcement')) {
      return setCurrent('/admin/announcement')
    }
    if (pathname.includes('/admin/problem')) {
      return setCurrent('/admin/problem')
    }
    if (pathname.includes('/admin/contest')) {
      return setCurrent('/admin/contest')
    }
    if (pathname.includes('/admin/article')) {
      return setCurrent('/admin/article')
    }
    if (pathname.includes('/admin/user')) {
      return setCurrent('/admin/user')
    }
    if (pathname.includes('/admin/language')) {
      return setCurrent('/admin/language')
    }
    if (pathname.includes('/admin/judge_client_token')) {
      return setCurrent('/admin/judge_client_token')
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
                  icon={collapsed
                    ? <div className="i-mdi:menu-close h-[20px] w-[20px]" />
                    : <div className="i-mdi:menu-open h-[20px] w-[20px]" />}
                  onClick={() => setCollapsed(!collapsed)}
                  style={{
                    fontSize: '16px',
                    width: 64,
                    height: 64,
                  }}
                />
                )
              : (
                <Dropdown
                  menu={{
                    items,
                    onClick: onClickMenu,
                    selectable: true,
                    selectedKeys: [current],
                  }}
                  trigger={['click']}
                >
                  <Button
                    type="text"
                    icon={<div className="i-material-symbols:menu h-[20px] w-[20px]" />}
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
            <Link to="/">
              返回主站
            </Link>
          </div>
        </Header>
        <Content
          style={{
            minHeight: 280,
            background: colorBgContainer,
            overflowY: 'auto',
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}
