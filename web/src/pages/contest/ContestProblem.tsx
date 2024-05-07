import React, { useEffect, useMemo, useState } from 'react'
import c from 'classnames'
import { useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import useSWR from 'swr'
import ProblemEditComponent from '../../components/ProblemEditComponent.tsx'
import { useLayout } from '../../hooks/useLayout.ts'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { ProblemDetail } from '../../components/ProblemDetail.tsx'
import { useLanguages } from '../../hooks/useLanguages.ts'

export const ContestProblem: React.FC = () => {
  const { isMobile } = useLayout()
  const { id: contestId, alias } = useParams()
  const [data, setData] = useState<Problem>()
  const { data: contest } = useSWR(`/contest/${contestId}`, async (path) => {
    return http.get<Contest>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛失败')
        throw err
      })
  })

  const { languages } = useLanguages()
  const languageOptions = useMemo(() => {
    if (!languages)
      return []
    const keys = Object.keys(languages)
    return keys.map((key) => {
      return {
        value: key,
        label: languages[key].display,
      }
    })
  }, [languages])
  useEffect(() => {
    if (contest) {
      http.get<Problem>(`/contest/${contestId}/problem/${alias}`)
        .then((res) => {
          return res.data.data
        })
        .then((problem) => {
          setData(problem)
        })
        .catch((err: AxiosError) => {
          void message.error('题目获取失败！')
          throw err
        })
    }
  }, [contestId, alias, contest])

  return (
    <div className={c('flex', isMobile ? ['flex-col'] : [])}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto', 'h-[calc(100vh-48px)]'])}>
        <ProblemDetail data={data} />
      </div>
      <div className={c('flex', 'flex-col', !isMobile ? ['w-1/2'] : ['p-2'])}>
        <ProblemEditComponent
          hBorder={48}
          languageOptions={languageOptions}
          submitUrl={`/contest/${contestId}/problem/${alias}/submit`}
          codeLengthLimit={16}
          samples={data?.samples || []}
          timeLimit={data?.timeLimit || 1000}
          memoryLimit={data?.memoryLimit || 256}
        />
      </div>
    </div>
  )
}

export default ContestProblem
