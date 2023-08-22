const statusMessageMap: { [key in SubmissionStatus]: string } = {
  PENDING: '待评测',
  JUDGING: '评测中',
  ACCEPTED: '答案正确',
  WRONG_ANSWER: '答案错误',
  TIME_LIMIT_EXCEEDED: '超时',
  MEMORY_LIMIT_EXCEEDED: '超内存',
  RUNTIME_ERROR: '运行错误',
  COMPILE_ERROR: '编译错误',
  SYSTEM_ERROR: '系统错误',
  PRESENTATION_ERROR: '格式错误',
  DEPRECATED: '已废弃',
}

const statusColorMap: { [key in SubmissionStatus]: string } = {
  PENDING: '#000',
  JUDGING: '#000',
  ACCEPTED: '#54ab4f',
  WRONG_ANSWER: '#e8615b',
  TIME_LIMIT_EXCEEDED: '#e8615b',
  MEMORY_LIMIT_EXCEEDED: '#e8615b',
  RUNTIME_ERROR: '#e8615b',
  COMPILE_ERROR: '#e8615b',
  SYSTEM_ERROR: '#e8615b',
  PRESENTATION_ERROR: '#e8615b',
  DEPRECATED: '#000',
}

export function statusToColor(status: SubmissionStatus) {
  return statusColorMap[status] || '#000000'
}

export function statusToMessage(status: SubmissionStatus) {
  return statusMessageMap[status] || status
}
