import type { FormInstance, UploadFile, UploadProps } from 'antd'
import { Button, Form, Input, InputNumber, Switch, Transfer, Upload, message } from 'antd'
import React, { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import type { RcFile } from 'antd/es/upload'
import type { HttpResponse, L } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { ProblemDetail } from '../../components/ProblemDetail.tsx'

interface Params {
  title: string
  content: string
  languages: number[]
  visible: boolean
}

export const AdminProblemEditPage: React.FC = () => {
  const { pathname } = useLocation()
  const mode = pathname.split('/').pop() === 'new' ? '新建' : '修改'
  const { id } = useParams()
  const nav = useNavigate()
  const formRef = useRef<FormInstance>(null)
  const [data, setData] = useState<Problem>()
  const [languages, setLanguages] = useState<string[]>([])
  const [allLanguages, setAllLanguages] = useState<{ key: string; title: string }[]>([])
  const [testcaseFileId, setTestcaseFileId] = useState<number>()
  useEffect(() => {
    if (mode === '修改' && id) {
      http.get<Problem>(`/admin/problem/${id}`)
        .then((res) => {
          setData(res.data.data)
          setLanguages(res.data.data.languages?.map(l => l.toString()) ?? [])
          setTestcaseFileId(res.data.data.testcase?.fileId)
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
            languages,
            allowAllLanguages: res.data.data.allowAllLanguages ?? false,
            codeLength: res.data.data.codeLength ?? 16,
          })
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '获取题目失败')
          throw err
        })
    }
    http.get<L<Language>>('/admin/language')
      .then((res) => {
        const ls = res.data.data.list
        setAllLanguages(ls.map(l => ({ key: l.id.toString(), title: l.languageName })))
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取语言失败')
        throw err
      })
  }, [mode, id, formRef])

  const onSubmit = (v: Params) => {
    if (!testcaseFileId) {
      void message.error('请上传测试数据')
      return
    }
    const data = { ...v, languages: languages.map(Number), testcase: testcaseFileId }
    if (mode === '新建') {
      http.post<Problem>('/admin/problem', data)
        .then(() => {
          void message.success('创建成功')
          nav('/admin/problem')
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? '发布失败')
          throw err
        })
    } else {
      http.patch<void>(`/admin/problem/${id ?? 0}`, data)
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

  const [fileList, setFileList] = useState<UploadFile[]>([])
  const handleChange: UploadProps['onChange'] = (info) => {
    let newFileList = [...info.fileList]

    // 1. Limit the number of uploaded files
    // Only to show one recent uploaded file, and old ones will be replaced by the new
    newFileList = newFileList.slice(-1)

    // 2. Read from response and show file link
    newFileList = newFileList.map((file) => {
      if (file.response) {
        // Component will show file.url as link
        file.url = file.response.url
      }
      return file
    })

    setFileList(newFileList)
  }
  const props: UploadProps = {
    onChange: handleChange,
    multiple: false,
    accept: 'application/zip, application/x-zip-compressed',
    beforeUpload: (file) => {
      const isZIP = file.type === 'application/zip' || file.type === 'application/x-zip-compressed'
      if (!isZIP) {
        void message.error(`${file.name} is not a zip file`)
        return false
      }
      return isZIP || Upload.LIST_IGNORE
    },
    customRequest: (options) => {
      if (fileList.length !== 1) {
        void message.error('请上传一个文件')
        return
      }
      const formData = new FormData()
      formData.append('file', fileList[0].originFileObj as RcFile)
      http.post<{
        fileId: number
      }>('/admin/problem/uploadTestcase', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
        .then((res) => {
          setTestcaseFileId(res.data.data.fileId)
          options.onSuccess?.(res.data)
        })
        .catch((err: AxiosError<HttpResponse>) => {
          void message.error(err.response?.data.message ?? 'upload failed.')
          options.onError?.(err)
        })
    },
  }
  return (<>
    <div h="[calc(100vh-64px)]" w-full flex justify-between>
      <div w="1/2" overflow-y-auto p-4>
        <h2 text-xl>{mode}题目</h2>
        <Form
          name="basic"
          layout='vertical'
          onFinish={onSubmit}
          onChange={() => setData(formRef.current?.getFieldsValue() as Problem)}
          autoComplete="off"
          ref={formRef}
        >
          <div flex>
            <Form.Item label="题号" rules={[{ required: true, message: '请输入标题！' }]} name="alias" className="mr-2">
              <Input/>
            </Form.Item>

            <Form.Item label="标题" rules={[{ required: true, message: '请输入标题！' }]} name="title" className="grow">
              <Input/>
            </Form.Item>
          </div>

          <div flex>
            <Form.Item label="内存限制" rules={[{ required: true, message: '请输入内存限制！' }]}
                       name="memoryLimit" className="mr-2" initialValue={256}>
              <InputNumber addonAfter="MB" />
            </Form.Item>

            <Form.Item label="时间限制" rules={[{ required: true, message: '请输入时间限制！' }]}
                       name="timeLimit" className="grow" initialValue={1000} >
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
                  <div w-full flex>
                    <Form.Item
                      {...restField}
                      className="mr-2 w-1/2"
                      label={`输入 ${name + 1}`}
                      name={[name, 'input']}
                      rules={[{ required: true, message: '请输入样例输入' }]}
                    >
                      <Input.TextArea rows={4} placeholder='样例输入'/>
                    </Form.Item>
                    <Form.Item
                      {...restField}
                      className="mr-2 w-1/2"
                      label={`输出 ${name + 1}`}
                      name={[name, 'output']}
                      rules={[{ required: true, message: '请输入样例输出' }]}
                    >
                      <Input.TextArea rows={4} placeholder='样例输出'/>
                    </Form.Item>
                  </div>
                  {
                    fields.length > 1 && (<div className="i-mdi:minus-circle-outline" onClick={() => remove(name)}/>)
                  }
                </div>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()} block icon={<div className="i-mdi:plus"/>}>
                  添加样例
                </Button>
              </Form.Item>
            </>)}
          </Form.List>

          <Form.Item label="提示" name="note">
            <Input.TextArea rows={4}/>
          </Form.Item>

          <Form.Item label="允许所有语言" name="allowAllLanguages" valuePropName="checked">
            <Switch/>
          </Form.Item>

          <Form.Item label="代码长度限制" rules={[{ required: true, message: '请输入代码长度限制！' }]}
                     name="codeLength" initialValue={16}>
            <InputNumber addonAfter="KB" />
          </Form.Item>

          <Form.Item label="语言" name="languages">
            <Transfer
              dataSource={allLanguages}
              targetKeys={languages}
              onChange={setLanguages}
              render={item => item.title}
            />
          </Form.Item>

          <Form.Item label="测试数据">
            <Upload {...props} fileList={fileList}>
              <Button icon={<div className="i-mdi:cloud-upload"/> }>Upload</Button>
            </Upload>
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
      <div w="1/2" overflow-y-auto p-4>
        <h2 text-xl>预览</h2>
        <ProblemDetail data={data}/>
      </div>
    </div>
  </>)
}

export default AdminProblemEditPage
