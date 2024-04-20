let isDev: boolean

type UserRole = 'BANNED' | 'USER' | 'ADMIN' | 'SUPER_ADMIN'

interface CurrentUser {
  id?: number
  username?: string
  email?: string
  realName?: string
  bio?: string
  role?: UserRole
  registerAt?: number
}

interface User {
  id?: number
  username?: string
  realName?: string
  bio?: string
  registerAt?: string
}

interface Announcement {
  id?: number
  title?: string
  content?: string
  author?: string
  visible?: boolean
  createdAt?: number
}

interface Problem {
  id: number
  title: string
  alias: string
  background?: string
  description?: string
  inputDescription?: string
  outputDescription?: string
  timeLimit?: number
  memoryLimit?: number
  judgeTimes?: number
  samples: ProblemSample[]
  note?: string
  author?: string
  languages?: number[]
  allowAllLanguages: boolean
  codeLength: number
  submitTimes: number
  acceptedTimes: number
}

interface TestcaseDto {
  fileId: number
}

interface ProblemSample {
  input: string
  output: string
}

type SubmissionStatus = |
  'PENDING' |
  'JUDGING' |
  'ACCEPTED' |
  'WRONG_ANSWER' |
  'TIME_LIMIT_EXCEEDED' |
  'MEMORY_LIMIT_EXCEEDED' |
  'RUNTIME_ERROR' |
  'COMPILE_ERROR' |
  'SYSTEM_ERROR' |
  'PRESENTATION_ERROR' |
  'DEPRECATED'

interface Submission {
  id: number
  user: {
    username: string
  }
  code: string
  status: SubmissionStatus
  stage: JudgeStage
  memory?: number
  time?: number
  compileLog?: string
  language: {
    languageName: string
  }
  problem: {
    alias: string
    title: string
  }
  submitTime: string
  testcaseResult?: {
    status: SubmissionStatus
    time: number
    memory: number
  }[]
}

interface SelfTestSubmission {
  id: number
  status: SubmissionStatus
  stage: JudgeStage
  memory?: number
  time?: number
  compileLog?: string
  input: string
  output?: string
  expectedOutput?: string
}

type JudgeStage = 'PENDING' | 'COMPILING' | 'JUDGING' | 'FINISHED'

interface Contest {
  id: number
  title: string
  description?: string
  startTime: string
  endTime: string
  type: ContestType
  permission: ContestPermission
  userCount: number
  users: {
    username: string
    realName?: string
    joinAt: string
  }[]
  author: string
  languages?: number[]
  allowAllLanguages: boolean
  hasPermission: boolean
  freezeTime?: number
  showFinalBoard: boolean
}

type ContestType = |
  'ICPC'

type ContestPermission = |
  'PUBLIC' |
  'PRIVATE' |
  'PASSWORD'

interface Article {
  id: number
  title: string
  content: string
  author: {
    username: string
    realName?: string
  }
  createdAt: number
}

namespace AdminDto {
  interface User {
    id: number
    username: string
    realName?: string
    role: UserRole
    email?: string
    createdAt: string
  }

  interface Contest {
    id: number
    title: string
    description?: string
    startTime: string
    endTime: string
    freezeTime?: number
    type: ContestType
    permission: ContestPermission
    password?: string
    problems: ContestProblem[]
    users: number[]
    languages: number[]
    allowAllLanguages: boolean
    visible: boolean
    showFinalBoard: boolean
    author: string
  }

  interface ContestProblem {
    contestProblemIndex: number
    problemId: number
  }

  interface JudgeClientToken {
    id: number
    name: string
    token: string
    enabled: boolean
  }

  interface Language {
    id: number
    languageName: string
    compileCommand?: string
    executeCommand?: string
    enabled: boolean
    memoryLimitRate?: number
    timeLimitRate?: number
    sourceFilename?: string
    targetFilename?: string
  }

  interface Problem {
    id: number
    title: string
    alias: string
    background?: string
    description?: string
    inputDescription?: string
    outputDescription?: string
    timeLimit?: number
    memoryLimit?: number
    judgeTimes?: number
    samples: ProblemSample[]
    note?: string
    author?: string
    visible?: boolean
    languages?: number[]
    allowAllLanguages: boolean
    createdAt?: string
    testcase: TestcaseDto
    codeLength: number
  }
}
