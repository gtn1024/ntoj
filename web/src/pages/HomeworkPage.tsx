import type { FC } from 'react'
import { Link, useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { http } from '../lib/Http.tsx'
import { ErrorForbidden, ErrorNotFound } from '../errors.ts'
import { timestampToDateString } from '../lib/misc.ts'

interface HomeworkStatus {
  [key: string]: {
    solved: boolean
    submissionId?: number
  }
}

export const HomeworkPage: FC = () => {
  const { id } = useParams()
  const { data: homework } = useSWR(`/homework/${id}`, async (path) => {
    return http.get<Homework>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError) => {
        if (err.response?.status === 404) {
          throw new ErrorNotFound()
        }
        if (err.response?.status === 403) {
          throw new ErrorForbidden()
        }
        throw err
      })
  })
  const { data: homeworkStatus } = useSWR(`/homework/${id}/status`, async (path) => {
    return http.get<HomeworkStatus>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError) => {
        if (err.response?.status === 404) {
          throw new ErrorNotFound()
        }
        if (err.response?.status === 403) {
          throw new ErrorForbidden()
        }
        throw err
      })
  })

  return (
    <div className="mx-auto max-w-1200px">
      <div className="flex items-start justify-between <md:flex-col">
        <div className="m-2 w-1/4 b b-gray-200 rounded-md b-solid bg-white p-4 <md:w-full">
          <div className="mb-2 text-center text-2xl font-bold">{homework?.title}</div>
          <div className="text-4 text-gray-500">
            <div className="i-mdi:timer-start-outline mr-1" />
            {timestampToDateString((homework?.startTime || 0) * 1000)}
          </div>
          <div className="text-4 text-gray-500">
            <div className="i-mdi:calendar-time mr-1" />
            {timestampToDateString((homework?.endTime || 0) * 1000)}
          </div>
        </div>
        <div className="m-2 w-3/4 <md:w-full">
          <div className="mb-4 b b-gray-200 rounded-md b-solid bg-white p-4">
            <h4 className="text-2xl font-bold">题目</h4>
            <div>
              <table className="w-full border-collapse b b-gray-200 b-solid" border={1}>
                <thead>
                  <tr>
                    <th className="w-5% p-2"></th>
                    <th className="w-20% p-2">题号</th>
                    <th className="p-2">题目</th>
                  </tr>
                </thead>
                <tbody>
                  {homework?.problems.map(problem => (
                    <tr key={problem.id} className="text-center">
                      <td className="p-2">
                        {homeworkStatus && homeworkStatus[problem.id]?.solved && (
                          <Link to={`/r/${homeworkStatus[problem.id].submissionId}`}>
                            <div className="i-mdi:check text-green-500" />
                          </Link>
                        )}
                      </td>
                      <td className="p-2"><Link to={`/p/${problem.alias}`}>{problem.alias}</Link></td>
                      <td className="p-2"><Link to={`/p/${problem.alias}`}>{problem.title}</Link></td>
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
