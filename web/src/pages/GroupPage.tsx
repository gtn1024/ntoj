import type { FC } from 'react'
import { Link, useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { http } from '../lib/Http.tsx'
import { ErrorNotFound } from '../errors.ts'
import { timestampToDateString } from '../lib/misc.ts'

export const GroupPage: FC = () => {
  const { id } = useParams()
  const { data } = useSWR(`/group/${id}`, async (path) => {
    return http.get<Group>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError) => {
        if (err.response?.status === 404) {
          throw new ErrorNotFound()
        }
        throw err
      })
  })
  return (
    <div className="mx-auto max-w-1200px">
      <div className="flex items-start justify-between <md:flex-col">
        <div className="m-2 w-1/4 b b-gray-200 rounded-md b-solid bg-white p-4 <md:w-full">
          <div className="mb-2 text-center text-2xl font-bold">{data?.name}</div>
          <div className="text-4 text-gray-500">
            <div className="i-mdi:person mr-1" />
            {data?.users.length}
          </div>
        </div>
        <div className="m-2 w-3/4 <md:w-full">
          <div className="mb-4 b b-gray-200 rounded-md b-solid bg-white p-4">
            <h4 className="text-2xl font-bold">作业</h4>
            <div>
              <table className="w-full border-collapse b b-gray-200 b-solid" border={1}>
                <thead>
                  <tr>
                    <th className="p-2">标题</th>
                    <th className="p-2">开始时间</th>
                    <th className="p-2">结束时间</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.homeworks.map(homework => (
                    <tr key={homework.id} className="text-center">
                      <td className="p-2"><Link to={`/homework/${homework.id}`}>{homework.title}</Link></td>
                      <td className="p-2">{timestampToDateString(homework.startTime * 1000)}</td>
                      <td className="p-2">{timestampToDateString(homework.endTime * 1000)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
          <div className="mb-4 b b-gray-200 rounded-md b-solid bg-white p-4">
            <h4 className="text-2xl font-bold">成员</h4>
            <div>
              <table className="w-full border-collapse b b-gray-200 b-solid" border={1}>
                <thead>
                  <tr>
                    <th className="p-2">用户名</th>
                    <th className="p-2">姓名</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.users.map(user => (
                    <tr key={user.id} className="text-center">
                      <td className="p-2"><Link to={`/u/${user.username}`}>{user.username}</Link></td>
                      <td className="p-2">{user.realName}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
