import React from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import { CalendarOutlined, UserOutlined } from '@ant-design/icons'
import { message } from 'antd'
import type { AxiosError } from 'axios'
import c from 'classnames'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import s from './AnnouncementPage.module.scss'

export const AnnouncementPage: React.FC = () => {
  const { id } = useParams()
  const { data } = useSWR(`/announcement/${id ?? 0}`, async (path) => {
    return http.get<Announcement>(path)
      .then(res => res.data.data)
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取公告失败')
        throw err
      })
  })
  return (
    <div className="m-5">
      <div>
        <h1 className="text-lg font-bold">
          {data?.title}
        </h1>
        <div className="flex justify-between">
          <div>
            <UserOutlined /> {data?.author}
          </div>
          <div>
            <CalendarOutlined/> {data?.createdAt}
          </div>
        </div>
        <div className={c(s.content, 'pt-4')}>
          <article dangerouslySetInnerHTML={{ __html: data?.content ?? '' }} />
        </div>
      </div>
    </div>
  )
}
