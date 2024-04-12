export function toFixedNumber(score: number, decimalPlaces: number = 2): number {
  return Number.parseFloat(score.toFixed(decimalPlaces))
}

export function timeDiff(from: string, to: string): number {
  return new Date(to).getTime() - new Date(from).getTime()
}

export function timeDiffString(diff: number) {
  let s: string = ''
  diff /= 1000
  const days = Math.floor(diff / 86400)
  diff = Math.max(diff - days * 86400, 0)
  const hours = Math.floor(diff / 3600)
  diff = Math.max(diff - hours * 3600, 0)
  const minutes = Math.floor(diff / 60)
  diff = Math.max(diff - minutes * 60, 0)
  const seconds = Math.floor(diff)
  if (days > 0) {
    s += `${days}天`
  }
  if (hours > 0) {
    s += `${hours}小时`
  }
  if (minutes > 0) {
    s += `${minutes}分钟`
  }
  if (seconds > 0) {
    s += `${seconds}秒`
  }
  return s
}

export function penaltyToTimeString(penalty: number): string {
  const hours = Math.floor(penalty / 3600)
  penalty -= hours * 3600
  const minutes = Math.floor(penalty / 60)
  penalty -= minutes * 60
  const seconds = Math.floor(penalty)
  let res = ''
  if (hours < 10) {
    res += '0'
  }
  res += `${hours}:`
  if (minutes < 10) {
    res += '0'
  }
  res += `${minutes}:`
  if (seconds < 10) {
    res += '0'
  }
  res += `${seconds}`
  return res
}

export function userRoleToCNString(role: UserRole) {
  let res: string
  switch (role) {
    case 'BANNED':
      res = '封禁用户'
      break
    case 'USER':
      res = '普通用户'
      break
    case 'ADMIN':
      res = '管理员'
      break
    case 'SUPER_ADMIN':
      res = '超级管理员'
      break
  }
  return res
}
