import type { FC } from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { http } from '../lib/Http.tsx'
import { ErrorNotFound } from '../errors.ts'

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
        <div className="m-2 w-3/4 b b-gray-200 rounded-md b-solid bg-white p-2 <md:w-full">

        </div>
      </div>
    </div>
  )
}
