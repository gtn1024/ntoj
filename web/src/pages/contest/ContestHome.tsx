import React, { useState } from 'react'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Modal, message } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import c from 'classnames'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { MarkdownArticle } from '../../components/MarkdownArticle.tsx'
import { timeDiff, timeDiffString } from '../../lib/misc.ts'
import { useLayout } from '../../hooks/useLayout.ts'
import { useUserStore } from '../../stores/useUserStore.tsx'

export const ContestHome: React.FC = () => {
  const { id } = useParams()
  const { data: contest, mutate } = useSWR(`/contest/${id}`, async (path) => {
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
  const userStore = useUserStore()
  const nav = useNavigate()
  const [registerModalOpen, setRegisterModalOpen] = useState(false)
  const [confirmLoading, setConfirmLoading] = useState(false)
  const [password, setPassword] = useState<string>('')
  const handleRegister = () => {
    if (!userStore.user.id) {
      void message.error('请先登录')
      const redirect = encodeURIComponent(`${location.pathname}${location.search}`)
      nav(`/sign_in?redirect=${redirect}`)
      return
    }
    if (contest?.permission === 'PRIVATE') {
      void message.error('您没有权限加入该比赛')
      return
    }
    if (contest?.permission === 'PASSWORD' && !password) {
      void message.error('请输入密码')
      return
    }
    setConfirmLoading(true)
    http.post(`/contest/${id}/register`, { password })
      .then(() => {
        void mutate()
        void message.success('加入比赛成功')
        setRegisterModalOpen(false)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '加入比赛失败')
        throw err
      })
      .finally(() => {
        setConfirmLoading(false)
      })
  }
  return (
    <div className={c(isMobile && 'flex-col', 'p-2 flex items-start max-w-[1200px] m-auto')}>
      <div className={c('bg-white rounded-lg shadow-md pb-8 my-1', isMobile ? 'w-11/12 mx-auto' : 'mx-2 w-3/4')}>
        <div className="flex flex-col gap-4 px-4">
          <div className="pt-8">
            <MarkdownArticle data={contest?.description ?? ''} />
          </div>
        </div>
      </div>
      <div className={c(isMobile ? 'w-11/12 mx-auto' : 'mx-2 w-1/4 min-w-280px')}>
        <div className={c('bg-white rounded-lg shadow-md pb-8 my-1')}>
          <div className="px-4 pt-8">
            <h2 className="font-light">{contest?.title}</h2>
          </div>
          <div className="flex flex-col gap-4 px-4">
            <div className="flex justify-between">
              <span className="text-gray-500">创建人</span>
              <span className="text-gray-500">{contest?.author.username}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">开始时间</span>
              <span className="text-gray-500">{contest?.startTime}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">结束时间</span>
              <span className="text-gray-500">{contest?.endTime}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">持续时间</span>
              <span className="text-gray-500">{timeDiffString(timeDiff(contest?.startTime ?? '', contest?.endTime ?? ''))}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">比赛规则</span>
              <span className="text-gray-500">{contest?.type}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">比赛人数</span>
              <span className="text-gray-500">{contest?.userCount}</span>
            </div>
          </div>
        </div>
        {
          !contest?.hasPermission && (
            <>
              <button className="h-50px w-full" onClick={() => setRegisterModalOpen(true)}>
                {userStore.user.id ? '加入比赛' : '请先登录'}
              </button>
              <Modal
                title="注册比赛"
                open={registerModalOpen}
                onOk={handleRegister}
                confirmLoading={confirmLoading}
                onCancel={() => setRegisterModalOpen(false)}
              >
                <p>是否确认加入比赛？</p>
                {
                  contest?.permission === 'PASSWORD' && (
                    <>
                      <p>本场比赛存在密码，请输入密码</p>
                      <input type="password" value={password} onChange={event => setPassword(event.target.value)} />
                    </>
                  )
                }
              </Modal>
            </>
          )
        }
      </div>
    </div>
  )
}

export default ContestHome
