import React from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import s from './UserProfilePage.module.scss'

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
    <div className={s.wrapper}>
      <div className={s.card}>
        <div className={s.main}>
          <div className={s.left}>
            <div className={s.nickname}>{data?.realName}</div>
            <div className={s.username}>@{data?.username}</div>
            <div className={s.registerAt}>{data?.registerAt}</div>
          </div>
        </div>
      </div>
    </div>
  )
}
