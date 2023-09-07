import React, { useState } from 'react'
import { Outlet, useNavigate, useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'

interface MenuProps {
  items: {
    key: string
    label: string
  }[]
  current: string
  setCurrent: (key: string) => void
  onClick?: (item: { key: string; label: string }) => void
}

const MenuComponent: React.FC<MenuProps> = ({ items, current, setCurrent, onClick }) => {
  return (
    <div h-full flex items-center gap-1>
      {
        items.map(item => (
          <div
            key={item.key}
            px-2 cursor-pointer hover:text-blue-400
            className={current === item.key ? 'text-blue-500' : 'text-gray-500'}
            onClick={() => {
              setCurrent(item.key)
              onClick?.(item)
            }}
          >
            {item.label}
          </div>
        ))
      }
    </div>
  )
}

export const ContestLayout: React.FC = () => {
  const { id } = useParams()
  const nav = useNavigate()
  const [current, setCurrent] = useState('')
  const { data: contest } = useSWR(`/contest/${id}`, async (path) => {
    return http.get<Contest>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛失败')
        throw err
      })
  })
  const items: MenuProps['items'] = [
    { label: '首页', key: '' },
    { label: '题目', key: '/problems' },
  ]
  return (
    <div flex flex-col h-screen>
      <div h="[48px]" border-b="1px #dddddd solid">
        <div h-full flex items-center justify-between leading="[48px]">
          <div flex>
            <div bg="[#232c31]" text-white h-full px-4 mr-1>
              {contest?.title}
            </div>
            <MenuComponent items={items} current={current} setCurrent={setCurrent} onClick={(item) => {
              nav(`/c/${id}${item.key}`)
            }}/>
            <div
              px-2 cursor-pointer text-gray-600
              onClick={() => nav('/c') }
            >退出</div>
          </div>
          <div>
            user
          </div>
        </div>
      </div>
      <div grow>
        <Outlet/>
      </div>
    </div>
  )
}
