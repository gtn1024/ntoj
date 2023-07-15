import React from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'

export const ProblemPage: React.FC = () => {
  const nav = useNavigate()
  const { alias } = useParams()
  const { data } = useSWR(`/problem/${alias ?? ''}`, async (path) => {
    return http.get<Problem>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError) => {
        if (err.response?.status === 404) {
          void message.error('题目不存在！')
          nav('/404')
        }
        throw err
      })
  })

  return (
    <ProblemDetail data={data} />
  )
}
