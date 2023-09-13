import React, { useRef, useState } from 'react'
import type { AxiosError } from 'axios'
import { Button, Select, message } from 'antd'
import c from 'classnames'
import { useWindowSize } from 'react-use'
import type { HttpResponse } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { useLayout } from '../hooks/useLayout.ts'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'
import { CodeMirrorEditor } from './CodeMirrorEditor.tsx'

interface Props {
  hBorder: number
  submitUrl: string
  codeLengthLimit: number
  languageOptions: { value: string; label: string }[]
}

export const ProblemEditComponent: React.FC<Props> = ({ hBorder, submitUrl, codeLengthLimit, languageOptions }) => {
  const { isMobile } = useLayout()
  const [code, setCode] = useState('')
  const [language, setLanguage] = useState<string>()
  const [judgeMessage, setJudgeMessage] = useState<SubmissionStatus | JudgeStage | '正在提交'>()
  const [intervalId, setIntervalId] = useState<number | null>(null)
  const [editorLanguage, setEditorLanguage] = useState('cpp')
  const [toolbarVisible, setToolbarVisible] = useState(false)
  const [toolbarSection, setToolbarSection] = useState<'result' | 'input'>('result')
  const [isSubmissionOk, setIsSubmissionOk] = useState(false)

  const [submissionResult, setSubmissionResult] = useState<Submission>()

  const onSubmitCode = () => {
    if (!language) {
      void message.error('请选择语言')
      return
    }
    if (code.length > (codeLengthLimit ?? 0) * 1024) {
      void message.error('代码长度超过限制')
      return
    }
    if (intervalId) {
      clearInterval(intervalId)
    }
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
      <div className={c('h-[40px]', 'flex', 'items-center')}>
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
      </div>
      <div grow ref={editorWrapperRef}>
        <CodeMirrorEditor
          editorHeight={!isMobile ? (height - hBorder - 40 - 40 - (toolbarVisible ? 160 : 0)) : 300}
          value={code}
          setValue={setCode}
          language={editorLanguage}
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
            {
              /*
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
              className={
              c('px-2', 'border', 'cursor-pointer', 'bg-white', 'rounded', 'hover:bg-gray-200', 'flex',
                'items-center', 'border-[#32ca99]', 'text-[#32ca99]', 'outline-none')
            }>
              <Icon icon={playIcon} width={24} height={24}/> 自测运行
            </button>
               */
            }
          </div>
          <div className={''}>
            <Button type="primary" onClick={onSubmitCode} disabled={!code || !language}>提交</Button>
          </div>
        </div>
        {toolbarVisible && <div className={c(toolbarVisible ? [!isMobile ? 'h-[160px]' : 'h-full'] : ['invisible', 'h-0'])}>
          <div className={c('p-2', 'h-full', !isMobile && 'overflow-y-auto')}>
            {
              toolbarSection === 'result'
                ? (
                  <div>
                    {
                      !judgeMessage
                        ? (<div flex justify-center items-center h-full text="[#999]">
                            提交之后，这里将会显示运行结果
                          </div>)
                        : !isSubmissionOk
                            ? (<div flex justify-center items-center h-full gap-2 text="[#999]">
                                <div className="i-eos-icons:bubble-loading"/> 您的代码已提交，正在为您查询结果...
                              </div>)
                            : (<div
                                  rounded flex flex-col h-full w-full
                                  style={{
                                    color: statusToColor(judgeMessage as SubmissionStatus),
                                  }}
                               >
                                <div rounded-t bg="[#f0faf7]" flex gap-2 px-6 py-4>
                                  <div style={{
                                    color: statusToColor(judgeMessage as SubmissionStatus),
                                    fontWeight: 'bold',
                                  }}>
                                    {statusToMessage(judgeMessage as SubmissionStatus)}
                                  </div>
                                  {
                                    submissionResult?.status === 'ACCEPTED' && (<>
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
                                    submissionResult?.status === 'ACCEPTED' && (<>
                                      答案正确:恭喜！您提交的程序通过了所有的测试用例
                                    </>)
                                  }
                                  {
                                    submissionResult?.status === 'COMPILE_ERROR' && (<>
                                      <div>编译错误:您提交的程序无法通过编译</div>
                                      <div>
                                        <pre>{submissionResult.compileLog}</pre>
                                      </div>
                                    </>)
                                  }
                                </div>
                              </div>)
                    }
                  </div>
                  )
                : (
                  <div>

                  </div>
                  )
            }
          </div>
        </div>}
      </div>
    </>
  )
}

export default ProblemEditComponent
