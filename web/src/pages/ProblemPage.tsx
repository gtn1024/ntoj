import React from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'

export const ProblemPage: React.FC = () => {
  const { alias } = useParams()
  const { data } = useSWR(`/problem/${alias ?? ''}`, async (path) => {
    return http.get<Problem>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err) => {
        throw err
      })
  })

  return (
    <ProblemDetail data={data} />
  )
}
