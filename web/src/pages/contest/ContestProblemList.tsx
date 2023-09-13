import React from 'react'
import c from 'classnames'
import { message } from 'antd'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Link, useParams } from 'react-router-dom'
import { useLayout } from '../../hooks/useLayout.ts'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

interface ContestProblem {
  alias: string
  title: string
}

export const ContestProblemList: React.FC = () => {
  const { isMobile } = useLayout()
  const { id } = useParams()
  const { data: problems } = useSWR(`/contest/${id}/problems`, async (path) => {
    return http.get<ContestProblem[]>(path)
      .then((res) => {
        return res.data.data.sort((a, b) => a.alias.localeCompare(b.alias))
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛题目失败')
        throw err
      })
  })
  return (
    <div p-2 flex items-start max-w="1200px" m-auto className={c(isMobile && 'flex-col')}>
      <table w-full>
        <thead>
          <tr bg="#eeeeee" text="#888888">
            <th px-4 py-3 font-normal>题号</th>
            <th px-4 py-3 font-normal>题目</th>
          </tr>
        </thead>
        <tbody>
          {problems?.map((problem, index) => (
            <tr key={index} bg="hover:#f0f0f0" cursor-pointer>
              <td width={'10%'} px-4 py-3>
                <div text-center>{problem.alias}</div>
              </td>
              <td width={'90%'} px-4 py-3>
                <Link to={`/c/${id}/p/${problem.alias}`}>{problem.title}</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default ContestProblemList
