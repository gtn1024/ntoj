import React, { useEffect, useRef, useState } from 'react'
import type { AxiosError } from 'axios'
import { Button, Drawer, Select, message } from 'antd'
import c from 'classnames'
import { useWindowSize } from 'react-use'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { useLayout } from '../hooks/useLayout.ts'
import { useCodemirrorConfig } from '../hooks/useCodemirrorConfig.ts'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'
import { CodeMirrorEditor } from './CodeMirrorEditor.tsx'

interface Props {
  hBorder: number
  submitUrl: string
  codeLengthLimit: number
  languageOptions: { value: string; label: string }[]
  samples: { input: string; output: string }[]
  timeLimit: number
  memoryLimit: number
}

export const ProblemEditComponent: React.FC<Props> = ({ hBorder, submitUrl, codeLengthLimit, languageOptions, samples, timeLimit, memoryLimit }) => {
  const { isMobile } = useLayout()
  const [code, setCode] = useState('')
  const [language, setLanguage] = useState<string>()
  const [judgeMessage, setJudgeMessage] = useState<SubmissionStatus | JudgeStage | '正在提交'>()
  const [intervalId, setIntervalId] = useState<number | null>(null)
  const [editorLanguage, setEditorLanguage] = useState('cpp')
  const [toolbarVisible, setToolbarVisible] = useState(false)
  const [toolbarSection, setToolbarSection] = useState<'result' | 'input'>('result')
  const [isSubmissionOk, setIsSubmissionOk] = useState(false)
  const [selfInputData, setSelfInputData] = useState(samples[0]?.input ?? '')
  const [submissionResult, setSubmissionResult] = useState<Submission | SelfTestSubmission>()
  const [editorConfigDrawerOpen, setEditorConfigDrawerOpen] = useState(false)
  const [resultMode, setResultMode] = useState<'submit' | 'selfTest'>('submit')
  const { codemirrorConfig, setCodemirrorConfig } = useCodemirrorConfig()

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
              <div flex justify-center items-center h-full text="[#999]">
                提交之后，这里将会显示运行结果
              </div>
              )
            : !isSubmissionOk
                ? (
                  <div flex justify-center items-center h-full gap-2 text="[#999]">
                    <div className="i-eos-icons:bubble-loading"/> 您的代码已提交，正在为您查询结果...
                  </div>
                  )
                : (
                  <div rounded flex flex-col h-full w-full>
                    <div style={{ color: statusToColor(judgeMessage as SubmissionStatus) }}>
                      <div rounded-t bg="[#f0faf7]" flex gap-2 px-6 py-4>
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
                              <div flex items-center gap-1>
                                <div className="i-mdi:clock-outline"/> 运行时间 {submissionResult?.time}ms
                              </div>
                              <div flex items-center gap-1>
                                <div className="i-ion:hardware-chip-outline"/> 占用内存 {submissionResult?.memory}KB
                              </div>
                            </>
                          )
                        }
                      </div>
                      <div rounded-b flex flex-col gap-1 px-6 py-4>
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
                        <div flex flex-col>
                          <div flex gap-4>
                            <div>自测输入</div>
                            <pre flex-1 bg="#f7f8f9">{(submissionResult as SelfTestSubmission).input}</pre>
                          </div>
                          {(submissionResult as SelfTestSubmission).expectedOutput && (
                            <div flex gap-4>
                              <div>预期输出</div>
                              <pre flex-1 bg="#f7f8f9">{(submissionResult as SelfTestSubmission).expectedOutput}</pre>
                            </div>
                          )}
                          <div flex gap-4>
                            <div>实际输出</div>
                            <pre flex-1 bg="#f7f8f9">{(submissionResult as SelfTestSubmission).output}</pre>
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
      <div flex flex-col gap-1 h-full>
        <textarea
            border rounded p-2 w-full h-full outline-none
            placeholder="输入自测用例"
            value={selfInputData}
            onChange={e => setSelfInputData(e.target.value)}
        />
        {samples.length > 0 && (
          <div flex gap-1>
            {samples.map((sample, index) => (
              <button
                key={index}
                border rounded px-2 py-1 hover:bg-gray-200 cursor-pointer outline-none
                onClick={() => {
                  setSelfInputData(sample.input)
                }}
              >载入示例 {index + 1}</button>
            ))}
          </div>
        )}
      </div>
    )
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
    http.post<Submission>(submitUrl, { code, language: Number.parseInt(language) })
      .then((res) => {
        setJudgeMessage(res.data.data.status)
        const id = window.setInterval(() => {
          http.get<Submission>(`/submission/${res.data.data.id}`)
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
    const output = samples.findLast(sample => sample.input === selfInputData)?.output ?? null
    const data = {
      language: Number.parseInt(language),
      code,
      input: selfInputData,
      output,
      timeLimit,
      memoryLimit,
    }
    http.post<SelfTestSubmission>('/self_test', data)
      .then((res) => {
        setJudgeMessage(res.data.data.status)
        const id = window.setInterval(() => {
          http.get<SelfTestSubmission>(`/self_test/${res.data.data.id}`)
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

  const editorWrapperRef = useRef<HTMLDivElement>(null)
  const { height } = useWindowSize()

  function languageLabelToEditorLanguage(id: string) {
    const label = languageOptions.find(language => language.value === id)?.label.toLowerCase()
    if (!label) { return 'cpp' }
    if (label.includes('c++') || label.toLowerCase().includes('cpp')) { return 'cpp' }
    if (label.includes('c#') || label.toLowerCase().includes('csharp')) { return 'csharp' }
    if (label.includes('java')) { return 'java' }
    if (label.includes('py')) { return 'python' }
    if (label.includes('go')) { return 'go' }
    if (label.includes('rust')) { return 'rust' }
    if (label.includes('pascal')) { return 'pascal' }
    if (label.includes('c')) { return 'c' }
    return 'cpp'
  }

  return (
    <>
      <div h="40px" flex items-center justify-between mx-2>
        <div className={'flex'}>
          <Select
            className={c('w-[150px]')}
            value={language}
            onChange={(e) => {
              setLanguage(e)
              setEditorLanguage(languageLabelToEditorLanguage(e))
            }}
            options={languageOptions}
          />
        </div>
        <div flex gap-1>
          <button bg="inherit hover:#f3f3f6" rounded border-none cursor-pointer onClick={() => setEditorConfigDrawerOpen(true)}>
            <div className="i-material-symbols:settings"/>
          </button>
        </div>
        <Drawer title="编辑器设置" placement="right" onClose={() => setEditorConfigDrawerOpen(false)} open={editorConfigDrawerOpen}>
          <div flex flex-col>
            <h2>通用</h2>
            <div flex flex-col gap-4>
              <div flex flex-col>
                <h3>主题</h3>
                <div w-full>
                  <Select
                      w-full
                      value={codemirrorConfig?.theme}
                      onChange={e => setCodemirrorConfig({ ...codemirrorConfig, theme: e })}
                      options={[
                        { value: 'light', label: '浅色' },
                        { value: 'dark', label: '深色' },
                      ]}
                  />
                </div>
              </div>
              <div flex flex-col>
                <h3>字体大小</h3>
                <div w-full>
                  <Select
                      w-full
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
      <div grow ref={editorWrapperRef}>
        <CodeMirrorEditor
          editorHeight={!isMobile ? (height - hBorder - 40 - 40 - (toolbarVisible ? 160 : 0)) : 300}
          value={code}
          setValue={setCode}
          language={editorLanguage}
          theme={codemirrorConfig?.theme}
          fontSize={codemirrorConfig?.fontSize}
        />
      </div>
      <div relative bg-white>
        <div h="[5px]" left-0 absolute top-0 w-full>
          <button
            h="[24px]"
            w="[24px]"
            bg-white
            border-none
            rounded="[12px]"
            text="[#666]"
            block
            cursor-pointer
            left="1/2"
            ml="[-12px]"
            absolute
            top="[-12px]"
            p-0
            onClick={() => {
              setToolbarVisible(!toolbarVisible)
            }}
          >
            {
              toolbarVisible
                ? <div className={isMobile ? 'i-material-symbols:keyboard-arrow-up' : 'i-material-symbols:keyboard-arrow-down'} h="[24px]" w="[24px]"/>
                : <div className={isMobile ? 'i-material-symbols:keyboard-arrow-down' : 'i-material-symbols:keyboard-arrow-up'} h="[24px]" w="[24px]"/>
            }
          </button>
        </div>
        <div className={c('flex', 'justify-between', 'mx-2', 'py-1')}>
          <div className={c('flex', 'gap-1')}>
            <button
              className={c('px-2', 'border-none', 'cursor-pointer', 'rounded', 'hover:bg-gray-200',
                (toolbarVisible && toolbarSection === 'result') ? 'bg-gray-200' : 'bg-white')}
              onClick={() => {
                setToolbarVisible(true)
                setToolbarSection('result')
              }}
            >
              运行结果
            </button>
            <button
              className={c('px-2', 'border-none', 'cursor-pointer', 'bg-white', 'rounded', 'hover:bg-gray-200',
                (toolbarVisible && toolbarSection === 'input') ? 'bg-gray-200' : 'bg-white')}
              onClick={() => {
                setToolbarVisible(true)
                setToolbarSection('input')
              }}
            >
              自测输入
            </button>
            <button
              px-2 cursor-pointer bg-white rounded hover:bg-gray-200 flex items-center border="#32ca99" text="#32ca99" outline-none
              onClick={onSelfTest}
            >
              自测运行
            </button>
          </div>
          <div className={''}>
            <Button type="primary" onClick={onSubmitCode} disabled={!code || !language}>提交</Button>
          </div>
        </div>
        {toolbarVisible && <div className={c(toolbarVisible ? [!isMobile ? 'h-[160px]' : 'h-full'] : ['invisible', 'h-0'])}>
          <div className={c('p-2', 'h-full', !isMobile && 'overflow-y-auto')}>
            {
              toolbarSection === 'result'
                ? <ToolbarResult/>
                : <ToolbarInput/>
            }
          </div>
        </div>}
      </div>
    </>
  )
}

export default ProblemEditComponent
