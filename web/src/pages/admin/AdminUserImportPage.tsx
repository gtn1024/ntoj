import React, { useState } from 'react'
import { Button, Input, Space, message } from 'antd'
import type { AxiosError } from 'axios'
import { useNavigate } from 'react-router-dom'
import { type HttpResponse, http } from '../../lib/Http.tsx'

interface UserPreview {
  username: string
  password: string
  realName: string
  email: string
  role: UserRole
  exists: boolean
}

export const AdminUserImportPage: React.FC = () => {
  const nav = useNavigate()
  const [inputUser, setInputUser] = useState('')
  const [previewData, setPreviewData] = useState<Array<UserPreview>>([])
  function onClickPreview() {
    if (!inputUser) {
      void message.error('请输入用户信息')
      return
    }
    http.post<Array<UserPreview>>('/admin/user/user_import_preview', { users: inputUser })
      .then((res) => {
        setPreviewData(res.data.data)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message || err.message)
      })
  }
  function onClickImport() {
    if (!inputUser) {
      void message.error('请输入用户信息')
      return
    }
    http.post<Array<UserPreview>>('/admin/user/user_import', { users: inputUser })
      .then(() => {
        void message.success('导入成功')
        nav('/admin/user')
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message || err.message)
      })
  }
  return (
    <div flex flex-col p-4>
      <Space direction='vertical'>
        <h2 mb-2>用户导入</h2>
        <div>
          <p>用户</p>
          <Input.TextArea
            rows={10}
            placeholder={'输入用户信息，每行一个，使用 Tab 分隔，格式：用户名\t密码\t真实姓名\t电子邮箱\t用户权限'}
            value={inputUser}
            onChange={e => setInputUser(e.target.value)}
          />
        </div>
        <div flex gap-2>
          <Button type="primary" onClick={onClickPreview}>预览</Button>
          <Button onClick={onClickImport}>导入</Button>
        </div>
        {previewData.length > 0 && (
          <div>
            <h3>共识别到 {previewData.length} 个用户</h3>
            <table text-4>
              <thead>
              <tr>
                <th>序号</th>
                <th>用户名</th>
                <th>密码</th>
                <th>真实姓名</th>
                <th>电子邮箱</th>
                <th>用户权限</th>
                <th>用户已存在</th>
              </tr>
              </thead>
              <tbody text-center>
                {previewData.map((user, index) => (
                  <tr key={index} bg={user.exists ? 'red' : 'green'}>
                    <td>{index + 1}</td>
                    <td>{user.username}</td>
                    <td>{user.password}</td>
                    <td>{user.realName}</td>
                    <td>{user.email}</td>
                    <td>{user.role}</td>
                    <td>{user.exists ? '是' : '否'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Space>
    </div>
  )
}
export default AdminUserImportPage
