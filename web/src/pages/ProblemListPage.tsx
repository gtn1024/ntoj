import React, { useState } from 'react'
import type { TablePaginationConfig } from 'antd'
import { Table, message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Link, useSearchParams } from 'react-router-dom'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'

interface Problem {
  id?: number
  title?: string
  alias?: string
}

export const ProblemListPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const [pagination, setPagination] = useState<TablePaginationConfig>({
    current: parseInt(searchParams.get('current') ?? '1'),
    pageSize: parseInt(searchParams.get('size') ?? '20'),
    showTotal: total => `共 ${total} 条`,
    showSizeChanger: true,
    onChange: (current, pageSize) => {
      setSearchParams({
        current: current.toString(),
        size: pageSize.toString(),
      })
      setPagination({
        ...pagination,
        current,
        pageSize,
      })
    },
  })
  const { data, error } = useSWR(`/problem?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
    return http.get<L<Problem>>(path)
      .then((res) => {
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取公告列表失败')
        throw err
      })
  })
  const loading = !data && !error
  const columns = [
    {
      title: '题号',
      dataIndex: 'alias',
      key: 'alias',
      render: (value: string) => {
        return <Link to={`/problem/${value}`}>{value}</Link>
      },
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      render: (value: string, record: Problem) => {
        return <Link to={`/problem/${record.alias ?? ''}`}>{value}</Link>
      },
    },
  ]

  return (
    <div className="m-[15px]">
      <Table
        loading={loading}
        dataSource={data}
        rowKey="id"
        columns={columns}
        pagination={pagination}
      />
    </div>
  )
}
