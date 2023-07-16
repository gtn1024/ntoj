import React, { useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Button, Select, message } from 'antd'
import c from 'classnames'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'
import { CodeMirrorEditor } from '../components/CodeMirrorEditor.tsx'
import { useLayout } from '../hooks/useLayout.ts'

export const ProblemPage: React.FC = () => {
  const nav = useNavigate()
  const { isMobile } = useLayout()
  const { alias } = useParams()
  const [code, setCode] = useState('')
  const [language, setLanguage] = useState('gnu_cpp17')
  const languageOptions: { value: string;label: string }[] = [
    { value: 'gnu_c11', label: 'C11 (GCC)' },
    { value: 'gnu_cpp14', label: 'C++14 (G++)' },
    { value: 'gnu_cpp17', label: 'C++17 (G++)' },
    { value: 'gnu_cpp20', label: 'C++20 (G++)' },
    { value: 'java_8', label: 'Java8' },
    { value: 'java_11', label: 'Java11' },
    { value: 'java_17', label: 'Java17' },
    { value: 'python_2', label: 'Python2' },
    { value: 'python_3', label: 'Python3' },
  ]
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
  const onSubmitCode = async () => {
    const res = await http.post<Submission>(`/problem/${alias ?? 0}/submit`, { code, language })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '提交失败')
        throw err
      })
    void message.info(`提交成功，提交ID：${res.data.data.id}`)
  }

  const editorWrapperRef = useRef<HTMLDivElement>(null)

  return (
    <div className={c('flex', isMobile && 'flex-col', 'h-[calc(100vh-64px-80px)]')}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <ProblemDetail data={data}/>
      </div>
      <div className={c('flex', 'flex-col', !isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <div className={c('grow')} ref={editorWrapperRef}>
          <CodeMirrorEditor
            height={!isMobile ? `${editorWrapperRef.current?.clientHeight ?? 0}px` : '300px'}
            value={code}
            setValue={setCode}
          />
        </div>
        <div className={c('')}>
          <div className={c('flex', 'justify-between', 'mx-2', 'py-1')}>
            <div className={''}>
              <Select
                className={c('w-[150px]')}
                value={language}
                onChange={setLanguage}
                options={languageOptions}
              />
            </div>
            <div className={''}>
              <Button type="primary" onClick={onSubmitCode} disabled={!code || !language}>提交</Button>
            </div>
          </div>
          <div className={c('invisible', 'h-0')}>
           放置提交结果、测试用例等
          </div>
        </div>
      </div>
    </div>
  )
}
