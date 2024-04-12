import React from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import type { MenuProps } from 'antd'
import { Dropdown, message } from 'antd'
import { useUserStore } from '../stores/useUserStore'
import { clearToken } from '../lib/token.ts'

interface Props {
  className?: string
}

export const AccountComponent: React.FC<Props> = (props) => {
  const location = useLocation()
  const { user, clearUser } = useUserStore()
  const nav = useNavigate()
  const redirect = encodeURIComponent(`${location.pathname}${location.search}`)
  const logout = () => {
    clearToken()
    clearUser()
    void message.success('退出登录成功')
    nav(`/sign_in?redirect=${redirect}`)
  }
  let items: MenuProps['items'] = [
    {
      key: 'user-center',
      label: (
        <Link to={`/u/${user.username ?? ''}`}>
          个人中心
        </Link>
      ),
    },
    {
      key: 'admin',
      label: (
        <Link to="/admin">
          后台管理
        </Link>
      ),
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      label: (
        <span onClick={logout}>
          退出登录
        </span>
      ),
    },
  ]
  if (user.role !== 'ADMIN' && user.role !== 'SUPER_ADMIN') {
    items = items?.filter(item => item?.key !== 'admin')
  }
  return (
    <div className={props.className}>
      {user.id
        ? (
          <Dropdown menu={{ items }}>
            <a onClick={e => e.preventDefault()}>
              {user.username}
              <div className="i-material-symbols:keyboard-arrow-down" w="[16px]" h="[16px]" />
            </a>
          </Dropdown>
          )
        : (
          <Link to={{
            pathname: '/sign_in',
            search: `redirect=${location.pathname}`,
          }}
          >
            登录 | 注册
          </Link>
          )}
    </div>
  )
}
