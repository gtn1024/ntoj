import React, { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { FormInstance } from 'antd'
import { Button, DatePicker, Form, Input, message } from 'antd'
import dayjs from 'dayjs'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { DebounceSelect } from '../../components/antd/DebounceSelect.tsx'

interface GroupValue {
  label: string
  value: number
}

interface ProblemValue {
  label: string
  value: number
}

function getProblemLabel(problem: { id: number, title: string, alias: string }) {
  return `${problem.id}. ${problem.title} (${problem.alias})`
}
function getGroupLabel(group: { id: number, name: string }) {
  return `${group.id}. ${group.name}`
}

export const AdminHomeworkEditPage: React.FC = () => {
  const nav = useNavigate()
  const { id } = useParams()
  const mode = id ? 'edit' : 'create'
  const formRef = useRef<FormInstance>(null)
  const [groups, setGroups] = useState<GroupValue[]>([])
  const [problems, setProblems] = useState<ProblemValue[]>([])
  const fetchProblemList = async (keyword: string): Promise<GroupValue[]> => {
    return http.get<AdminDto.Problem[]>(`/admin/problem/search?keyword=${keyword}`)
      .then((res) => {
        const problems = res.data.data
        return problems.map((problem) => {
          const label = getProblemLabel(problem)
          return ({
            label,
            value: problem.id,
          })
        })
      })
  }
  const fetchGroupList = async (keyword: string): Promise<GroupValue[]> => {
    return http.get<AdminDto.AdminGroup[]>(`/admin/group/search?keyword=${keyword}`)
      .then((res) => {
        const groups = res.data.data
        return groups.map((group) => {
          const label = getGroupLabel(group)
          return ({
            label,
            value: group.id,
          })
        })
      })
  }
  useEffect(() => {
    if (mode === 'edit') {
      http.get<AdminDto.Homework>(`/admin/homework/${id}`)
        .then((res) => {
          formRef.current?.setFieldsValue({
            title: res.data.data.title,
            time: [
              dayjs(res.data.data.startTime * 1000),
              dayjs(res.data.data.endTime * 1000),
            ],
          })
          setGroups(res.data.data.groups.map(group => ({
            label: getGroupLabel(group),
            value: group.id,
          })))
          setProblems(res.data.data.problems.map(problem => ({
            label: getProblemLabel(problem),
            value: problem.id,
          })))
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取作业失败')
          throw err
        })
    }
  }, [id, formRef])

  const onSubmit = (v: any) => {
    const requestMethod = mode === 'create' ? http.post.bind(http) : http.patch.bind(http)
    const url = mode === 'create' ? '/admin/homework' : `/admin/homework/${id}`
    const data = {
      title: v.title,
      groups: groups.map(group => group.value),
      problems: problems.map(problem => problem.value),
      startTime: v.time[0].unix(),
      endTime: v.time[1].unix(),
    }
    requestMethod(url, data)
      .then(() => {
        void message.success(`${mode === 'edit' ? '修改' : '创建'}作业成功`)
        nav('/admin/homework')
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? `${mode === 'edit' ? '修改' : '创建'}作业失败`)
        throw err
      })
  }
  return (
    <div className="h-[calc(100vh-64px)] w-full flex justify-between">
      <div className="w-full overflow-y-auto p-4">
        <h2 className="text-xl">
          { mode === 'edit' ? '修改' : '创建' }
          作业
        </h2>
        <Form
          name="basic"
          layout="vertical"
          onFinish={onSubmit}
          autoComplete="off"
          ref={formRef}
        >
          <Form.Item label="标题" rules={[{ required: true, message: '请输入标题！' }]} name="title" className="grow">
            <Input />
          </Form.Item>

          <Form.Item label="开始结束时间" rules={[{ required: true, message: '请选择开始结束时间！' }]} name="time">
            <DatePicker.RangePicker
              showTime={{ format: 'HH:mm:ss' }}
              format="YYYY-MM-DD HH:mm:ss"
            />
          </Form.Item>

          <Form.Item label="群组">
            <DebounceSelect
              mode="multiple"
              value={groups}
              fetchOptions={fetchGroupList}
              onChange={(newValue) => {
                setGroups(newValue as GroupValue[])
              }}
              style={{ width: '100%' }}
            />
          </Form.Item>

          <Form.Item label="题目">
            <DebounceSelect
              mode="multiple"
              value={problems}
              fetchOptions={fetchProblemList}
              onChange={(newValue) => {
                setProblems(newValue as ProblemValue[])
              }}
              style={{ width: '100%' }}
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              { mode === 'edit' ? '修改' : '创建' }
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  )
}
