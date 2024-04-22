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

export const AdminArticlePage: React.FC = () => {
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
  } = useSWR(`/admin/article?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
    return http.get<L<AdminDto.Article>>(path)
      .then((res) => {
        setPagination({
          ...pagination,
          total: res.data.data.total,
        })
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取文章列表失败')
        throw err
      })
  })
  const loading = !data && !error
  const deleteArticle = (id: number) => {
    http.delete(`/admin/article/${id}`)
      .then(() => {
        void message.success('删除成功')
        void mutate()
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '删除失败')
        throw err
      })
  }

  const columns: ColumnsType<AdminDto.Article> = [
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
        return <Link to={`/article/${record.id ?? 0}`}>{value}</Link>
      },
    },
    {
      title: '创建人',
      dataIndex: 'author',
      key: 'author',
      render: (_value, record) => {
        return record.author?.username
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
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
                nav(`/admin/article/${value ?? 0}/edit`)
              }}
            >
              编辑
            </Button>
            <Popconfirm
              title="删除"
              description="确认删除这一项吗？"
              onConfirm={() => deleteArticle(value ?? 0)}
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
      <h2 className="mb-2">文章管理</h2>
      <Space direction="vertical">
        <div className="flex justify-between">
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
