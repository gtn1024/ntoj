import React, { useState } from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { LinkComponent } from '../../components/LinkComponent.tsx'
import { mdit } from '../../lib/mdit.ts'
import { useUserStore } from '../../stores/useUserStore.tsx'

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
  closed: boolean
  sticky: boolean
}
export const ContestClarificationDetailPage: React.FC = () => {
  const userStore = useUserStore()
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
  const onCloseClarification = () => {
    http.patch<void>(`/contest/${id}/clarification/${clarificationId}/close`)
      .then(() => {
        void message.success('操作成功')
        void mutate()
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '操作失败')
      })
  }
  const onStickyClarification = () => {
    http.patch<void>(`/contest/${id}/clarification/${clarificationId}/sticky`)
      .then(() => {
        void message.success('操作成功')
        void mutate()
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '操作失败')
      })
  }
  return (
    <div className="m-auto max-w-[1200px] flex flex-col items-start gap-2 p-2">
      <div className="w-full b-1 b-[#666] rounded b-solid px-4 py-2">
        <h1>
          <span className="flex gap-2">
            {clarification?.contestProblemAlias && (
              <LinkComponent href={`/c/${id}/p/${clarification.contestProblemAlias}`} className="h-full text-#888">
                #
                {clarification.contestProblemAlias}
              </LinkComponent>
            )}
            {clarification?.title}
          </span>
        </h1>
        <article dangerouslySetInnerHTML={{ __html: mdit.render(clarification?.content ?? '') }}></article>
        <div className="flex gap-2 pb-4 text-[#999]">
          <span>{clarification?.user}</span>
          <span>{clarification?.createdAt}</span>
        </div>
      </div>
      <div className="w-full flex flex-col gap-1">
        {
          clarification?.replies.sort((a, b) => b.id - a.id).map(reply => (
            <div key={reply.id} className="w-full flex flex-col b-1 b-[#666] rounded b-solid px-4 py-2">
              <div className="flex gap-2 pb-4 text-[#999]">
                <span>{reply.user}</span>
                <span>{reply.createdAt}</span>
              </div>
              <article className="break-words" dangerouslySetInnerHTML={{ __html: mdit.render(reply.content.replace('\n', '\n\n')) }}></article>
            </div>
          ))
        }
      </div>
      <div className="w-full">
        {
          !clarification?.closed && (
            <>
              <textarea className="h-200px w-full" placeholder="回复内容" value={replyContent} onChange={e => setReplyContent(e.target.value)} />
              <button type="button" onClick={onReplySubmit}>发布</button>
            </>
          )
        }
        {
          userStore.user.role && (['ADMIN', 'SUPER_ADMIN', 'COACH'] as UserRole[]).includes(userStore.user.role) && (
            <>
              <button type="button" onClick={onCloseClarification}>{clarification?.closed ? '开启' : '关闭'}</button>
              <button type="button" onClick={onStickyClarification}>{clarification?.sticky ? '取消置顶' : '置顶'}</button>
            </>
          )
        }
      </div>
    </div>
  )
}
export default ContestClarificationDetailPage
