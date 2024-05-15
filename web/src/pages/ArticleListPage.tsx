import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Button, Pagination, message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { timestampToDateString, useQueryParam } from '../lib/misc.ts'
import { useUserPermission } from '../hooks/useUserPermission.ts'
import { PERM, checkPermission } from '../lib/Permission.ts'

const ArticleItem: React.FC<{ article: Article, className?: string }> = ({ article, className }) => {
  return (
    <div className={className}>
      <div className="p-4 pb-0 text-4 hover:bg-#fafafa">
        <div className="flex justify-between">
          <Link to={`/article/${article.id}`}>
            {article.title}
          </Link>
          <div>
            {timestampToDateString(article.createdAt * 1000)}
          </div>
        </div>
      </div>
    </div>
  )
}

export const ArticleListPage: React.FC = () => {
  const permission = useUserPermission()
  const nav = useNavigate()
  const problemAlias = useQueryParam('problemAlias')
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [total, setTotal] = useState(0)
  const [problemData, setProblemData] = useState<Problem | null>(null)
  const { data: articles } = useSWR(`/article?current=${currentPage}&pageSize=${pageSize}&problemAlias=${problemAlias}`, async (path) => {
    return http.get<L<Article>>(path)
      .then((res) => {
        setTotal(res.data.data.total)
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取文章列表失败')
        throw err
      })
  })
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
  return (
    <div className="mx-auto max-w-1200px">
      <div className="py-4">
        <div className="flex items-center justify-between">
          <div className="text-4">
            {problemAlias !== '' && (
              <div>
                正在查看：
                <Link to={`/p/${problemData?.alias || ''}`}>{problemData?.title}</Link>
              </div>
            )}
          </div>
          <div>
            {checkPermission(permission, PERM.PERM_CREATE_ARTICLE) && (
              <Button type="primary" href={problemAlias === '' ? '/article/new' : `/article/new?problemAlias=${problemAlias}`}>写文章</Button>
            )}
          </div>
        </div>
        <div className="my-4 bg-white py-2">
          {articles?.map((article) => {
            return <ArticleItem key={article.id} article={article} />
          })}
        </div>
        <div className="">
          <Pagination
            current={currentPage}
            pageSize={pageSize}
            onChange={(page, size) => {
              setCurrentPage(page)
              setPageSize(size)
            }}
            total={total}
          />
        </div>
      </div>
    </div>
  )
}
