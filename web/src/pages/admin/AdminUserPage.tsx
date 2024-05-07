import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import type { TablePaginationConfig } from 'antd'
import { Button, Space, Table, message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import type { ColumnsType } from 'antd/es/table'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { userRoleToCNString } from '../../lib/misc.ts'

export const AdminUserPage: React.FC = () => {
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
  } = useSWR(`/admin/user?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
    return http.get<L<AdminDto.AdminUser>>(path)
      .then((res) => {
        setPagination({
          ...pagination,
          total: res.data.data.total,
        })
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取用户列表失败')
        throw err
      })
  })
  const loading = !data && !error

  const columns: ColumnsType<AdminDto.AdminUser> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      render: (value) => {
        return <Link to={`/u/${value}`}>{value}</Link>
      },
    },
    {
      title: '显示名',
      dataIndex: 'displayName',
      key: 'displayName',
    },
    {
      title: '注册时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '用户权限',
      dataIndex: 'role',
      key: 'role',
      render: (value: UserRole) => {
        return userRoleToCNString(value)
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
                nav(`/admin/user/${value ?? 0}/edit`)
              }}
            >
              编辑
            </Button>
          </Space>
        )
      },
    },
  ]
  return (
    <div className="flex flex-col p-4">
      <h2 className="mb-2">用户管理</h2>
      <Space direction="vertical">
        <div className="flex justify-between">
          <div>
            <button type="button" onClick={() => nav('/admin/user/new')}>新建</button>
          </div>
          <div>
            <button type="button" onClick={() => nav('/admin/user/import')}>导入用户</button>
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

export default AdminUserPage
