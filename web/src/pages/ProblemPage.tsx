import React from 'react'
import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import { Button, Space, message } from 'antd'
import { http } from '../lib/Http.tsx'
import { mdit } from '../lib/mdit.ts'
import s from './ProblemPage.module.scss'

interface ProblemSample {
  input: string
  output: string
}

interface Problem {
  id?: number
  title?: string
  alias?: string
  background?: string
  description?: string
  inputDescription?: string
  outputDescription?: string
  timeLimit?: number
  memoryLimit?: number
  judgeTimes?: number
  samples?: ProblemSample[]
  note?: string
  author?: string
}

interface SectionProps {
  title: string
  markdown?: string
}

const ProblemSection: React.FC<SectionProps> = (props) => {
  const { title, markdown } = props
  const html = mdit.render(markdown ?? '')
  return (
    markdown
      ? (
        <div className={s.section}>
          <h3>{title}</h3>
          <div dangerouslySetInnerHTML={{ __html: html }}/>
        </div>
        )
      : null
  )
}

interface ProblemSampleSectionProps {
  samples?: ProblemSample[]
}

const ProblemSampleSection: React.FC<ProblemSampleSectionProps> = (props) => {
  const copyToClipBoard = (data: string) => {
    navigator.clipboard.writeText(data)
      .then(() => {
        void message.success('复制成功')
      })
      .catch(() => {
        void message.error('复制失败')
      })
  }
  return (
    <div className={s.section}>
      {props.samples?.map((item, idx) => {
        return (
          <div className={s.samples} key={idx}>
            <div key={item.input} className={s.sample}>
              <h3>样例 {idx + 1}</h3>
              <div className={s.sampleBody}>
                <div className={s.input}>
                  <div className={s.title}>
                    <span>输入</span>
                    <Button size='small' type='primary' onClick={() => copyToClipBoard(item.input)}>复制</Button>
                  </div>
                  <pre>{item.input}</pre>
                </div>
                <div className={s.output}>
                  <div className={s.title}>
                    <span>输出</span>
                    <Button size='small' type='primary' onClick={() => copyToClipBoard(item.output)}>复制</Button>
                  </div>
                  <pre>{item.output}</pre>
                </div>
              </div>
            </div>
          </div>
        )
      })}
    </div>
  )
}

export const ProblemPage: React.FC = () => {
  const { alias } = useParams()
  const { data } = useSWR(`/problem/${alias ?? ''}`, async (path) => {
    return http.get<Problem>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err) => {
        throw err
      })
  })

  return (
    <div className={s.wrapper}>
      <div className={s.body}>
        <div className={s.title}>
          <h2>{data?.title}</h2>
        </div>
        <div className={s.meta}>
          <Space>
            <span>时间限制：{data?.timeLimit} ms</span>
            <span>内存限制：{data?.memoryLimit} MB</span>
          </Space>
        </div>
        <div className={s.content}>
          <ProblemSection title='题目背景' markdown={data?.background}/>
          <ProblemSection title='题目描述' markdown={data?.description}/>
          <ProblemSection title='输入描述' markdown={data?.inputDescription}/>
          <ProblemSection title='输出描述' markdown={data?.outputDescription}/>
          <ProblemSampleSection samples={data?.samples}/>
          <ProblemSection title='提示' markdown={data?.note}/>
        </div>
      </div>
    </div>
  )
}
