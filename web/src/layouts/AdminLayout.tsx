import React, { useState } from 'react'
import {
  AlertOutlined,
  HomeOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined, QuestionOutlined,
} from '@ant-design/icons'
import type { MenuProps } from 'antd'
import { Button, Layout, Menu, Space, theme } from 'antd'
import { Link, Outlet, useNavigate } from 'react-router-dom'
import { useUserStore } from '../stores/useUserStore.tsx'
import s from './AdminLayout.module.scss'

const { Header, Sider, Content } = Layout

export const AdminLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)
  const {
    token: { colorBgContainer },
  } = theme.useToken()
  const { user } = useUserStore()
  const nav = useNavigate()
  const items: MenuProps['items'] = [
    {
      key: '',
      icon: <HomeOutlined/>,
      label: '首页',
    },
    {
      key: 'announcement',
      icon: <AlertOutlined />,
      label: '公告',
    },
    {
      key: 'problem',
      icon: <QuestionOutlined />,
      label: '题目',
    },
  ]
  const onClickMenu: MenuProps['onClick'] = (e) => {
    nav(`/admin/${e.key}`)
  }
  return (
    <Layout className={s.layout}>
      <Sider theme="light" trigger={null} collapsible collapsed={collapsed}>
        {/* <div className="logo" /> */}
        <Menu
          mode="inline"
          defaultSelectedKeys={['1']}
          items={items}
          onClick={onClickMenu}
        />
      </Sider>
      <Layout>
        <Header className={s.header} style={{ padding: 0, background: colorBgContainer }}>
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
          <div className={s.user}>
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
