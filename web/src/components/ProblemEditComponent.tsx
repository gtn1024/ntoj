import React, { useEffect, useMemo, useRef, useState } from 'react'
import type { AxiosError } from 'axios'
import { Button, Drawer, Select, message } from 'antd'
import c from 'classnames'
import { useWindowSize } from 'react-use'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { useLayout } from '../hooks/useLayout.ts'
import { useCodemirrorConfig } from '../hooks/useCodemirrorConfig.ts'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'
import { useLanguages } from '../hooks/useLanguages.ts'
import { CodeMirrorEditor } from './CodeMirrorEditor.tsx'

interface Props {
  hBorder: number
  submitUrl: string
  codeLengthLimit: number
  languageOptions?: { value: string, label: string }[]
  samples: { input: string, output: string }[]
  timeLimit: number
  memoryLimit: number
}

export const ProblemEditComponent: React.FC<Props> = ({ hBorder, submitUrl, codeLengthLimit, languageOptions, samples }) => {
  const { isMobile } = useLayout()
  const [code, setCode] = useState('')
  const [language, setLanguage] = useState<string>()
  const [judgeMessage, setJudgeMessage] = useState<SubmissionStatus | JudgeStage | '正在提交'>()
  const [intervalId, setIntervalId] = useState<number | null>(null)
  const [toolbarVisible, setToolbarVisible] = useState(false)
  const [toolbarSection, setToolbarSection] = useState<'result' | 'input'>('result')
  const [isSubmissionOk, setIsSubmissionOk] = useState(false)
  const [selfInputData, setSelfInputData] = useState(samples[0]?.input ?? '')
  const [submissionResult, setSubmissionResult] = useState<RecordDto>()
  const [editorConfigDrawerOpen, setEditorConfigDrawerOpen] = useState(false)
  const [resultMode, setResultMode] = useState<'submit' | 'selfTest'>('submit')
  const { codemirrorConfig, setCodemirrorConfig } = useCodemirrorConfig()

  const { languages } = useLanguages()
  const editorLanguage = useMemo(() => {
    if (!languages || !language) {
      return 'cpp'
    }
    return languages[language].editor || 'cpp'
  }, [languages, language])

  useEffect(() => {
    if (samples.length > 0) {
      setSelfInputData(samples[0].input)
    }
  }, [samples])

  const ToolbarResult: React.FC = () => {
    return (
      <div>
        {
          !judgeMessage
            ? (
              <div className="h-full flex items-center justify-center text-[#999]">
                提交之后，这里将会显示运行结果
              </div>
              )
            : !isSubmissionOk
                ? (
                  <div className="h-full flex items-center justify-center gap-2 text-[#999]">
                    <div className="i-eos-icons:bubble-loading" />
                    {' '}
                    您的代码已提交，正在为您查询结果...
                  </div>
                  )
                : (
                  <div className="h-full w-full flex flex-col rounded">
                    <div style={{ color: statusToColor(judgeMessage as SubmissionStatus) }}>
                      <div className="flex gap-2 rounded-t bg-[#f0faf7] px-6 py-4">
                        <div style={{ color: statusToColor(judgeMessage as SubmissionStatus), fontWeight: 'bold' }}>
                          {resultMode === 'submit'
                            ? statusToMessage(judgeMessage as SubmissionStatus)
                            : (
                                submissionResult?.status === 'ACCEPTED' ? '自测通过' : statusToMessage(judgeMessage as SubmissionStatus)
                              )}
                        </div>
                        {
                          submissionResult?.status === 'ACCEPTED' && (
                            <>
                              <div className="flex items-center gap-1">
                                <div className="i-mdi:clock-outline" />
                                {' '}
                                运行时间
                                {submissionResult?.time}
                                ms
                              </div>
                              <div className="flex items-center gap-1">
                                <div className="i-ion:hardware-chip-outline" />
                                {' '}
                                占用内存
                                {submissionResult?.memory}
                                KB
                              </div>
                            </>
                          )
                        }
                      </div>
                      <div className="flex flex-col gap-1 rounded-b px-6 py-4">
                        {
                          submissionResult?.status === 'ACCEPTED' && (
                            <>
                              答案正确:恭喜！您提交的程序通过了所有的测试用例
                            </>
                          )
                        }
                        {
                          submissionResult?.status === 'COMPILE_ERROR' && (
                            <>
                              <div>编译错误:您提交的程序无法通过编译</div>
                              <div>
                                <pre>{submissionResult.compileLog}</pre>
                              </div>
                            </>
                          )
                        }
                      </div>
                    </div>
                    {
                      resultMode === 'selfTest' && submissionResult?.stage === 'FINISHED' && submissionResult?.status !== 'COMPILE_ERROR' && (
                        <div className="flex flex-col">
                          <div className="flex gap-4">
                            <div>自测输入</div>
                            <pre className="flex-1 bg-#f7f8f9">{submissionResult.testcaseResult[0].input}</pre>
                          </div>
                          <div className="flex gap-4">
                            <div>实际输出</div>
                            <pre className="flex-1 bg-#f7f8f9">{submissionResult.testcaseResult[0].output}</pre>
                          </div>
                        </div>
                      )
                    }
                  </div>
                  )
        }
      </div>
    )
  }
  const ToolbarInput: React.FC = () => {
    return (
      <div className="h-full flex flex-col gap-1">
        <textarea
          className="h-full w-full border rounded p-2 outline-none"
          placeholder="输入自测用例"
          value={selfInputData}
          onChange={e => setSelfInputData(e.target.value)}
        />
        {samples.length > 0 && (
          <div className="flex gap-1">
            {samples.map((sample, index) => (
              <button
                type="button"
                key={index}
                className="cursor-pointer border rounded px-2 py-1 outline-none hover:bg-gray-200"
                onClick={() => {
                  setSelfInputData(sample.input)
                }}
              >
                载入示例
                {index + 1}
              </button>
            ))}
          </div>
        )}
      </div>
    )
  }

  const getSubmitBody = (code: string, lang: string, selfTest: boolean, input?: string) => {
    const body: any = { code, lang }
    if (selfTest) {
      body.selfTest = true
      body.input = input!
    }
    return body
  }

  const sendSubmitRequest = (body: any) => {
    http.post<RecordDto>(submitUrl, body)
      .then((res) => {
        setJudgeMessage(res.data.data.status)
        const id = window.setInterval(() => {
          http.get<RecordDto>(`/record/${res.data.data.id}`)
            .then((res) => {
              if (res.data.data.stage === 'FINISHED') {
                setSubmissionResult(res.data.data)
                setIsSubmissionOk(true)
                setJudgeMessage(res.data.data.status)
                clearInterval(id)
              } else {
                setJudgeMessage(res.data.data.stage)
              }
            })
            .catch((err: AxiosError<HttpResponse>) => {
              void message.error(err.response?.data.message)
              throw err
            })
        }, 1500)
        setIntervalId(id)
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '提交失败')
        throw err
      })
  }

  const onSubmitCode = () => {
    if (!language) {
      void message.error('请选择语言')
      return
    }
    if (!code.length) {
      void message.error('请输入代码')
      return
    }
    if (code.length > (codeLengthLimit ?? 0) * 1024) {
      void message.error('代码长度超过限制')
      return
    }
    if (intervalId) {
      clearInterval(intervalId)
    }
    setResultMode('submit')
    setToolbarSection('result')
    setIsSubmissionOk(false)
    setToolbarVisible(true)
    setJudgeMessage('正在提交')
    const body = getSubmitBody(code, language, false)
    sendSubmitRequest(body)
  }

  const onSelfTest = () => {
    if (!language) {
      void message.error('请选择语言')
      return
    }
    if (!code.length) {
      void message.error('请输入代码')
      return
    }
    if (code.length > (codeLengthLimit ?? 0) * 1024) {
      void message.error('代码长度超过限制')
      return
    }
    if (!selfInputData) {
      void message.error('请输入自测用例')
      return
    }
    if (intervalId) {
      clearInterval(intervalId)
    }
    setResultMode('selfTest')
    setToolbarSection('result')
    setIsSubmissionOk(false)
    setToolbarVisible(true)
    setJudgeMessage('正在提交')
    const body = getSubmitBody(code, language, true, selfInputData)
    sendSubmitRequest(body)
  }

  const editorWrapperRef = useRef<HTMLDivElement>(null)
  const { height } = useWindowSize()

  return (
    <>
      <div className="mx-2 h-40px flex items-center justify-between">
        <div className="flex">
          <Select
            className={c('w-[150px]')}
            value={language}
            onChange={(e) => {
              setLanguage(e)
            }}
            options={languageOptions}
          />
        </div>
        <div className="flex gap-1">
          <button type="button" className="cursor-pointer rounded border-none bg-inherit hover:bg-#f3f3f6" onClick={() => setEditorConfigDrawerOpen(true)}>
            <div className="i-material-symbols:settings" />
          </button>
        </div>
        <Drawer title="编辑器设置" placement="right" onClose={() => setEditorConfigDrawerOpen(false)} open={editorConfigDrawerOpen}>
          <div className="flex flex-col">
            <h2>通用</h2>
            <div className="flex flex-col gap-4">
              <div className="flex flex-col">
                <h3>主题</h3>
                <div className="w-full">
                  <Select
                    className="w-full"
                    value={codemirrorConfig?.theme}
                    onChange={e => setCodemirrorConfig({ ...codemirrorConfig, theme: e })}
                    options={[
                      { value: 'light', label: '浅色' },
                      { value: 'dark', label: '深色' },
                    ]}
                  />
                </div>
              </div>
              <div className="flex flex-col">
                <h3>字体大小</h3>
                <div className="w-full">
                  <Select
                    className="w-full"
                    value={codemirrorConfig?.fontSize}
                    onChange={e => setCodemirrorConfig({ ...codemirrorConfig, fontSize: e })}
                    options={[
                      { value: 14, label: '默认' },
                      { value: 12, label: '小' },
                      { value: 16, label: '大' },
                      { value: 18, label: '更大' },
                      { value: 20, label: '加大' },
                    ]}
                  />
                </div>
              </div>
            </div>
          </div>
        </Drawer>
      </div>
      <div className="grow" ref={editorWrapperRef}>
        <CodeMirrorEditor
          editorHeight={!isMobile ? (height - hBorder - 40 - 40 - (toolbarVisible ? 160 : 0)) : 300}
          value={code}
          setValue={setCode}
          language={editorLanguage}
          theme={codemirrorConfig?.theme}
          fontSize={codemirrorConfig?.fontSize}
        />
      </div>
      <div className="relative bg-white">
        <div className="absolute left-0 top-0 h-[5px] w-full">
          <button
            type="button"
            className="absolute left-1/2 top-[-12px] ml-[-12px] block h-24px w-24px cursor-pointer rounded-[12px] border-none bg-white p-0 text-[#666]"
            onClick={() => {
              setToolbarVisible(!toolbarVisible)
            }}
          >
            {
              toolbarVisible
                ? <div className={c(isMobile ? 'i-material-symbols:keyboard-arrow-up' : 'i-material-symbols:keyboard-arrow-down', 'h-24px w-24px')} />
                : <div className={c(isMobile ? 'i-material-symbols:keyboard-arrow-down' : 'i-material-symbols:keyboard-arrow-up', 'h-24px w-24px')} />
            }
          </button>
        </div>
        <div className={c('flex', 'justify-between', 'mx-2', 'py-1')}>
          <div className={c('flex', 'gap-1')}>
            <button
              type="button"
              className={c('px-2', 'border-none', 'cursor-pointer', 'rounded', 'hover:bg-gray-200', (toolbarVisible && toolbarSection === 'result') ? 'bg-gray-200' : 'bg-white')}
              onClick={() => {
                setToolbarVisible(true)
                setToolbarSection('result')
              }}
            >
              运行结果
            </button>
            <button
              type="button"
              className={c('px-2', 'border-none', 'cursor-pointer', 'bg-white', 'rounded', 'hover:bg-gray-200', (toolbarVisible && toolbarSection === 'input') ? 'bg-gray-200' : 'bg-white')}
              onClick={() => {
                setToolbarVisible(true)
                setToolbarSection('input')
              }}
            >
              自测输入
            </button>
            <button
              type="button"
              className="flex cursor-pointer items-center border-#32ca99 rounded bg-white px-2 text-#32ca99 outline-none hover:bg-gray-200"
              onClick={onSelfTest}
            >
              自测运行
            </button>
          </div>
          <div className="">
            <Button type="primary" onClick={onSubmitCode} disabled={!code || !language}>提交</Button>
          </div>
        </div>
        {toolbarVisible && (
          <div className={c(toolbarVisible ? [!isMobile ? 'h-[160px]' : 'h-full'] : ['invisible', 'h-0'])}>
            <div className={c('p-2', 'h-full', !isMobile && 'overflow-y-auto')}>
              {
              toolbarSection === 'result'
                ? <ToolbarResult />
                : <ToolbarInput />
            }
            </div>
          </div>
        )}
      </div>
    </>
  )
}

export default ProblemEditComponent
