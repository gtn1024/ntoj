import React from 'react'
import { message } from 'antd'
import c from 'classnames'
import { Link } from 'react-router-dom'
import { mdit } from '../lib/mdit.ts'
import { useLayout } from '../hooks/useLayout.ts'

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
          <h4 className="mb-1 font-bold">{title}</h4>
          <div dangerouslySetInnerHTML={{ __html: html }} />
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
  const SampleField: React.FC<{ title: string, data: string }> = (props) => {
    return (
      <div className="mt-1 w-full">
        <div className="mb-1 flex flex-row items-center justify-between">
          <span className="grow">{props.title}</span>
          <button type="button" onClick={() => copyToClipBoard(props.data)}>复制</button>
        </div>
        <div className="border-0 border-l-2 border-green-300 border-solid bg-white p-2">
          <pre className="m-0">{props.data}</pre>
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
              <h4 className="mb-1 font-bold">
                样例
                {idx + 1}
              </h4>
              <div className="flex flex-col">
                <SampleField title="输入" data={item.input} />
                <SampleField title="输出" data={item.output} />
              </div>
            </div>
          </div>
        )
      })}
    </div>
  )
}

export const ProblemDetail: React.FC<{ data?: Problem, showProblemAlias?: boolean }> = ({ data, showProblemAlias }) => {
  const { isMobile } = useLayout()

  return (
    <div className={c(!isMobile && 'm-4')}>
      <div className="p-4">
        <div className="flex items-center justify-between">
          <h2 className="font-bold">
            {showProblemAlias && data?.alias}
            {' '}
            {data?.title}
          </h2>
          <div className="text-4">
            <Link to={`/article?problemAlias=${data?.alias || ''}`}>
              <div className="i-mdi:book-open-variant" />
              {' '}
              查看题解
            </Link>
          </div>
        </div>
        <div className="pb-20px text-xs">
          <span className="mr-1">
            时间限制：
            {data?.timeLimit}
            {' '}
            ms
          </span>
          <span className="mr-1">
            内存限制：
            {data?.memoryLimit}
            {' '}
            MB
          </span>
          <span className="mr-1">
            代码长度限制：
            {data?.codeLength}
            {' '}
            KB
          </span>
        </div>
        <div>
          <ProblemSection title="题目背景" markdown={data?.background} />
          <ProblemSection title="题目描述" markdown={data?.description} />
          <ProblemSection title="输入描述" markdown={data?.inputDescription} />
          <ProblemSection title="输出描述" markdown={data?.outputDescription} />
          <ProblemSampleSection samples={data?.samples ?? []} />
          <ProblemSection title="提示" markdown={data?.note} />
        </div>
      </div>
    </div>
  )
}
