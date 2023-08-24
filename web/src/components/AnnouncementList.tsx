import React from 'react'
import { List, message } from 'antd'
import useSWR from 'swr'
import { Link } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'

export const AnnouncementList: React.FC = () => {
  const { data, error } = useSWR('/announcement', async (path) => {
    return http.get<L<Announcement>>(path)
      .then((res) => {
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取公告列表失败')
        throw err
      })
  })
  const loading = !data && !error
  return (
    <div>
      <List
        itemLayout="horizontal"
        loading={loading}
        dataSource={data}
        renderItem={item => (
          <List.Item
            extra={<div>{item.createdAt}</div>}
          >
            <List.Item.Meta
              title={<Link to={`/a/${item.id ?? 0}`} style={{ fontWeight: 'bold' }}> {item.title} </Link>}
            />
          </List.Item>
        )}
      />
    </div>
  )
}
