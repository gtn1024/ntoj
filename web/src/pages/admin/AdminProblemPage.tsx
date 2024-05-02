import React, { useState } from 'react'
import type { TablePaginationConfig } from 'antd'
import { Button, Popconfirm, Space, Table, message } from 'antd'
import useSWR from 'swr'
import type { ColumnsType } from 'antd/es/table'
import { Link, useNavigate } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

export const AdminProblemPage: React.FC = () => {
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
  } = useSWR(`/admin/problem?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
    return http.get<L<AdminDto.Problem>>(path)
      .then((res) => {
        setPagination({
          ...pagination,
          total: res.data.data.total,
        })
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取公告列表失败')
        throw err
      })
  })
  const loading = !data && !error
  const deleteProblem = (id: number) => {
    http.delete(`/admin/problem/${id}`)
      .then(() => {
        void message.success('删除成功')
        void mutate()
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '删除失败')
        throw err
      })
  }

  const columns: ColumnsType<AdminDto.Problem> = [
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
        return <Link to={`/p/${record.alias ?? ''}`}>{value}</Link>
      },
    },
    {
      title: '显示 ID',
      dataIndex: 'alias',
      key: 'alias',
    },
    {
      title: '创建人',
      dataIndex: 'author',
      key: 'author',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '是否可见',
      dataIndex: 'visible',
      key: 'visible',
      render: (value: boolean) => {
        return (value ? '是' : '否')
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
                nav(`/admin/problem/${value ?? 0}/edit`)
              }}
            >
              编辑
            </Button>
            <Popconfirm
              title="删除"
              description="确认删除这一项吗？"
              onConfirm={() => deleteProblem(value ?? 0)}
              okText="确认"
              cancelText="取消"
            >
              <Button type="link">删除</Button>
            </Popconfirm>
          </Space>
        )
      },
    },
  ]
  return (
    <div className="flex flex-col p-4">
      <h2 className="mb-2">题目管理</h2>
      <Space direction="vertical">
        <div className="flex justify-between">
          <div>
            <button type="button" onClick={() => nav('/admin/problem/new')}>新建</button>
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

export default AdminProblemPage
