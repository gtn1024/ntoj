import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import c from 'classnames'
import type { AxiosError } from 'axios'
import Highlight from 'react-highlight'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'

interface Submission {
  id: number
  user: {
    username: string
  }
  code: string
  status: SubmissionStatus
  memory?: number
  time?: number
  compileLog?: string
  language: {
    languageName: string
  }
  problem: {
    alias: string
    title: string
  }
  submitTime: string
  testcaseResult?: {
    status: SubmissionStatus
    time: number
    memory: number
  }[]
}

export const RecordPage: React.FC = () => {
  const { id } = useParams()
  const [data, setData] = useState<Submission>()
  const [score, setScore] = useState<number>()
  useEffect(() => {
    void http.get<Submission>(`/submission/${id}`)
      .then((res) => {
        const data = res.data.data
        setData(data)
        if (data.testcaseResult) {
          const score = data.testcaseResult.reduce((acc, cur) => {
            if (cur.status === 'ACCEPTED') {
              return acc + 1
            }
            return acc
          }, 0)
          setScore(100 * score / data.testcaseResult.length)
        }
      })
      .catch((err: AxiosError<HttpResponse>) => {
        throw err
      })
  }, [id])

  return (
    <div>
      <div className={c('max-w-[1200px] mx-auto my-2 flex items-start')}>
        <div className={c('bg-white rounded-lg shadow-md w-3/4 mx-2 pb-8')}>
          <div className={c('pt-8 px-4')}>
            <h2 className={c('font-light')}>
              <span style={{ color: statusToColor(data?.status ?? 'PENDING') }}>
                {statusToMessage(data?.status ?? 'PENDING')}
              </span>
            </h2>
          </div>
          <div className={c()}>
            <div className={c('flex flex-col px-4 gap-1')}>
              <div className={c()}>
                <Highlight>
                  {data?.code}
                </Highlight>
              </div>
              <div className={c()}>
                <table className={c('w-full')} border={1}>
                  <thead>
                    <tr className={c('leading-8', 'text-gray-500')}>
                      <th className={c('text-left', 'px-2')}>#</th>
                      <th className={c('text-left', 'px-2')}>状态</th>
                      <th className={c('text-left', 'px-2')}>分数</th>
                      <th className={c('text-left', 'px-2')}>时间</th>
                      <th className={c('text-left', 'px-2')}>内存</th>
                    </tr>
                  </thead>
                    <tbody>
                      {data?.testcaseResult?.map((item, index) => {
                        return (
                          <tr key={index} className={c('leading-6')}>
                            <td className={c('text-left', 'px-2')}>#{index + 1}</td>
                            <td className={c('text-left', 'px-2')}>
                              <span style={{ color: statusToColor(item.status) }}>
                                {statusToMessage(item.status)}
                              </span>
                            </td>
                            <td className={c('text-left', 'px-2')}>{item.status === 'ACCEPTED' ? (100 / (data?.testcaseResult?.length ?? 100)) : 0}</td>
                            <td className={c('text-left', 'px-2')}>{item.time}ms</td>
                            <td className={c('text-left', 'px-2')}>{item.memory}Kib</td>
                          </tr>
                        )
                      })}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        <div className={c('bg-white rounded-lg shadow-md w-1/4 mx-2 pb-8')}>
          <div className={c('pt-8 px-4')}>
            <h2 className={c('font-light')}>信息</h2>
          </div>
          <div className={c('flex flex-col px-4 gap-4')}>
            <div className={c('flex flex-col gap-2')}>
              <div className={c('flex justify-between')}>
                <span className={c('text-gray-500')}>提交者</span>
                <span className={c('text-gray-500')}>
                  <Link to={`/user/${data?.user.username}`}>
                    {data?.user.username}
                  </Link>
                </span>
              </div>
              <div className={c('flex justify-between')}>
                <span className={c('text-gray-500')}>提交时间</span>
                <span className={c('text-gray-500')}>{data?.submitTime}</span>
              </div>
              <div className={c('flex justify-between')}>
                <span className={c('text-gray-500')}>语言</span>
                <span className={c('text-gray-500')}>{data?.language.languageName}</span>
              </div>
              <div className={c('flex justify-between')}>
                <span className={c('text-gray-500')}>题目</span>
                <span className={c('text-gray-500')}>
                  <Link to={`/problem/${data?.problem.alias}`}>{data?.problem.title}</Link>
                </span>
              </div>
            </div>
            <div className={c('flex flex-col gap-2')}>
              <div className={c('flex justify-between')}>
                <span className={c('text-gray-500')}>最大内存</span>
                <span className={c('text-gray-500')}>{data?.memory}Kib</span>
              </div>
              <div className={c('flex justify-between')}>
                <span className={c('text-gray-500')}>最大时间</span>
                <span className={c('text-gray-500')}>{data?.time}ms</span>
              </div>
              <div className={c('flex justify-between')}>
                  <span className={c('text-gray-500')}>分数</span>
                  <span className={c('text-gray-500')}>{score}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
