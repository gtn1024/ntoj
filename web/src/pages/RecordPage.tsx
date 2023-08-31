import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import c from 'classnames'
import type { AxiosError } from 'axios'
import Highlight from 'react-highlight'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'
import { useLayout } from '../hooks/useLayout.ts'
import { toFixedNumber } from '../lib/misc.ts'

export const RecordPage: React.FC = () => {
  const { id } = useParams()
  const [data, setData] = useState<Submission>()
  const [score, setScore] = useState<number>(0)
  const { isMobile } = useLayout()
  useEffect(() => {
    void http.get<Submission>(`/submission/${id}`)
      .then((res) => {
        const data = res.data.data
        setData(data)
        if (data.testcaseResult && !!data.testcaseResult.length) {
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
      <div className={c('max-w-[1200px] mx-auto my-2 flex items-start', isMobile && 'flex-col')}>
        <div className={c('bg-white rounded-lg shadow-md pb-8 my-1', isMobile ? 'w-11/12 mx-auto' : 'mx-2 w-3/4')}>
          <div className={c('pt-8 px-4')}>
            <h2 className={c('font-light')}>
              <span style={{ color: statusToColor(data?.status ?? 'PENDING') }}>
                {statusToMessage(data?.status ?? 'PENDING')}
              </span>
            </h2>
          </div>
          <div className={c()}>
            <div className={c('flex flex-col px-4 gap-1')}>
              {data?.compileLog && (
                  <div>
                    <pre>
                      {data?.compileLog}
                    </pre>
                  </div>
              )}
              <div className={c()}>
                <Highlight>
                  {data?.code}
                </Highlight>
              </div>
              <div className={c()}>
                {!!data?.testcaseResult?.length && (
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
                            <td className={c('text-left', 'px-2')}>{item.status === 'ACCEPTED' ? toFixedNumber(100 / (data?.testcaseResult?.length ?? 100)) : 0}</td>
                            <td className={c('text-left', 'px-2')}>{item.time}ms</td>
                            <td className={c('text-left', 'px-2')}>{item.memory}Kib</td>
                          </tr>
                      )
                    })}
                    </tbody>
                  </table>
                )}
              </div>
            </div>
          </div>
        </div>
        <div className={c('bg-white rounded-lg shadow-md pb-8 my-1', isMobile ? 'w-11/12 mx-auto' : 'mx-2 w-1/4')}>
          <div className={c('pt-8 px-4')}>
            <h2 className={c('font-light')}>信息</h2>
          </div>
          <div className={c('flex flex-col px-4 gap-4')}>
            <div className={c('flex flex-col gap-2')}>
              <div className={c('flex justify-between')}>
                <span className={c('text-gray-500')}>提交者</span>
                <span className={c('text-gray-500')}>
                  <Link to={`/u/${data?.user.username}`}>
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
                  <Link to={`/p/${data?.problem.alias}`}>{data?.problem.title}</Link>
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
                  <span className={c('text-gray-500')}>{toFixedNumber(score)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
