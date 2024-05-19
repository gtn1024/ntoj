let isDev: boolean

interface CurrentUser {
  id?: number
  username?: string
  displayName?: string
  email?: string
  bio?: string
  createdAt?: number
  groups?: {
    id: number
    name: string
    userNumber: number
  }[]
  permission?: string
  iPermission?: bigint
}

interface User {
  id: number
  username: string
  displayName?: string
  email?: string
  bio?: string
  createdAt: number
}

interface Group {
  id: number
  name: string
  users: Array<{
    id: number
    username: string
    displayName?: string
  }>
  homeworks: Array<{
    id: number
    title: string
    startTime: number
    endTime: number
  }>
}

interface Announcement {
  id: number
  title: string
  content: string
  author: string
  visible: boolean
  createdAt: number
}

interface Problem {
  id: number
  title: string
  alias: string
  background?: string
  description?: string
  inputDescription?: string
  outputDescription?: string
  timeLimit: number
  memoryLimit: number
  judgeTimes?: number
  samples: ProblemSample[]
  note?: string
  author: string
  codeLength: number
  submitTimes: number
  acceptedTimes: number
}

interface Homework {
  id: number
  title: string
  startTime: number
  endTime: number
  problems: Array<{
    id: number
    alias: string
    title: string
  }>
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

interface RecordDto {
  createdAt: number
  user: User
  problem: {
    title: string
    alias: string
  } | null
  origin: string
  lang: string
  code: string
  status: SubmissionStatus
  stage: JudgeStage
  time: number | null
  memory: number | null
  compileLog: string | null
  testcaseResult: {
    status: SubmissionStatus
    time: number
    memory: number
    input: string
    output: string
  }[]
  id: string
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
    displayName?: string
    joinAt: string
  }[]
  author: User
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
  author: User
  createdAt: number
  problemAlias?: string
}

namespace AdminDto {
  interface Homework {
    id: number
    title: string
    startTime: number
    endTime: number
    problems: Array<{
      id: number
      alias: string
      title: string
    }>
    groups: Array<{
      id: number
      name: string
    }>
    author: string
  }

  interface AdminUser {
    id: number
    username: string
    displayName?: string
    userRole: string
    email?: string
    createdAt: string
  }

  interface AdminGroup {
    id: number
    name: string
    users: User[]
    creator: string
    createdAt: number
  }

  interface Article {
    id: number
    title: string
    content: string
    author: AdminUser
    visible: boolean
    createdAt: number
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
    visible: boolean
    showFinalBoard: boolean
    author: string
  }

  interface ContestProblem {
    contestProblemIndex: number
    problemId: number
  }

  interface Problem {
    id: number
    title: string
    alias: string
    background?: string
    description?: string
    inputDescription?: string
    outputDescription?: string
    timeLimit: number
    memoryLimit: number
    judgeTimes?: number
    samples: ProblemSample[]
    note?: string
    author: string
    visible: boolean
    createdAt: string
    testcase: TestcaseDto
    codeLength: number
  }
}
