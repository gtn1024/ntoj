import React, { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import c from 'classnames'
import { useWindowSize } from 'react-use'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'
import { CodeMirrorEditor } from '../components/CodeMirrorEditor.tsx'
import { useLayout } from '../hooks/useLayout.ts'

export const ProblemPage: React.FC = () => {
  const nav = useNavigate()
  const { isMobile } = useLayout()
  const { height } = useWindowSize()
  const { alias } = useParams()
  const [code, setCode] = useState('')
  const { data } = useSWR(`/problem/${alias ?? ''}`, async (path) => {
    return http.get<Problem>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError) => {
        if (err.response?.status === 404) {
          void message.error('题目不存在！')
          nav('/404')
        }
        throw err
      })
  })

  return (
    <div className={c('flex', isMobile && 'flex-col', 'h-[calc(100vh-64px-80px)]')}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <ProblemDetail data={data} />
      </div>
      <div className={c('flex', 'flex-col', !isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <CodeMirrorEditor height={!isMobile ? `${height - 80 - 64 - 40}px` : '300px'} value={code} setValue={setCode}/>
        <div className={c('h-[40px]')}></div>
      </div>
    </div>
  )
}
