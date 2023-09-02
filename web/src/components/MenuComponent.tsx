import React, { useEffect, useState } from 'react'
import type { MenuProps } from 'antd'
import { Button, Dropdown, Menu } from 'antd'
import { useLocation, useNavigate } from 'react-router-dom'
import { useLayout } from '../hooks/useLayout.ts'

interface Props {
  className?: string
}

const items: MenuProps['items'] = [
  {
    label: '首页',
    key: '/',
  },
  {
    label: '题库',
    key: '/p',
  },
  {
    label: '竞赛',
    key: '/c',
  },
  {
    label: '记录',
    key: '/r',
  },
  {
    label: '关于',
    key: '/about',
  },
]

export const MenuComponent: React.FC<Props> = (props) => {
  const [current, setCurrent] = useState<string>('')
  const nav = useNavigate()
  const { isMobile } = useLayout()

  const { pathname } = useLocation()

  useEffect(() => {
    if (pathname.includes('/p')) {
      return setCurrent('/p')
    }
    if (pathname.includes('/r')) {
      return setCurrent('/r')
    }
    if (pathname.includes('/c')) {
      return setCurrent('/c')
    }
    if (pathname.includes('/about')) {
      return setCurrent('/about')
    }
    if (pathname.includes('/')) {
      return setCurrent('/')
    }
    return setCurrent('')
  }, [pathname])

  const onClick: MenuProps['onClick'] = (e) => {
    setCurrent(e.key)
    nav(e.key)
  }

  return (
    <div className={props.className}>
      {!isMobile
        ? (
          <Menu className={props.className} onClick={onClick} selectedKeys={[current]} mode="horizontal"
                items={items}/>
          )
        : (<Dropdown menu={{
            items,
            onClick,
            selectable: true,
            selectedKeys: [current],
          }} trigger={['click']}>
          <Button
            type="text"
            icon={<div className="i-material-symbols:menu h-[20px] w-[20px]"/> }
            style={{
              fontSize: '16px',
              width: 64,
              height: 64,
            }}
          />
        </Dropdown>)
      }
    </div>)
}
