import React, { useState } from 'react'
import { HomeOutlined, QuestionOutlined, SettingOutlined } from '@ant-design/icons'
import type { MenuProps } from 'antd'
import { Menu } from 'antd'
import { useNavigate } from 'react-router-dom'

interface Props {
  className?: string
}

const items: MenuProps['items'] = [
  {
    label: '首页',
    key: '/',
    icon: <HomeOutlined />,
  },
  {
    label: '题库',
    key: '/problem',
    icon: <QuestionOutlined />,
  },
  {
    label: '关于',
    key: '/about',
    icon: <SettingOutlined />,
  },
]

export const MenuComponent: React.FC<Props> = (props) => {
  const [current, setCurrent] = useState('/')
  const nav = useNavigate()

  const onClick: MenuProps['onClick'] = (e) => {
    setCurrent(e.key)
    nav(e.key)
  }

  return <Menu className={props.className} onClick={onClick} selectedKeys={[current]} mode="horizontal" items={items} />
}
