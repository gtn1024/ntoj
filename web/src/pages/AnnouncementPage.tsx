import React from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import useSWR from 'swr'
import { message } from 'antd'
import type { AxiosError } from 'axios'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import s from './AnnouncementPage.module.scss'

export const AnnouncementPage: React.FC = () => {
  const nav = useNavigate()
  const { id } = useParams()
  const { data } = useSWR(`/announcement/${id ?? 0}`, async (path) => {
    return http.get<Announcement>(path)
      .then(res => res.data.data)
      .catch((err: AxiosError<HttpResponse>) => {
        if (err.response?.status === 404) {
          void message.error('公告不存在！')
          nav('/404')
        } else {
          void message.error(err.response?.data.message ?? '获取公告失败')
        }
        throw err
      })
  })
  return (
    <div m-5>
      <div>
        <h1 text-lg font-bold>
          {data?.title}
        </h1>
        <div flex justify-between>
          <div>
            <div className="i-mdi:account"/> {data?.author}
          </div>
          <div>
            <div className="i-mdi:calendar"/> {data?.createdAt}
          </div>
        </div>
        <div className={s.content} pt-4>
          <article dangerouslySetInnerHTML={{ __html: data?.content ?? '' }} />
        </div>
      </div>
    </div>
  )
}

export default AnnouncementPage
