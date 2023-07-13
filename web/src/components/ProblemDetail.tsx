import React from 'react'
import { message } from 'antd'
import { mdit } from '../lib/mdit.ts'

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
        <div className="mb-[20px]">
          <h4 className="font-bold mb-1">{title}</h4>
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
  const SampleField: React.FC<{ title: string; data: string }> = (props) => {
    return (
      <div className="w-full mt-1">
        <div className="flex flex-row justify-between items-center mb-1">
          <span className="grow">{props.title}</span>
          <button onClick={() => copyToClipBoard(props.data)}>复制</button>
        </div>
        <div className="bg-white p-2 border-0 border-l-2 border-solid border-green-300">
          <pre className='m-0'>{props.data}</pre>
        </div>
      </div>
    )
  }
  return (
    <div className="mb-[20px]">
      {props.samples?.map((item, idx) => {
        return (
          <div key={idx}>
            <div key={item.input}>
              <h4 className="font-bold mb-1">样例 {idx + 1}</h4>
              <div className="flex flex-col">
                <SampleField title='输入' data={item.input}/>
                <SampleField title='输出' data={item.output}/>
              </div>
            </div>
          </div>
        )
      })}
    </div>
  )
}

export const ProblemDetail: React.FC<{ data?: Problem }> = (props) => {
  const { data } = props

  return (
    <div className="m-4">
      <div className="p-4">
        <div>
          <h2 className="font-bold">{data?.title}</h2>
        </div>
        <div className="pb-[20px] text-xs">
          <span className="mr-1">时间限制：{data?.timeLimit} ms</span>
          <span className="mr-1">内存限制：{data?.memoryLimit} MB</span>
        </div>
        <div>
          <ProblemSection title='题目背景' markdown={data?.background}/>
          <ProblemSection title='题目描述' markdown={data?.description}/>
          <ProblemSection title='输入描述' markdown={data?.inputDescription}/>
          <ProblemSection title='输出描述' markdown={data?.outputDescription}/>
          <ProblemSampleSection samples={data?.samples ?? []}/>
          <ProblemSection title='提示' markdown={data?.note}/>
        </div>
      </div>
    </div>
  )
}
