import React, { useEffect, useState } from 'react'
import { message } from 'antd'
import type { AxiosError } from 'axios'
import { useNavigate, useParams } from 'react-router-dom'
import type { HttpResponse } from '../../lib/Http'
import { http } from '../../lib/Http'
import type { ContestProblem } from './ContestProblemList'

export const ContestNewClarificationPage: React.FC = () => {
  const nav = useNavigate()
  const { id } = useParams()
  const [boards, setBoards] = useState<ContestProblem[]>([])
  const [selectedBoard, setSelectedBoard] = useState<string>('all')
  const [title, setTitle] = useState<string>('')
  const [content, setContent] = useState<string>('')
  useEffect(() => {
    http.get<ContestProblem[]>(`/contest/${id}/problems`)
      .then((res) => {
        setBoards(res.data.data)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛题目失败')
        throw err
      })
  }, [id])
  const handleBoardChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedBoard(event.target.value)
  }

  const handleTitleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setTitle(event.target.value)
  }

  const handleEditorChange = (content: string) => {
    setContent(content)
  }

  const onSubmit = () => {
    if (!selectedBoard || !title || !content) {
      void message.error('标题或内容不能为空')
      return
    }
    const request = {
      title,
      content,
      contestProblemId: selectedBoard === 'all' ? null : selectedBoard,
    }
    http.post(`/contest/${id}/clarification`, request)
      .then(() => {
        void message.success('提交成功')
        nav(`/c/${id}/clarification`)
      })
      .catch((e: AxiosError<HttpResponse>) => {
        void message.error(e.response?.data.message ?? '提交失败')
      })
  }
  return (
    <div p-2 flex flex-col items-start max-w="1200px" m-auto gap-2 w-full>
      <div flex flex-col w-full gap-2>
        <div flex gap-2 leading-8>
          <select value={selectedBoard} onChange={handleBoardChange} w="20%">
            <option value={'all'}>不选择题目</option>
            {boards.map((board, i) => (
              <option key={i} value={board.alias}>{`${board.alias} - ${board.title}`}</option>
            ))}
          </select>
          <input type="text" value={title} onChange={handleTitleChange} w="80%"/>
        </div>
        <div w-full>
          <textarea w-full rows={20} value={content} onChange={e => handleEditorChange(e.target.value)} />
        </div>
      </div>
      <div>
        <button onClick={onSubmit}>
          提交
        </button>
      </div>
    </div>
  )
}

export default ContestNewClarificationPage
