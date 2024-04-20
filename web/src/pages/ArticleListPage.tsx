import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import dayjs from 'dayjs'
import { Button, Pagination, message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'

const ArticleItem: React.FC<{ article: Article, className?: string }> = ({ article, className }) => {
  return (
    <div className={className}>
      <div className="p-4 pb-0 text-4 hover:bg-#fafafa">
        <div className="flex justify-between">
          <Link to={`/article/${article.id}`}>
            { article.title }
          </Link>
          <div>
            {dayjs(article.createdAt * 1000).format('YYYY-MM-DD HH:mm:ss')}
          </div>
        </div>
      </div>
    </div>
  )
}

export const ArticleListPage: React.FC = () => {
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [total, setTotal] = useState(0)
  const { data: articles } = useSWR(`/article?current=${currentPage}&pageSize=${pageSize}`, async (path) => {
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
  return (
    <div className="mx-auto max-w-1200px">
      <div className="py-4">
        <div className="flex justify-end">
          <Button type="primary" href="/article/new">写文章</Button>
        </div>
        <div className="my-4 bg-white py-2">
          { articles?.map((article, index) => {
            return <ArticleItem key={index} article={article} />
          }) }
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
