import React, { useEffect, useState } from 'react'
import { Button, Input, message } from 'antd'
import { Link, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { http } from '../lib/Http.tsx'
import { useQueryParam } from '../lib/misc.ts'

export const ArticleEditPage: React.FC = () => {
  const nav = useNavigate()
  const problemAlias = useQueryParam('problemAlias')
  const { id } = useParams()
  const mode = id ? '修改' : '发表'
  const [title, setTitle] = React.useState<string>('')
  const [content, setContent] = React.useState<string>('')
  const [problemData, setProblemData] = useState<Problem | null>(null)
  useEffect(() => {
    if (problemAlias !== '') {
      http.get<Problem>(`/problem/${problemAlias}`)
        .then((res) => {
          setProblemData(res.data.data)
        })
        .catch((err: AxiosError) => {
          if (err.response?.status === 404) {
            nav('/404')
            return
          }
          throw err
        })
    }
  }, [problemAlias])
  useEffect(() => {
    if (mode === '修改') {
      http.get<Article>(`/article/${id}`)
        .then((res) => {
          setTitle(res.data.data.title)
          setContent(res.data.data.content)
        })
        .catch((err) => {
          if (err.response?.status === 404) {
            void message.error('文章不存在')
            nav('/article')
          }
          void message.error(err.response?.data.message ?? '获取文章失败')
          throw err
        })
    }
  }, [id, mode, nav])
  function onSubmit() {
    if (!title || !content) {
      void message.error('标题和内容不能为空')
      return
    }
    const sendRequest = mode === '修改' ? http.patch.bind(http) : http.post.bind(http)
    const url = mode === '修改' ? `/article/${id}` : '/article'
    sendRequest<Article>(url, { title, content, problemAlias })
      .then((res) => {
        void message.success(`${mode}成功`)
        nav(`/article/${res.data.data.id}`)
      })
      .catch((err) => {
        void message.error(err.response?.data.message || `${mode}失败`)
        throw err
      })
  }
  return (
    <div className="mx-auto max-w-1200px">
      <div className="m-4 rounded-md bg-white p-2">
        {problemAlias !== '' && (
          <div className="py-2">
            <div className="text-4">
              {'正在为 '}
              <Link to={`/p/${problemData?.alias || ''}`}>{problemData?.title}</Link>
              {' 写文章'}
            </div>
          </div>
        )}
        <div className="py-2">
          <Input placeholder="标题" value={title} onChange={e => setTitle(e.target.value)} />
        </div>
        <div className="py-2">
          <Input.TextArea rows={20} placeholder="内容，支持 Markdown" value={content} onChange={e => setContent(e.target.value)} />
        </div>
        <div className="py-2">
          <Button type="primary" onClick={onSubmit}>{mode}</Button>
        </div>
      </div>
    </div>
  )
}
