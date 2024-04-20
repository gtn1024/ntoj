import React, { useEffect, useState } from 'react'
import c from 'classnames'
import { Link, useNavigate, useParams } from 'react-router-dom'
import dayjs from 'dayjs'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Modal, message } from 'antd'
import { mdit } from '../lib/mdit.ts'
import { ErrorNotFound } from '../errors.ts'
import { type HttpResponse, http } from '../lib/Http.tsx'
import { useUserStore } from '../stores/useUserStore.tsx'
import s from './ArticleViewPage.module.scss'

export const ArticleViewPage: React.FC = () => {
  const nav = useNavigate()
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
  const [open, setOpen] = useState(false)
  const [confirmLoading, setConfirmLoading] = useState(false)
  function handleOk() {
    setConfirmLoading(true)
    http.delete(`/article/${id}`)
      .then(() => {
        setOpen(false)
        setConfirmLoading(false)
        void message.success('删除成功')
        nav('/article')
      })
  }

  function handleCancel() {
    setOpen(false)
  }
  function onDeleteClick() {
    setOpen(true)
  }
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
            <Link to="#" onClick={onDeleteClick} className="ml-4">
              <div className="i-mdi:delete" />
              {' '}
              删除
            </Link>
          </div>
        )}
        <div className={c(s.content, 'pt-4')}>
          <article dangerouslySetInnerHTML={{ __html: mdit.render(data?.content || '') }} />
        </div>
      </div>
      <Modal
        title="Title"
        open={open}
        onOk={handleOk}
        confirmLoading={confirmLoading}
        onCancel={handleCancel}
      >
        <p>确认删除吗？删除后无法恢复！</p>
      </Modal>
    </div>
  )
}
