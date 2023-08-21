import React, { useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import type { TablePaginationConfig } from 'antd'
import { Table, message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'

interface SubmissionDto {
  id: number
  status: SubmissionStatus
  time?: number
  memory?: number
  language?: string
  user: {
    username: string
  }
  problem: {
    alias: string
    title: string
  }
  submitTime: string
}

export const RecordListPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const [pagination, setPagination] = useState<TablePaginationConfig>({
    current: Number.parseInt(searchParams.get('current') ?? '1'),
    pageSize: Number.parseInt(searchParams.get('size') ?? '50'),
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
  const {
    data,
    error,
  } = useSWR(`/submission/list?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
    return http.get<L<SubmissionDto>>(path)
      .then((res) => {
        const data = res.data.data
        setPagination({
          ...pagination,
          total: data.total,
        })
        return data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取提交列表失败')
        throw err
      })
  })
  const loading = !data && !error
  const columns = [
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: '题目',
      render: (_value: string, record: SubmissionDto) => {
        return <Link to={`/problem/${record.problem.alias ?? ''}`}>{record.problem.alias} {record.problem.title}</Link>
      },
    },
    {
      title: '提交者',
      render: (_value: string, record: SubmissionDto) => {
        return <Link to={`/user/${record.user.username ?? ''}`}>{record.user.username}</Link>
      },
    },
    {
      title: '时间',
      dataIndex: 'time',
      key: 'time',
      render: (value?: number) => {
        return value ? `${value}ms` : '0ms'
      },
    },
    {
      title: '内存',
      dataIndex: 'memory',
      key: 'memory',
      render: (value?: number) => {
        return value ? `${value}KiB` : '0KiB'
      },
    },
    {
      title: '语言',
      dataIndex: 'language',
      key: 'language',
    },
    {
      title: '提交时间',
      dataIndex: 'submitTime',
      key: 'submitTime',
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
