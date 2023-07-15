var isDev: boolean

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
  id?: number
  title?: string
  alias?: string
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
  createdAt?: string
  visible?: boolean
}

interface ProblemSample {
  input: string
  output: string
}

interface Pagination {
  page: number
  pageSize: number
}

interface Submission {
  id: number
}
