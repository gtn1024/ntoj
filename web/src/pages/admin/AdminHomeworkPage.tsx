import React, { useState } from 'react'
import type { TablePaginationConfig } from 'antd'
import { Button, Popconfirm, Space, Table, message } from 'antd'
import useSWR from 'swr'
import type { ColumnsType } from 'antd/es/table'
import { Link, useNavigate } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { timestampToDateString } from '../../lib/misc.ts'

export const AdminHomeworkPage: React.FC = () => {
  const nav = useNavigate()
  const [pagination, setPagination] = useState<TablePaginationConfig>({
    current: 1,
    pageSize: 20,
    showTotal: total => `共 ${total} 条`,
    showSizeChanger: true,
    onChange: (current, pageSize) => {
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
    mutate,
  } = useSWR(`/admin/homework?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
    return http.get<L<AdminDto.Homework>>(path)
      .then((res) => {
        setPagination({
          ...pagination,
          total: res.data.data.total,
        })
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取作业列表失败')
        throw err
      })
  })
  const loading = !data && !error
  const deleteHomework = (id: number) => {
    http.delete(`/admin/homework/${id}`)
      .then(() => {
        void message.success('删除成功')
        void mutate()
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '删除失败')
        throw err
      })
  }

  const exportHomeworkData = (id: number) => {
    void http.get(`/admin/homework/${id}/export`, {}, { responseType: 'blob' })
  }

  const onCreateHomeworkClick = () => {
    nav('/admin/homework/create')
  }

  const columns: ColumnsType<AdminDto.Homework> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      render: (value, record) => {
        return <Link to={`/homework/${record.id ?? 0}`}>{value}</Link>
      },
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      key: 'startTime',
      render: (value: number) => {
        return timestampToDateString(value * 1000)
      },
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      key: 'endTime',
      render: (value: number) => {
        return timestampToDateString(value * 1000)
      },
    },
    {
      title: '操作',
      dataIndex: 'id',
      render: (value: number) => {
        return (
          <Space>
            <Button
              type="link"
              size="small"
              onClick={() => {
                nav(`/admin/homework/${value ?? 0}/edit`)
              }}
            >
              编辑
            </Button>
            <Popconfirm
              title="删除"
              description="确认删除这一项吗？"
              onConfirm={() => deleteHomework(value ?? 0)}
              okText="确认"
              cancelText="取消"
            >
              <Button type="link">删除</Button>
            </Popconfirm>
            <Button
              type="link"
              size="small"
              onClick={() => exportHomeworkData(value)}
            >
              导出
            </Button>
          </Space>
        )
      },
    },
  ]
  return (
    <div className="flex flex-col p-4">
      <h2 className="mb-2">作业管理</h2>
      <Space direction="vertical">
        <div className="flex justify-between">
          <div>
            <button onClick={onCreateHomeworkClick}>新建</button>
          </div>
        </div>
        <div>
          <Table
            dataSource={data}
            columns={columns}
            rowKey="id"
            loading={loading}
            pagination={pagination}
          />
        </div>
      </Space>
    </div>
  )
}
