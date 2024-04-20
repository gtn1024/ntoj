import React, { useEffect } from 'react'
import c from 'classnames'
import { Link, useParams } from 'react-router-dom'
import dayjs from 'dayjs'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import { mdit } from '../lib/mdit.ts'
import { ErrorNotFound } from '../errors.ts'
import { type HttpResponse, http } from '../lib/Http.tsx'
import { useUserStore } from '../stores/useUserStore.tsx'
import s from './ArticleViewPage.module.scss'

export const ArticleViewPage: React.FC = () => {
  const { id } = useParams()
  const userStore = useUserStore()
  const { data, error } = useSWR(`/article/${id}`, async (path) => {
    return http.get<Article>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        if (err.response?.status === 404) {
          throw new ErrorNotFound()
        }
        void message.error(err.response?.data.message ?? '获取文章失败')
        throw err
      })
  })
  useEffect(() => {
    if (error) {
      throw error
    }
  }, [error])
  return (
    <div className="mx-auto max-w-1200px">
      <div className="my-4 border rounded-md bg-white p-4">
        <h1 className="text-lg font-bold">
          {data?.title}
        </h1>
        <div className="flex justify-between">
          <div>
            <div className="i-mdi:account" />
            {' '}
            <Link to={`/u/${data?.author.username}`}>
              {data?.author.realName}
            </Link>
          </div>
          <div>
            <div className="i-mdi:calendar" />
            {' '}
            {dayjs((data?.createdAt || 0) * 1000).format('YYYY-MM-DD HH:mm:ss')}
          </div>
        </div>
        {userStore.user.username === data?.author.username && (
          <div>
            <Link to={`/article/${data?.id}/edit`}>
              <div className="i-mdi:pencil" />
              {' '}
              编辑
            </Link>
          </div>
        )}
        <div className={c(s.content, 'pt-4')}>
          <article dangerouslySetInnerHTML={{ __html: mdit.render(data?.content || '') }} />
        </div>
      </div>
    </div>
  )
}
