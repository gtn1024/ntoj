import React from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { statusToColor, statusToMessage } from '../../lib/SubmissionUtils.ts'
import { LinkComponent } from '../../components/LinkComponent.tsx'

interface ContestSubmission {
  id: number
  user: string
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
  const { data } = useSWR(`/contest/${id}/submission`, async (path) => {
    return http.get<L<ContestSubmission>>(path)
      .then((res) => {
        return res.data.data.list
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛提交列表失败')
        throw err
      })
  })
  return (
    <div p-2 flex flex-col items-start max-w="1200px" m-auto gap-2 w-full>
      <table w-full text-sm>
        <thead>
          <tr bg="#eeeeee" text="#888888">
            <th px-4 py-3 font-normal>运行ID</th>
            <th px-4 py-3 font-normal>用户名</th>
            <th px-4 py-3 font-normal>题号</th>
            <th px-4 py-3 font-normal>运行结果</th>
            <th px-4 py-3 font-normal>运行时间</th>
            <th px-4 py-3 font-normal>使用内存</th>
            <th px-4 py-3 font-normal>代码长度</th>
            <th px-4 py-3 font-normal>使用语言</th>
            <th px-4 py-3 font-normal>提交时间</th>
          </tr>
        </thead>
        <tbody>
        {data?.map((submission, index) => (
          <tr key={index} bg="hover:#f0f0f0" cursor-pointer>
            <td width={'10%'} px-4 py-3>
              <div text-center>{submission.id}</div>
            </td>
            <td width={'10%'} px-4 py-3>
              <div text-center>{submission.user}</div>
            </td>
            <td width={'10%'} px-4 py-3>
              <div text-center>
                <LinkComponent href={`/c/${id}/p/${submission.alias}`}>
                  {submission.alias}
                </LinkComponent>
              </div>
            </td>
            <td width={'10%'} px-4 py-3>
              <div text-center style={{ color: statusToColor(submission.result) }}>{statusToMessage(submission.result)}</div>
            </td>
            <td width={'10%'} px-4 py-3>
              <div text-center>{submission.time}ms</div>
            </td>
            <td width={'10%'} px-4 py-3>
              <div text-center>{submission.memory}KB</div>
            </td>
            <td width={'10%'} px-4 py-3>
              <div text-center>{submission.codeLength}B</div>
            </td>
            <td width={'10%'} px-4 py-3>
              <div text-center>{submission.language}</div>
            </td>
            <td width={'20%'} px-4 py-3>
              <div text-center>{submission.submitTime}</div>
            </td>
          </tr>
        ))}
        </tbody>
      </table>
    </div>
  )
}

export default ContestSubmissionListPage
