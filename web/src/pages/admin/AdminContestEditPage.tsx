import React, { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { FormInstance } from 'antd'
import {
  Button,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Modal,
  Select,
  Space,
  Switch,
  Table,
  Transfer,
  message,
} from 'antd'
import dayjs from 'dayjs'
import type { ColumnsType } from 'antd/es/table'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

export const AdminContestEditPage: React.FC = () => {
  const nav = useNavigate()
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const [languages, setLanguages] = useState<string[]>([])
  const [allLanguages, setAllLanguages] = useState<{ key: string, title: string }[]>([])
  const formRef = useRef<FormInstance>(null)
  const modalFormRef = useRef<FormInstance>(null)
  const [contestPermission, setContestPermission] = useState<ContestPermission>('PUBLIC')
  const [problems, setProblems] = useState<AdminDto.ContestProblem[]>([])
  const [allProblems, setAllProblems] = useState<AdminDto.Problem[]>([])
  const [showProblemModal, setShowProblemModal] = useState<boolean>(false)
  const [modalTitle, setModalTitle] = useState<'添加题目' | '修改题目'>('添加题目')
  const [editProblem, setEditProblem] = useState<number | null>(null)
  const [users, setUsers] = useState<number[]>([])
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<AdminDto.Contest>(`/admin/contest/${id}`)
        .then((res) => {
          setLanguages(res.data.data.languages.map(l => l.toString()) ?? [])
          setProblems(res.data.data.problems)
          setUsers(res.data.data.users)
          formRef?.current?.setFieldsValue({
            ...res.data.data,
            time: [
              dayjs(res.data.data.startTime, 'YYYY-MM-DD HH:mm:ss'),
              dayjs(res.data.data.endTime, 'YYYY-MM-DD HH:mm:ss'),
            ],
          })
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取竞赛失败')
          throw err
        })
    }
    http.get<L<AdminDto.Language>>('/admin/language')
      .then((res) => {
        const ls = res.data.data.list
        setAllLanguages(ls.map(l => ({ key: l.id.toString(), title: l.languageName })))
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取语言失败')
        throw err
      })
    http.get<L<AdminDto.Problem>>('/admin/problem')
      .then((res) => {
        setAllProblems(res.data.data.list)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取题目失败')
        throw err
      })
  }, [mode, id, formRef])

  const onSubmit = (v: any) => {
    const params = {
      ...v,
      users,
      languages: languages.map(l => Number.parseInt(l)),
      problems: problems.sort((a, b) => a.contestProblemIndex - b.contestProblemIndex),
      startTime: v.time[0].unix(),
      endTime: v.time[1].unix(),
    }
    if (mode === '新建') {
      http.post('/admin/contest', { ...params })
        .then(() => {
          void message.success('新建竞赛成功')
          nav('/admin/contest')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '新建竞赛失败')
          throw err
        })
    } else {
      http.patch(`/admin/contest/${id}`, { ...params })
        .then(() => {
          void message.success('修改竞赛成功')
          nav('/admin/contest')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '修改竞赛失败')
          throw err
        })
    }
  }

  const problemTableColumns: ColumnsType<AdminDto.ContestProblem> = [
    {
      title: '竞赛题目编号',
      dataIndex: 'contestProblemIndex',
      key: 'contestProblemIndex',
    },
    {
      title: '题目ID',
      dataIndex: 'problemId',
      key: 'problemId',
    },
    {
      title: '题目标题',
      key: 'problemTitle',
      render: (_, record) => {
        const problem = allProblems.find(p => p.id === record.problemId)
        return (
          <>{problem?.title ?? '题目不存在'}</>
        )
      },
    },
    {
      title: '操作',
      key: '操作',
      render: (_value, _record, idx) => (
        <Space size="middle">
          <a onClick={() => {
            setModalTitle('修改题目')
            setEditProblem(idx)
            modalFormRef?.current?.setFieldsValue({
              problemId: problems[idx].problemId.toString(),
              contestProblemIndex: problems[idx].contestProblemIndex.toString(),
            })
            setShowProblemModal(true)
          }}
          >
            编辑
          </a>
          <a onClick={() => {
            setProblems(problems.filter((_, i) => i !== idx))
          }}
          >
            删除
          </a>
        </Space>
      ),
    },
  ]

  const handleModalOk = () => {
    const values = modalFormRef.current?.getFieldsValue(['problemId', 'contestProblemIndex'])
    if (modalTitle === '修改题目' && editProblem != null) {
      setProblems(problems.map((p, i) => {
        if (i === editProblem) {
          return {
            problemId: Number.parseInt(values?.problemId),
            contestProblemIndex: Number.parseInt(values?.contestProblemIndex),
          }
        }
        return p
      }))
    } else {
      setProblems([
        ...problems,
        {
          problemId: Number.parseInt(values?.problemId),
          contestProblemIndex: Number.parseInt(values?.contestProblemIndex),
        },
      ])
    }
    setShowProblemModal(false)
  }

  const handleCancel = () => {
    setShowProblemModal(false)
  }

  return (
    <div className="h-[calc(100vh-64px)] w-full flex justify-between">
      <div className="w-full overflow-y-auto p-4">
        <h2 className="text-xl">
          {mode}
          竞赛
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

          <Form.Item label="竞赛描述" name="description">
            <Input.TextArea rows={6} />
          </Form.Item>

          <Form.Item label="开始结束时间" name="time">
            <DatePicker.RangePicker
              showTime={{ format: 'HH:mm:ss' }}
              format="YYYY-MM-DD HH:mm:ss"
            />
          </Form.Item>

          <Form.Item label="封榜时间" name="freezeTime">
            <InputNumber addonAfter="分钟" />
          </Form.Item>

          <Form.Item label="类型" name="type" initialValue="ICPC">
            <Select
              options={[
                { value: 'ICPC', label: 'ICPC' },
              ]}
            />
          </Form.Item>

          <Form.Item label="允许所有语言" name="allowAllLanguages" valuePropName="checked">
            <Switch />
          </Form.Item>

          <Form.Item label="语言" name="languages">
            <Transfer
              dataSource={allLanguages}
              targetKeys={languages}
              onChange={setLanguages}
              render={item => item.title}
            />
          </Form.Item>

          <Form.Item
            label="显示最终榜单"
            name="showFinalBoard"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Space>
            <Form.Item label="权限" name="permission" initialValue="PUBLIC">
              <Select
                onChange={v => setContestPermission(v as ContestPermission)}
                options={[
                  { value: 'PUBLIC', label: '公开比赛' },
                  { value: 'PRIVATE', label: '私有比赛' },
                  { value: 'PASSWORD', label: '需要密码' },
                ]}
              />
            </Form.Item>
            {
              contestPermission === 'PASSWORD' && (
                <Form.Item label="密码" name="password">
                  <Input />
                </Form.Item>
              )
            }
          </Space>

          <Form.Item label="题目">
            <Space
              direction="vertical"
              style={{
                width: '100%',
              }}
            >
              <Button
                type="primary"
                onClick={() => {
                  setModalTitle('添加题目')
                  setEditProblem(null)
                  setShowProblemModal(true)
                  modalFormRef?.current?.setFieldsValue({
                    problemId: undefined,
                    contestProblemIndex: undefined,
                  })
                }}
              >
                添加题目
              </Button>
              <Table rowKey="contestProblemIndex" columns={problemTableColumns} dataSource={problems} pagination={false} />
              <Modal title={modalTitle} open={showProblemModal} onOk={handleModalOk} onCancel={handleCancel}>
                <Form ref={modalFormRef}>
                  <Form.Item label="题目" name="problemId">
                    <Select
                      options={allProblems.map(p => ({ value: p.id.toString(), label: `${p.id} - ${p.title}` }))}
                    />
                  </Form.Item>
                  <Form.Item label="题目顺序" name="contestProblemIndex">
                    <InputNumber min={1} max={problems.length + 1} />
                  </Form.Item>
                </Form>
              </Modal>
            </Space>
          </Form.Item>

          <Form.Item
            label="是否可见"
            name="visible"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              {mode === '新建' ? '新建' : '修改'}
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  )
}

export default AdminContestEditPage
