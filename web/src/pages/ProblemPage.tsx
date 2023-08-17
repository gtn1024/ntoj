import React, { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { Button, Select, message } from 'antd'
import c from 'classnames'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'
import { CodeMirrorEditor } from '../components/CodeMirrorEditor.tsx'
import { useLayout } from '../hooks/useLayout.ts'

export const ProblemPage: React.FC = () => {
  const nav = useNavigate()
  const { isMobile } = useLayout()
  const { alias } = useParams()
  const [code, setCode] = useState('')
  const [language, setLanguage] = useState<string>()
  const [judgeMessage, setJudgeMessage] = useState<SubmissionStatus | '正在提交' | null>(null)
  const [intervalId, setIntervalId] = useState<number | null>(null)
  const [languageOptions, setLanguageOptions] = useState<{ value: string; label: string }[]>([])
  const [data, setData] = useState<Problem>()

  useEffect(() => {
    Promise.all([
      http.get<L<{
        id: number
        name: string
      }>>('/language')
        .then((res) => {
          return res.data.data.list
        })
        .catch((err: AxiosError<HttpResponse>) => {
          throw err
        }),
      http.get<Problem>(`/problem/${alias ?? ''}`)
        .then((res) => {
          return res.data.data
        })
        .catch((err: AxiosError) => {
          throw err
        }),
    ])
      .then(([languages, problem]) => {
        setData(problem)
        const availableLanguages = languages
          .filter((language) => {
            return problem.languages?.includes(language.id)
          })
        setLanguageOptions(
          availableLanguages
            .map(language => ({
              value: language.id.toString(),
              label: language.name,
            })),
        )
      })
      .catch((err) => {
        void message.error('题目获取失败！')
        throw err
      })
  }, [alias, nav])

  function statusToMessage(status: SubmissionStatus | '正在提交' | null): string {
    switch (status) {
      case 'PENDING':
        return '正在评测'
      case 'ACCEPTED':
        return '通过'
      case 'WRONG_ANSWER':
        return '答案错误'
      case 'TIME_LIMIT_EXCEEDED':
        return '超时'
      case 'MEMORY_LIMIT_EXCEEDED':
        return '超内存'
      case 'RUNTIME_ERROR':
        return '运行错误'
      case 'COMPILE_ERROR':
        return '编译错误'
      case 'SYSTEM_ERROR':
        return '系统错误'
    }
    return status as string ?? ''
  }

  const onSubmitCode = () => {
    if (!language) {
      void message.error('请选择语言')
      return
    }
    setJudgeMessage('正在提交')
    http.post<Submission>(`/problem/${alias ?? 0}/submit`, { code, language: Number.parseInt(language) })
      .then((res) => {
        void message.info(`提交成功，提交ID：${res.data.data.id}`)
        setJudgeMessage(res.data.data.status)
        if (intervalId) {
          clearInterval(intervalId)
        }
        const id = setInterval(() => {
          http.get<Submission>(`/submission/${res.data.data.id}`)
            .then((res) => {
              setJudgeMessage(res.data.data.status)
              if (res.data.data.status !== 'PENDING') {
                setJudgeMessage(res.data.data.status)
                clearInterval(id)
              }
            })
            .catch((err: AxiosError<HttpResponse>) => {
              void message.error(err.response?.data.message)
              throw err
            })
        }, 1500)
        setIntervalId(id)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '提交失败')
        throw err
      })
  }

  const editorWrapperRef = useRef<HTMLDivElement>(null)

  return (
    <div className={c('flex', isMobile && 'flex-col', 'h-[calc(100vh-64px-80px)]')}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <ProblemDetail data={data}/>
      </div>
      <div className={c('flex', 'flex-col', !isMobile && ['w-1/2'])}>
        <div className={c('grow')} ref={editorWrapperRef}>
          <CodeMirrorEditor
            height={!isMobile ? `${editorWrapperRef.current?.clientHeight ?? 0}px` : '300px'}
            value={code}
            setValue={setCode}
          />
        </div>
        <div className={c('')}>
          <div className={c('flex', 'justify-between', 'mx-2', 'py-1')}>
            <div className={'flex'}>
              <Select
                className={c('w-[150px]')}
                value={language}
                onChange={setLanguage}
                options={languageOptions}
              />
              <div className={c('flex', 'items-center')}>
                <span>{statusToMessage(judgeMessage)}</span>
              </div>
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
