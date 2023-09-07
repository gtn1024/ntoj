import React from 'react'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import { useParams } from 'react-router-dom'
import c from 'classnames'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { MarkdownArticle } from '../../components/MarkdownArticle.tsx'
import { timeDiff, timeDiffString } from '../../lib/misc.ts'
import { useLayout } from '../../hooks/useLayout.ts'

export const ContestHome: React.FC = () => {
  const { id } = useParams()
  const { data: contest } = useSWR(`/contest/${id}`, async (path) => {
    return http.get<Contest>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛失败')
        throw err
      })
  })
  const { isMobile } = useLayout()
  return (
    <div p-2 flex items-start max-w="[1200px]" m-auto className={c(isMobile && 'flex-col')}>
      <div className={c('bg-white rounded-lg shadow-md pb-8 my-1', isMobile ? 'w-11/12 mx-auto' : 'mx-2 w-3/4')}>
        <div flex flex-col px-4 gap-4>
          <div pt-8>
            <MarkdownArticle data={contest?.description ?? ''}/>
          </div>
        </div>
      </div>
      <div className={c('bg-white rounded-lg shadow-md pb-8 my-1', isMobile ? 'w-11/12 mx-auto' : 'mx-2 w-1/4')}>
        <div pt-8 px-4>
          <h2 font-light>{contest?.title}</h2>
        </div>
        <div flex flex-col px-4 gap-4>
          <div flex justify-between>
            <span text-gray-500>创建人</span>
            <span text-gray-500>{contest?.author}</span>
          </div>
          <div flex justify-between>
            <span text-gray-500>时间</span>
            <span text-gray-500>
              开始：{contest?.startTime}<br/>
              结束：{contest?.endTime}
            </span>
          </div>
          <div flex justify-between>
            <span text-gray-500>持续时间</span>
            <span text-gray-500>{timeDiffString(timeDiff(contest?.startTime ?? '', contest?.endTime ?? ''))}</span>
          </div>
          <div flex justify-between>
            <span text-gray-500>比赛规则</span>
            <span text-gray-500>{contest?.type}</span>
          </div>
          <div flex justify-between>
            <span text-gray-500>比赛人数</span>
            <span text-gray-500>{contest?.userCount}</span>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ContestHome
