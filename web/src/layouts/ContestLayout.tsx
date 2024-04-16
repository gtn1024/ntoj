import React, { useEffect, useState } from 'react'
import { Outlet, useLocation, useNavigate, useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message, notification } from 'antd'
import { useLocalStorage } from 'react-use'
import c from 'classnames'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { AccountComponent } from '../components/AccountComponent.tsx'
import { useContestClarification } from '../hooks/useContestClarification.ts'

interface MenuProps {
  items: {
    key: string
    label: string
    visible: boolean
  }[]
  current: string
  setCurrent: (key: string) => void
  onClick?: (item: { key: string, label: string }) => void
}

const MenuComponent: React.FC<MenuProps> = ({ items, current, setCurrent, onClick }) => {
  return (
    <ul className="m-0 h-full flex list-none items-center gap-1 p-0">
      {
        items.map(item => (
          item.visible && (
            <li
              key={item.key}
              className={c(current === item.key ? 'text-blue-500' : 'text-gray-500', 'px-2 cursor-pointer hover:text-blue-400')}
              onClick={() => {
                setCurrent(item.key)
                onClick?.(item)
              }}
            >
              {item.label}
            </li>
          )
        ))
      }
    </ul>
  )
}

export const ContestLayout: React.FC = () => {
  const { id } = useParams()
  const nav = useNavigate()
  const [current, setCurrent] = useState('')
  const { pathname } = useLocation()
  useEffect(() => {
    if (pathname.includes('/p')) {
      return setCurrent('/p')
    }
    if (pathname.includes('/submission')) {
      return setCurrent('/submission')
    }
    if (pathname.includes('/clarification')) {
      return setCurrent('/clarification')
    }
    if (pathname.includes('/standing')) {
      return setCurrent('/standing')
    }
    setCurrent('')
  }, [pathname])
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
  const { clarifications } = useContestClarification(id)
  const [contestClarificationStorage, setContestClarificationStorage] = useLocalStorage<{ [key: string]: boolean }>('contestClarifications', {})
  const [clarificationHasShown, setClarificationHasShown] = useState<{ [key: string]: boolean }>({})
  useEffect(() => {
    if (!clarifications || !contestClarificationStorage) {
      return
    }
    const stickyClarifications = clarifications?.filter(clarification => clarification.sticky)
    if (stickyClarifications && stickyClarifications.length > 0) {
      for (const clarification of stickyClarifications) {
        if (!contestClarificationStorage[clarification.id] && !clarificationHasShown[clarification.id]) {
          setClarificationHasShown({ ...clarificationHasShown, [clarification.id]: true })
          void notification.info({
            key: clarification.id,
            message: '公告',
            description: (
              <>
                <p>{clarification.title}</p>
                <p>请进入疑问板块查看</p>
              </>
            ),
            duration: 0,
            onClose: () => setContestClarificationStorage({ ...contestClarificationStorage, [clarification.id]: true }),
          })
        }
      }
    }
  }, [clarificationHasShown, clarifications, contestClarificationStorage, setContestClarificationStorage])

  const items: MenuProps['items'] = [
    { label: '首页', key: '', visible: true },
    { label: '题目', key: '/p', visible: contest?.hasPermission ?? false },
    { label: '提交', key: '/submission', visible: contest?.hasPermission ?? false },
    { label: '疑问', key: '/clarification', visible: contest?.hasPermission ?? false },
    { label: '排名', key: '/standing', visible: contest?.hasPermission ?? false },
  ]
  return (
    <div className="h-screen flex flex-col">
      <div className="h-48px border-b border-#ddd border-solid">
        <div className="h-full flex items-center justify-between leading-[48px]">
          <div className="flex">
            <div className="mr-1 h-full bg-[#232c31] px-4 text-white">
              {contest?.title}
            </div>
            <MenuComponent
              items={items}
              current={current}
              setCurrent={setCurrent}
              onClick={(item) => {
                nav(`/c/${id}${item.key}`)
              }}
            />
            <div
              className="cursor-pointertext-gray-600 px-2"
              onClick={() => nav('/c')}
            >
              退出
            </div>
          </div>
          <div className="m-4">
            <AccountComponent className="flex justify-end" />
          </div>
        </div>
      </div>
      <div className="grow overflow-y-auto">
        <Outlet />
      </div>
    </div>
  )
}
