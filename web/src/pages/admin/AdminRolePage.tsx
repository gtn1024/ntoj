import React, { useMemo, useState } from 'react'
import { Button, Input, Modal, Popconfirm, Space, Table, message } from 'antd'
import type { AxiosError } from 'axios'
import { useRoles } from '../../hooks/useRoles.ts'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

export const AdminUserPage: React.FC = () => {
  const [roleName, setRoleName] = useState<string>('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [confirmLoading, setConfirmLoading] = useState(false)
  const { roles, isLoading, mutate } = useRoles()
  const handleModalOk = () => {
    setConfirmLoading(true)

    http.post(`/permission?name=${roleName}`)
      .then(() => {
        void mutate()
        void message.success('创建成功')
      })
      .catch((e: AxiosError<HttpResponse>) => {
        void message.error(e.response?.data?.message || e.message)
      })
      .finally(() => {
        setIsModalOpen(false)
        setConfirmLoading(false)
      })
  }
  const handleModalCancel = () => {
    setRoleName('')
    setIsModalOpen(false)
  }

  const removeRole = (name: string) => {
    http.delete(`/permission`, { name })
      .then(() => {
        void mutate()
        void message.success('删除成功')
      })
      .catch((e: AxiosError<HttpResponse>) => {
        void message.error(e.response?.data?.message || e.message)
      })
  }

  const data = useMemo(() => {
    if (!roles)
      return []
    return Object.entries(roles).map(([key, value]) => {
      return {
        name: key,
        role: value,
      }
    })
  }, [roles])

  const columns = [
    {
      title: '角色',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '操作',
      dataIndex: 'name',
      render: (name: string) => {
        return (
          <Space>
            <Popconfirm
              title="删除"
              description="确认删除这一项吗？删除后该角色用户将重置为 default"
              onConfirm={() => removeRole(name)}
              okText="确认"
              cancelText="取消"
            >
              <Button
                type="link"
                disabled={['default', 'root', 'guest'].includes(name)}
              >
                删除
              </Button>
            </Popconfirm>
          </Space>
        )
      },
    },
  ]
  return (
    <div className="flex flex-col p-4">
      <h2 className="mb-2">角色管理</h2>
      <Space direction="vertical">
        <div className="flex justify-between">
          <div>
            <button type="button" onClick={() => setIsModalOpen(true)}>新建</button>
          </div>
        </div>
        <div>
          <Table
            size="small"
            dataSource={data}
            columns={columns}
            rowKey="id"
            loading={isLoading}
            pagination={false}
          />
        </div>
      </Space>
      <Modal
        title="创建角色"
        open={isModalOpen}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        confirmLoading={confirmLoading}
      >
        <label>
          名称
          <Input type="text" value={roleName} onChange={e => setRoleName(e.target.value)} />
        </label>
      </Modal>
    </div>
  )
}

export default AdminUserPage
