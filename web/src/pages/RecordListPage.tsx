import React, { useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import type { TablePaginationConfig } from 'antd'
import { Table, message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import type { ColumnsType } from 'antd/es/table'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'
import { useLanguages } from '../hooks/useLanguages.ts'

interface SubmissionDto {
  id: number
  status: SubmissionStatus
  time?: number
  memory?: number
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
  const { languages } = useLanguages()
  const {
    data,
    error,
  } = useSWR(`/record?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
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
  const columns: ColumnsType<SubmissionDto> = [
    {
      title: '运行ID',
      dataIndex: 'id',
      key: 'id',
      render: (value: string) => {
        return <Link to={`/r/${value}`}>{value}</Link>
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (value: SubmissionStatus) => {
        return <span style={{ color: statusToColor(value) }}>{statusToMessage(value)}</span>
      },
    },
    {
      title: '题目',
      render: (_value: string, record: SubmissionDto) => {
        return (
          <Link to={`/p/${record.problem.alias ?? ''}`}>
            {record.problem.alias}
            {' '}
            {record.problem.title}
          </Link>
        )
      },
    },
    {
      title: '提交者',
      render: (_value: string, record: SubmissionDto) => {
        return record.user.username
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
      dataIndex: 'lang',
      key: 'lang',
      render(value: string) {
        return languages?.[value]?.display || value
      },
    },
    {
      title: '提交时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render(value: number) {
        // YYYY-MM-DD HH:mm:ss
        return new Date(value * 1000).toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
        })
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

export default RecordListPage
