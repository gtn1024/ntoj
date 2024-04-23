import React from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Pagination, message } from 'antd'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { statusToColor, statusToMessage } from '../../lib/SubmissionUtils.ts'
import { LinkComponent } from '../../components/LinkComponent.tsx'

interface ContestSubmission {
  id: number
  user: User
  alias: string
  result: SubmissionStatus
  time?: number
  memory?: number
  language: string
  codeLength: number
  submitTime: string
}

export const ContestSubmissionListPage: React.FC = () => {
  const { id } = useParams()
  const [current, setCurrent] = React.useState(1)
  const { data } = useSWR(`/contest/${id}/submission?current=${current}&pageSize=20`, async (path: string) => {
    return http.get<L<ContestSubmission>>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛提交列表失败')
        throw err
      })
  })
  return (
    <div className="m-auto max-w-1200px w-full flex flex-col items-start gap-2 p-2">
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-#eee text-#888">
            <th className="px-4 py-3 font-normal">运行ID</th>
            <th className="px-4 py-3 font-normal">用户名</th>
            <th className="px-4 py-3 font-normal">题号</th>
            <th className="px-4 py-3 font-normal">运行结果</th>
            <th className="px-4 py-3 font-normal">运行时间</th>
            <th className="px-4 py-3 font-normal">使用内存</th>
            <th className="px-4 py-3 font-normal">代码长度</th>
            <th className="px-4 py-3 font-normal">使用语言</th>
            <th className="px-4 py-3 font-normal">提交时间</th>
          </tr>
        </thead>
        <tbody>
          {data?.list.map(submission => (
            <tr key={submission.id} className="cursor-pointer hover:bg-#f0f0f0">
              <td width="10%" className="px-4 py-3">
                <div className="text-center">{submission.id}</div>
              </td>
              <td width="10%" className="px-4 py-3">
                <div className="text-center">
                  {submission.user.realName && (
                    <>
                      {submission.user.realName}
                      {' '}
                      <br />
                    </>
                  ) }
                  {submission.user.username}
                </div>
              </td>
              <td width="10%" className="px-4 py-3">
                <div className="text-center">
                  <LinkComponent href={`/c/${id}/p/${submission.alias}`}>
                    {submission.alias}
                  </LinkComponent>
                </div>
              </td>
              <td width="10%" className="px-4 py-3">
                <div className="text-center" style={{ color: statusToColor(submission.result) }}>{statusToMessage(submission.result)}</div>
              </td>
              <td width="10%" className="px-4 py-3">
                <div className="text-center">
                  {submission.time}
                  ms
                </div>
              </td>
              <td width="10%" className="px-4 py-3">
                <div className="text-center">
                  {submission.memory}
                  KB
                </div>
              </td>
              <td width="10%" className="px-4 py-3">
                <div className="text-center">
                  {submission.codeLength}
                  B
                </div>
              </td>
              <td width="10%" className="px-4 py-3">
                <div className="text-center">{submission.language}</div>
              </td>
              <td width="20%" className="px-4 py-3">
                <div className="text-center">{submission.submitTime}</div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="w-full flex justify-center">
        <Pagination current={current} onChange={setCurrent} total={data?.total} pageSize={20} showSizeChanger={false} />
      </div>
    </div>
  )
}

export default ContestSubmissionListPage
