import React, { useState } from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { LinkComponent } from '../../components/LinkComponent.tsx'
import { mdit } from '../../lib/mdit.ts'

interface ClarificationReplyDto {
  id: number
  content: string
  user: string
  createdAt: string
}

interface ContestClarificationDetailDto {
  id: number
  title: string
  user: string
  createdAt: string
  contestProblemAlias?: string
  content: string
  replies: ClarificationReplyDto[]
}
export const ContestClarificationDetailPage: React.FC = () => {
  const [replyContent, setReplyContent] = useState('')
  const { id, clarificationId } = useParams()
  const { data: clarification, mutate } = useSWR(`/contest/${id}/clarification/${clarificationId}`, async (path: string) => {
    return http.get<ContestClarificationDetailDto>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取疑问列表失败')
        throw err
      })
  })
  const onReplySubmit = () => {
    if (!replyContent) {
      void message.error('回复内容不能为空')
      return
    }
    http.post<void>(`/contest/${id}/clarification/${clarificationId}/reply`, {
      content: replyContent,
    })
      .then(() => {
        setReplyContent('')
        void message.success('回复成功')
        void mutate()
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '回复失败')
      })
  }
  return (
    <div p-2 flex flex-col items-start max-w="1200px" m-auto gap-2>
      <div w-full rounded b="1px solid #666" px-4 py-2>
        <h1>
          <span flex gap-2>
            {clarification?.contestProblemAlias && (
              <LinkComponent href={`/c/${id}/p/${clarification.contestProblemAlias}`} className="h-full text-#888">
                #{clarification.contestProblemAlias}
              </LinkComponent>
            )}
            {clarification?.title}
          </span>
        </h1>
        <article dangerouslySetInnerHTML={{ __html: mdit.render(clarification?.content ?? '') }}></article>
        <div flex gap-2 pb-4 text="#999">
          <span>{clarification?.user}</span>
          <span>{clarification?.createdAt}</span>
        </div>
      </div>
      <div w-full flex flex-col gap-1>
        {
          clarification?.replies.sort((a, b) => b.id - a.id).map(reply => (
            <div w-full rounded b="1px solid #666" px-4 py-2 flex flex-col>
              <div flex gap-2 pb-4 text="#999">
                <span>{reply.user}</span>
                <span>{reply.createdAt}</span>
              </div>
              <article break-words dangerouslySetInnerHTML={{ __html: mdit.render(reply.content.replace('\n', '\n\n')) }}></article>
            </div>
          ))
        }
      </div>
      <div w-full>
        <textarea w-full h="200px" placeholder="回复内容" value={replyContent} onChange={e => setReplyContent(e.target.value)}/>
        <button onClick={onReplySubmit}>提交</button>
      </div>
    </div>
  )
}
export default ContestClarificationDetailPage
