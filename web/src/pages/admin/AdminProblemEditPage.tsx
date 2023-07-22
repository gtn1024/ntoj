import type { FormInstance } from 'antd'
import { Button, Form, Input, InputNumber, Switch, message } from 'antd'
import React, { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { ProblemDetail } from '../../components/ProblemDetail.tsx'

interface Params {
  title: string
  content: string
  visible: boolean
}

export const AdminProblemEditPage: React.FC = () => {
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const nav = useNavigate()
  const formRef = useRef<FormInstance>(null)
  const [data, setData] = useState<Problem>()
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<Problem>(`/admin/problem/${id}`)
        .then((res) => {
          setData(res.data.data)
          formRef?.current?.setFieldsValue({
            alias: res.data.data.alias ?? '',
            title: res.data.data.title ?? '',
            background: res.data.data.background ?? '',
            description: res.data.data.description ?? '',
            inputDescription: res.data.data.inputDescription ?? '',
            outputDescription: res.data.data.outputDescription ?? '',
            timeLimit: res.data.data.timeLimit ?? 1000,
            memoryLimit: res.data.data.memoryLimit ?? 64,
            samples: res.data.data.samples ?? [{ input: '', output: '' }],
            note: res.data.data.note ?? '',
            visible: res.data.data.visible ?? false,
          })
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取题目失败')
          throw err
        })
    }
  }, [mode, id, formRef])

  const onSubmit = (v: Params) => {
    if (mode === '新建') {
      http.post<Problem>('/admin/problem', { ...v })
        .then(() => {
          void message.success('创建成功')
          nav('/admin/problem')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '发布失败')
          throw err
        })
    } else {
      http.patch<void>(`/admin/problem/${id ?? 0}`, { ...v })
        .then(() => {
          void message.success('修改成功')
          nav('/admin/problem')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '修改失败')
          throw err
        })
    }
  }

  return (<>
    <div className="flex justify-between w-full h-[calc(100vh-64px)]">
      <div className="w-1/2 overflow-y-auto p-4">
        <h2 className="text-xl">{mode}题目</h2>
        <Form
          name="basic"
          layout='vertical'
          onFinish={onSubmit}
          onChange={() => setData(formRef.current?.getFieldsValue() as Problem)}
          autoComplete="off"
          ref={formRef}
        >
          <div className="flex">
            <Form.Item label="题号" rules={[{ required: true, message: '请输入标题！' }]} name="alias" className="mr-2">
              <Input/>
            </Form.Item>

            <Form.Item label="标题" rules={[{ required: true, message: '请输入标题！' }]} name="title" className="grow">
              <Input/>
            </Form.Item>
          </div>

          <div className="flex">
            <Form.Item label="内存限制" rules={[{ required: true, message: '请输入内存限制！' }]}
                       name="memoryLimit" className="mr-2" initialValue={128}>
              <InputNumber addonAfter="MB" />
            </Form.Item>

            <Form.Item label="时间限制" rules={[{ required: true, message: '请输入时间限制！' }]}
                       name="timeLimit" className="grow" initialValue={128} >
              <InputNumber addonAfter="ms" />
            </Form.Item>
          </div>

          <Form.Item label="题目背景" name="background">
            <Input.TextArea rows={6}/>
          </Form.Item>

          <Form.Item label="题目描述" name="description">
            <Input.TextArea rows={6}/>
          </Form.Item>

          <Form.Item label="输入描述" name="inputDescription">
            <Input.TextArea rows={4}/>
          </Form.Item>

          <Form.Item label="输出描述" name="outputDescription">
            <Input.TextArea rows={4}/>
          </Form.Item>

          <Form.List name="samples">
            {(fields, { add, remove }) => (<>
              {fields.map(({ key, name, ...restField }) => (
                <div key={key} style={{ display: 'flex', marginBottom: 8, width: '100%' }}>
                  <div className="flex w-full">
                    <Form.Item
                      {...restField}
                      className="w-1/2 mr-2"
                      label={`输入 ${name + 1}`}
                      name={[name, 'input']}
                      rules={[{ required: true, message: '请输入样例输入' }]}
                    >
                      <Input.TextArea rows={4} placeholder='样例输入'/>
                    </Form.Item>
                    <Form.Item
                      {...restField}
                      className="w-1/2 mr-2"
                      label={`输出 ${name + 1}`}
                      name={[name, 'output']}
                      rules={[{ required: true, message: '请输入样例输出' }]}
                    >
                      <Input.TextArea rows={4} placeholder='样例输出'/>
                    </Form.Item>
                  </div>
                  {
                    fields.length > 1 && (<MinusCircleOutlined onClick={() => remove(name)}/>)
                  }
                </div>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                  添加样例
                </Button>
              </Form.Item>
            </>)}
          </Form.List>

          <Form.Item label="提示" name="note">
            <Input.TextArea rows={4}/>
          </Form.Item>

          <Form.Item
            label="是否可见"
            name="visible"
            valuePropName="checked"
          >
            <Switch/>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              {mode === '新建' ? '发布' : '修改'}
            </Button>
          </Form.Item>
        </Form>
      </div>
      <div className="w-1/2 overflow-y-auto p-4">
        <h2 className="text-xl">预览</h2>
        <ProblemDetail data={data}/>
      </div>
    </div>
  </>)
}
