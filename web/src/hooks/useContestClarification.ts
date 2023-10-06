import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'

interface ContestClarificationDto {
  id: number
  title: string
  user: string
  createdAt: string
  sticky: boolean
  replyCount: number
  contestProblemAlias?: string
}
export function useContestClarification(contestId?: string) {
  const { data: clarifications, mutate } = useSWR(`/contest/${contestId}/clarifications`, async (path) => {
    return http.get<ContestClarificationDto[]>(path)
      .then((res) => {
        const sticky = res.data.data.filter(clarification => clarification.sticky)
        const notSticky = res.data.data.filter(clarification => !clarification.sticky)
        return sticky.concat(notSticky)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取疑问列表失败')
        throw err
      })
  })
  return { clarifications, mutate }
}
