import React, { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import c from 'classnames'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'
import { useLayout } from '../hooks/useLayout.ts'
import ProblemEditComponent from '../components/ProblemEditComponent.tsx'

export const ProblemPage: React.FC = () => {
  const nav = useNavigate()
  const { isMobile } = useLayout()
  const { alias } = useParams()
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
            return problem.allowAllLanguages || problem.languages?.includes(language.id)
          })
        setLanguageOptions(
          availableLanguages
            .sort((a, b) => {
              return a.name.localeCompare(b.name)
            })
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
  return (
    <div className={c('flex', isMobile ? ['flex-col'] : ['h-[calc(100vh-64px-80px)]'])}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <ProblemDetail data={data} showProblemAlias={true}/>
      </div>
      <div className={c('flex', 'flex-col', !isMobile ? ['w-1/2'] : ['p-2'])}>
        <ProblemEditComponent
            hBorder={64 + 80}
            submitUrl={`/problem/${alias ?? 0}/submit`}
            languageOptions={languageOptions}
            codeLengthLimit={data?.codeLength ?? 16}
        />
      </div>
    </div>
  )
}

export default ProblemPage
