import React, { useState } from 'react'
import type { TablePaginationConfig } from 'antd'
import { Button, Input, Modal, Popconfirm, Space, Table, message } from 'antd'
import useSWR from 'swr'
import type { ColumnsType } from 'antd/es/table'
import type { AxiosError } from 'axios'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { DebounceSelect } from '../../components/antd/DebounceSelect.tsx'

export const AdminGroupPage: React.FC = () => {
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
  const { data, error, mutate } = useSWR(`/admin/group?current=${pagination.current ?? 1}&pageSize=${pagination.pageSize ?? 20}`, async (path) => {
    return http.get<L<AdminDto.AdminGroup>>(path)
      .then((res) => {
        setPagination({
          ...pagination,
          total: res.data.data.total,
        })
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取小组列表失败')
        throw err
      })
  })

  interface UserValue {
    label: string
    value: number
  }

  const [isGroupModalOpen, setIsGroupModalOpen] = useState(false)
  const [mode, setMode] = useState<'创建小组' | '修改小组'>('创建小组')
  const [confirmLoading, setConfirmLoading] = useState(false)
  const [updateGroupId, setUpdateGroupId] = useState<number | null>(null)
  const [groupName, setGroupName] = useState<string>('')
  const [groupUsers, setGroupUsers] = useState<UserValue[]>([])
  const handleGroupModalOk = () => {
    setConfirmLoading(true)
    const userIds = groupUsers.map(user => user.value)
    const requestMethod = mode === '创建小组' ? http.post.bind(http) : http.patch.bind(http)
    const requestUrl = mode === '创建小组' ? '/admin/group' : `/admin/group/${updateGroupId}`
    requestMethod<AdminDto.AdminGroup>(requestUrl, { name: groupName, users: userIds })
      .then(() => {
        void message.success(`${mode === '创建小组' ? '创建' : '修改'}成功`)
        void mutate()
        setIsGroupModalOpen(false)
      })
      .finally(() => {
        setConfirmLoading(false)
      })
  }
  const handleGroupModalCancel = () => {
    setIsGroupModalOpen(false)
  }

  const onCreateGroupClick = () => {
    setGroupName('')
    setGroupUsers([])
    setIsGroupModalOpen(true)
    setUpdateGroupId(null)
    setMode('创建小组')
  }

  const loading = !data && !error
  const remove = (id: number) => {
    http.delete(`/admin/group/${id}`)
      .then(() => {
        void message.success('删除成功')
        void mutate()
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '删除失败')
        throw err
      })
  }
  const fetchGroup = (id: number) => {
    http.get<AdminDto.AdminGroup>(`/admin/group/${id}`)
      .then((res) => {
        setGroupName(res.data.data.name)
        setGroupUsers(res.data.data.users.map((user) => {
          let label = user.username
          if (user.realName) {
            label += ` (${user.realName})`
          }
          return ({
            label,
            value: user.id,
          })
        }))
      })
  }
  const columns: ColumnsType<AdminDto.AdminGroup> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '用户数',
      dataIndex: 'users',
      key: 'users',
      render: (users: User[]) => {
        return users.length
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
                setMode('修改小组')
                setUpdateGroupId(value)
                setIsGroupModalOpen(true)
                fetchGroup(value)
              }}
            >
              编辑
            </Button>
            <Popconfirm
              title="删除"
              description="确认删除这一项吗？"
              onConfirm={() => remove(value ?? 0)}
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

  const fetchUserList = async (keyword: string): Promise<UserValue[]> => {
    return http.get<AdminDto.AdminUser[]>(`/admin/user/search?keyword=${keyword}`)
      .then((res) => {
        const users = res.data.data
        return users.map((user) => {
          let label = user.username
          if (user.realName) {
            label += ` (${user.realName})`
          }
          return ({
            label,
            value: user.id,
          })
        })
      })
  }

  return (
    <div className="flex flex-col p-4">
      <h2 className="mb-2">小组管理</h2>
      <Space direction="vertical">
        <div className="flex justify-between">
          <div>
            <button onClick={onCreateGroupClick}>新建</button>
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
      <Modal
        title={mode === '创建小组' ? mode : `${mode} (${updateGroupId})`}
        open={isGroupModalOpen}
        onOk={handleGroupModalOk}
        onCancel={handleGroupModalCancel}
        confirmLoading={confirmLoading}
      >
        <label>
          名称
          <Input type="text" value={groupName} onChange={e => setGroupName(e.target.value)} />
        </label>
        <label>
          用户
          <DebounceSelect
            mode="multiple"
            value={groupUsers}
            fetchOptions={fetchUserList}
            onChange={(newValue) => {
              setGroupUsers(newValue as UserValue[])
            }}
            style={{ width: '100%' }}
          />
        </label>
      </Modal>
    </div>
  )
}
