import React from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { timestampToDateString } from '../lib/misc.ts'

export const UserProfilePage: React.FC = () => {
  const { username } = useParams()
  const { data } = useSWR(`/user/${username ?? ''}`, async (path) => {
    return http.get<User>(path)
      .then(res => res.data.data)
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取用户信息失败')
        throw err
      })
  })
  return (
    <div className="h-[calc(100vh-64px-80px)] flex items-center justify-center">
      <div className="h-[500px] w-[700px] border-1 border-[#e0e0e0] rounded-2xl border-solid bg-white shadow">
        <div className="m-[20px] flex items-center justify-between">
          <div>
            <div className="text-xl font-bold">{data?.displayName}</div>
            <div>
              @
              {data?.username}
            </div>
            <div>{timestampToDateString((data?.createdAt || 0) * 1000)}</div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default UserProfilePage
