import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { message } from 'antd'
import c from 'classnames'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'
import { useLayout } from '../hooks/useLayout.ts'
import ProblemEditComponent from '../components/ProblemEditComponent.tsx'
import { useLanguages } from '../hooks/useLanguages.ts'

export const ProblemPage: React.FC = () => {
  const nav = useNavigate()
  const { isMobile } = useLayout()
  const { alias } = useParams()
  const [data, setData] = useState<Problem>()

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
    http.get<Problem>(`/problem/${alias ?? ''}`)
      .then((res) => {
        return res.data.data
      })
      .then((problem) => {
        setData(problem)
      })
      .catch((err) => {
        void message.error('题目获取失败！')
        throw err
      })
  }, [alias, nav])
  return (
    <div className={c('flex', isMobile ? ['flex-col'] : ['h-[calc(100vh-64px-80px)]'])}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <ProblemDetail data={data} showProblemAlias />
      </div>
      <div className={c('flex', 'flex-col', !isMobile ? ['w-1/2'] : ['p-2'])}>
        <ProblemEditComponent
          hBorder={64 + 80}
          submitUrl={`/problem/${alias ?? 0}/submit`}
          languageOptions={languageOptions}
          codeLengthLimit={data?.codeLength ?? 16}
          samples={data?.samples ?? []}
          timeLimit={data?.timeLimit || 1000}
          memoryLimit={data?.memoryLimit || 256}
        />
      </div>
    </div>
  )
}

export default ProblemPage
