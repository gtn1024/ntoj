import React, { useEffect, useState } from 'react'
import c from 'classnames'
import { useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import useSWR from 'swr'
import ProblemEditComponent from '../../components/ProblemEditComponent.tsx'
import { useLayout } from '../../hooks/useLayout.ts'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { ProblemDetail } from '../../components/ProblemDetail.tsx'

export const ContestProblem: React.FC = () => {
  const { isMobile } = useLayout()
  const { id: contestId, alias } = useParams()
  const [languageOptions, setLanguageOptions] = useState<{ value: string; label: string }[]>([])
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
  useEffect(() => {
    if (contest) {
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
        http.get<Problem>(`/contest/${contestId}/problem/${alias}`)
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
              return contest?.allowAllLanguages || contest?.languages?.includes(language.id)
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
    }
  }, [contestId, alias, contest])

  return (
    <div className={c('flex', isMobile ? ['flex-col'] : [])}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto', 'h-[calc(100vh-48px)]'])}>
         <ProblemDetail data={data}/>
      </div>
      <div className={c('flex', 'flex-col', !isMobile ? ['w-1/2'] : ['p-2'])}>
        <ProblemEditComponent
            hBorder={48}
            languageOptions={languageOptions}
            submitUrl={`/contest/${contestId}/problem/${alias}/submit`}
            codeLengthLimit={16}
        />
      </div>
    </div>
  )
}

export default ContestProblem
