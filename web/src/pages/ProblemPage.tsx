import React, { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { AxiosError } from 'axios'
import { Button, Select, message } from 'antd'
import c from 'classnames'
import { useWindowSize } from 'react-use'
import { Icon } from '@iconify/react'
import baselineKeyboardArrowDown from '@iconify/icons-ic/baseline-keyboard-arrow-down'
import baselineKeyboardArrowUp from '@iconify/icons-ic/baseline-keyboard-arrow-up'
import loadingAltLoop from '@iconify/icons-line-md/loading-alt-loop'
import clockOutline from '@iconify/icons-mdi/clock-outline'
import hardwareChipOutline from '@iconify/icons-ion/hardware-chip-outline'
import type { HttpResponse, L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { ProblemDetail } from '../components/ProblemDetail.tsx'
import { CodeMirrorEditor } from '../components/CodeMirrorEditor.tsx'
import { useLayout } from '../hooks/useLayout.ts'
import { statusToColor, statusToMessage } from '../lib/SubmissionUtils.ts'

export const ProblemPage: React.FC = () => {
  const nav = useNavigate()
  const { isMobile } = useLayout()
  const { alias } = useParams()
  const [code, setCode] = useState('')
  const [language, setLanguage] = useState<string>()
  const [judgeMessage, setJudgeMessage] = useState<SubmissionStatus | JudgeStage | '正在提交'>()
  const [intervalId, setIntervalId] = useState<number | null>(null)
  const [languageOptions, setLanguageOptions] = useState<{ value: string; label: string }[]>([])
  const [data, setData] = useState<Problem>()
  const [editorLanguage, setEditorLanguage] = useState('cpp')
  const [toolbarVisible, setToolbarVisible] = useState(false)
  const [toolbarSection, setToolbarSection] = useState<'result' | 'input'>('result')
  const [isSubmissionOk, setIsSubmissionOk] = useState(false)

  useEffect(() => {
    Promise.all([
      http.get<L<{
        id: number
        name: string
      }>>('/language')
        .then((res) => {
          return res.data.data.list
        })
        .catch((err: AxiosError<HttpResponse>) => {
          throw err
        }),
      http.get<Problem>(`/problem/${alias ?? ''}`)
        .then((res) => {
          return res.data.data
        })
        .catch((err: AxiosError) => {
          throw err
        }),
    ])
      .then(([languages, problem]) => {
        setData(problem)
        const availableLanguages = languages
          .filter((language) => {
            return problem.allowAllLanguages || problem.languages?.includes(language.id)
          })
        setLanguageOptions(
          availableLanguages
            .sort((a, b) => {
              return a.name.localeCompare(b.name)
            })
            .map(language => ({
              value: language.id.toString(),
              label: language.name,
            })),
        )
      })
      .catch((err) => {
        void message.error('题目获取失败！')
        throw err
      })
  }, [alias, nav])

  const [submissionResult, setSubmissionResult] = useState<Submission>()

  const onSubmitCode = () => {
    if (!language) {
      void message.error('请选择语言')
      return
    }
    if (code.length > (data?.codeLength ?? 0) * 1024) {
      void message.error('代码长度超过限制')
      return
    }
    if (intervalId) {
      clearInterval(intervalId)
    }
    setIsSubmissionOk(false)
    setToolbarVisible(true)
    setJudgeMessage('正在提交')
    http.post<Submission>(`/problem/${alias ?? 0}/submit`, { code, language: Number.parseInt(language) })
      .then((res) => {
        setJudgeMessage(res.data.data.status)
        const id = setInterval(() => {
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
    <div className={c('flex', isMobile ? ['flex-col'] : ['h-[calc(100vh-64px-80px)]'])}>
      <div className={c(!isMobile && ['w-1/2', 'overflow-y-auto'])}>
        <ProblemDetail data={data}/>
      </div>
      <div className={c('flex', 'flex-col', !isMobile ? ['w-1/2'] : ['p-2'])}>
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
            editorHeight={!isMobile ? (height - 80 - 64 - 40 - 40 - (toolbarVisible ? 160 : 0)) : 300}
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
                  ? <Icon icon={isMobile ? baselineKeyboardArrowUp : baselineKeyboardArrowDown} width={24} height={24}/>
                  : <Icon icon={isMobile ? baselineKeyboardArrowDown : baselineKeyboardArrowUp} width={24} height={24}/>
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
          <div className={c(toolbarVisible ? [!isMobile ? 'h-[160px]' : 'h-full'] : ['invisible', 'h-0'])}>
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
                              ? (<div flex justify-center items-center h-full text="[#999]">
                                  <Icon icon={loadingAltLoop} height={24} width={24} /> 您的代码已提交，正在为您查询结果...
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
                                          <Icon icon={clockOutline} height={16} width={16} /> 运行时间 {submissionResult?.time}ms
                                        </div>
                                        <div flex items-center gap-1>
                                          <Icon icon={hardwareChipOutline} height={16} width={16} /> 占用内存 {submissionResult?.memory}KB
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
          </div>
        </div>
      </div>
    </div>
  )
}
