import React, { useState } from 'react'
import type { TablePaginationConfig } from 'antd'
import { Table, message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Link, useSearchParams } from 'react-router-dom'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'

interface Problem {
  id: number
  title: string
  alias: string
  submitTimes: number
  acceptedTimes: number
}

export const ProblemListPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const [pagination, setPagination] = useState<TablePaginationConfig>({
    current: Number.parseInt(searchParams.get('current') ?? '1'),
    pageSize: Number.parseInt(searchParams.get('size') ?? '20'),
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
        const data = res.data.data
        setPagination({
          ...pagination,
          total: data.total,
        })
        return data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取题目列表失败')
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
        return <Link to={`/p/${value}`}>{value}</Link>
      },
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      render: (value: string, record: Problem) => {
        return <Link to={`/p/${record.alias ?? ''}`}>{value}</Link>
      },
    },
    {
      title: '通过',
      dataIndex: 'acceptedTimes',
      key: 'acceptedTimes',
    },
    {
      title: '提交',
      dataIndex: 'submitTimes',
      key: 'submitTimes',
    },
    {
      title: '通过率',
      render: (_: string, record: Problem) => {
        if (record.submitTimes === 0) {
          return '0.00%'
        }
        return `${(record.acceptedTimes / record.submitTimes * 100).toFixed(2)}%`
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

export default ProblemListPage
