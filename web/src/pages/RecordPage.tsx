import React, { useMemo } from 'react'
import { Link, useParams } from 'react-router-dom'
import c from 'classnames'
import useSWR from 'swr'
import { http } from '../lib/Http.tsx'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'
import { useLayout } from '../hooks/useLayout.ts'
import { timestampToDateString, toFixedNumber } from '../lib/misc.ts'
import { useLanguages } from '../hooks/useLanguages.ts'
import { CodeHighlight } from '../components/CodeHighlight.tsx'

export const RecordPage: React.FC = () => {
  const { id } = useParams()
  const { data } = useSWR(`/record/${id}`, async (path) => {
    return http.get<RecordDto>(path)
      .then((res) => {
        return res.data.data
      })
  })
  const score = useMemo(() => {
    if (data && data.testcaseResult && !!data.testcaseResult.length) {
      return data.testcaseResult.reduce((acc, cur) => {
        if (cur.status === 'ACCEPTED') {
          return acc + 1
        }
        return acc
      }, 0)
    }
    return 0
  }, [data])
  const { isMobile } = useLayout()
  const { languages } = useLanguages()
  const languageName = useMemo(() => {
    if (!languages || !data) {
      return ''
    }
    return languages[data.lang]?.display || data.lang
  }, [languages, data])
  const highlightLanguage = useMemo(() => {
    if (!languages || !data) {
      return 'plaintext'
    }
    return languages[data.lang]?.highlight || 'plaintext'
  }, [data, languages])

  return (
    <div>
      <div className={c('max-w-[1200px] mx-auto my-2 flex items-start', isMobile && 'flex-col')}>
        <div className={c('bg-white rounded-lg shadow-md pb-8 my-1', isMobile ? 'w-11/12 mx-auto' : 'mx-2 w-3/4')}>
          <div className="px-4 pt-8">
            <h2 className="font-light">
              <span style={{ color: statusToColor(data?.status ?? 'PENDING') }}>
                {statusToMessage(data?.status ?? 'PENDING')}
              </span>
            </h2>
          </div>
          <div>
            <div className="flex flex-col gap-1 px-4">
              {data?.compileLog && (
                <div>
                  <pre>
                    {data?.compileLog}
                  </pre>
                </div>
              )}
              <CodeHighlight code={data?.code} lang={highlightLanguage} />
              <div>
                {!!data?.testcaseResult?.length && (
                  <table className="w-full border-1 border-gray-200 border-solid">
                    <thead>
                      <tr className="text-gray-500 leading-8">
                        <th className="px-2 text-left">#</th>
                        <th className="px-2 text-left">状态</th>
                        <th className="px-2 text-left">分数</th>
                        <th className="px-2 text-left">时间</th>
                        <th className="px-2 text-left">内存</th>
                      </tr>
                    </thead>
                    <tbody>
                      {data?.testcaseResult?.map((item, index) => {
                        return (
                          <tr key={index} className="leading-6">
                            <td className="px-2 text-left">
                              #
                              {index + 1}
                            </td>
                            <td className="px-2 text-left">
                              <span style={{ color: statusToColor(item.status) }}>
                                {statusToMessage(item.status)}
                              </span>
                            </td>
                            <td className="px-2 text-left">{item.status === 'ACCEPTED' ? toFixedNumber(100 / (data?.testcaseResult?.length ?? 100)) : 0}</td>
                            <td className="px-2 text-left">
                              {item.time}
                              ms
                            </td>
                            <td className="px-2 text-left">
                              {item.memory}
                              Kib
                            </td>
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
          <div className="px-4 pt-8">
            <h2 className="font-light">信息</h2>
          </div>
          <div className="flex flex-col gap-4 px-4">
            <div className="flex flex-col gap-2">
              <div className="flex justify-between">
                <span className="text-gray-500">提交者</span>
                <span className="text-gray-500">
                  {data?.user.username}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">提交时间</span>
                <span className="text-gray-500">{timestampToDateString((data?.createdAt || 0) * 1000)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">语言</span>
                <span className="text-gray-500">{languageName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">题目</span>
                <span className="text-gray-500">
                  <Link to={`/p/${data?.problem?.alias}`}>{data?.problem?.title}</Link>
                </span>
              </div>
            </div>
            <div className="flex flex-col gap-2">
              <div className="flex justify-between">
                <span className="text-gray-500">最大内存</span>
                <span className="text-gray-500">
                  {data?.memory}
                  Kib
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">最大时间</span>
                <span className="text-gray-500">
                  {data?.time}
                  ms
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">分数</span>
                <span className="text-gray-500">{toFixedNumber(score)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RecordPage
