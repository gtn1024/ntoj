import React, { useEffect, useState } from 'react'
import { HomeOutlined, MenuOutlined, QuestionOutlined, SettingOutlined } from '@ant-design/icons'
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
    icon: <HomeOutlined/>,
  },
  {
    label: '题库',
    key: '/problem',
    icon: <QuestionOutlined/>,
  },
  {
    label: '记录',
    key: '/record',
    icon: <QuestionOutlined/>,
  },
  {
    label: '关于',
    key: '/about',
    icon: <SettingOutlined/>,
  },
]

export const MenuComponent: React.FC<Props> = (props) => {
  const [current, setCurrent] = useState<string>('')
  const nav = useNavigate()
  const { isMobile } = useLayout()

  const { pathname } = useLocation()

  useEffect(() => {
    if (pathname.includes('/problem')) {
      return setCurrent('/problem')
    }
    if (pathname.includes('/record')) {
      return setCurrent('/record')
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
            icon={<MenuOutlined/>}
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
