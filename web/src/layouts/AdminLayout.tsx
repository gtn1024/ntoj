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
  const items = [
    {
      key: '/admin',
      label: '首页',
    },
    {
      key: '/admin/announcement',
      label: '公告',
    },
    {
      key: '/admin/group',
      label: '小组',
    },
    {
      key: '/admin/problem',
      label: '题目',
    },
    {
      key: '/admin/homework',
      label: '作业',
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
      key: '/admin/role',
      label: '角色',
    },
    {
      key: '/admin/permission',
      label: '权限',
    },
  ]

  useEffect(() => {
    for (const item of items) {
      if (item.key === '/admin') {
        continue
      }
      if (pathname.includes(item.key)) {
        return setCurrent(item.key)
      }
    }
    return setCurrent('/admin')
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
