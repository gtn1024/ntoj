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
