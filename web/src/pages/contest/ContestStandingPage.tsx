import React, { useEffect, useState } from 'react'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { Progress, message } from 'antd'
import { useParams } from 'react-router-dom'
import dayjs from 'dayjs'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { LinkComponent } from '../../components/LinkComponent.tsx'
import { penaltyToTimeString } from '../../lib/misc.ts'
import type { ContestProblem } from './ContestProblemList.tsx'

interface ContestStandingSubmission {
  id: number
  user: {
    username: string
    realName?: string
  }
  alias: string
  result: SubmissionStatus
  submitTime: string
}

interface StandingProblem {
  success: boolean
  tried: number
  tryAfterFreeze: number
  successTime?: number
  firstSolved?: boolean
  penalty?: number
}

interface Standing {
  user: {
    username: string
    realName?: string
  }
  solved: number
  penalty: number
  problems: {
    [key: string]: StandingProblem
  }
  submissions: {
    [key: string]: {
      id: number
      result: SubmissionStatus
      time: number
    }[]
  }
}
const colorMap = {
  FIRST_SOLVE: '#54af46',
  SOLVED: '#e5feb9',
  ATTEMPTED: '#fbd0d0',
  PENDING: '#c9d7f9',
}
function getCellColor(problem: StandingProblem) {
  if (problem.tryAfterFreeze > 0) { return colorMap.PENDING }
  if (problem.success) { return problem.firstSolved ? colorMap.FIRST_SOLVE : colorMap.SOLVED }
  if (problem.tried > 0) { return colorMap.ATTEMPTED }
  return 'inherit'
}

function getCellContent(problem: StandingProblem) {
  return problem.tried > 0 && (
    <div relative mx-1 my-1>
      {problem.success
        ? '+'
        : (problem.tryAfterFreeze > 0 ? '?' : '-')
      }
      <br/>
      {problem.tried}/{Math.floor(problem.penalty! / 60)}
    </div>
  )
}

export const ContestStandingPage: React.FC = () => {
  const { id: contestId } = useParams()
  const { data: contest } = useSWR(`/contest/${contestId}`, async (path) => {
    return http.get<Contest>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛失败')
        throw err
      })
  })
  const { data: problems } = useSWR(`/contest/${contestId}/problems`, async (path) => {
    return http.get<ContestProblem[]>(path)
      .then((res) => {
        return res.data.data.sort((a, b) => a.alias.localeCompare(b.alias))
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛题目失败')
        throw err
      })
  })
  const { data: submissions } = useSWR(`/contest/${contestId}/standing`, async (path) => {
    return http.get<ContestStandingSubmission[]>(path)
      .then((res) => {
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取竞赛排名失败')
        throw err
      })
  })
  const [standing, setStanding] = useState<Standing[]>([])
  const [finalStanding, setFinalStanding] = useState(false)
  const [freezeUnixTime, setFreezeUnixTime] = useState(dayjs(Date.now()).unix())
  const [endTime, setEndTime] = useState(dayjs(Date.now()).unix())
  const [startTime, setStartTime] = useState<number>()
  const [contestProgress, setContestProgress] = useState(0)
  const [contestProgressInterval, setContestProgressInterval] = useState<number>()
  useEffect(() => {
    if (!contest) {
      setFinalStanding(false)
      return
    }
    if (contest.showFinalBoard) {
      setFinalStanding(true)
    }
    setFreezeUnixTime(dayjs(contest.endTime).unix() - (contest.freezeTime ?? 0) * 60)
    setStartTime(dayjs(contest.startTime).unix())
    setEndTime(dayjs(contest.endTime).unix())
    if (!contestProgressInterval && startTime && endTime) {
      const now = dayjs(Date.now()).unix()
      if (startTime <= now && now <= endTime) {
        setContestProgressInterval(setInterval(() => {
          if (dayjs(Date.now()).unix() >= endTime) {
            clearInterval(contestProgressInterval)
            setContestProgress(100)
            setContestProgressInterval(undefined)
            return
          }
          setContestProgress((dayjs(Date.now()).unix() - startTime) / (endTime - startTime) * 100)
        }, 1000))
      } else if (now > endTime) {
        setContestProgress(100)
      } else {
        setContestProgress(0)
      }
    }

    if (!contest || !problems || !submissions || !startTime) { return }
    const standing: Standing[] = []
    const tempData: { [key: string]: Standing } = {}
    const firstSolved: { [key: string]: boolean } = {}
    for (const user of contest.users) {
      // const joinAt = dayjs(user.joinAt).unix()
      // if (joinAt > endTime) { continue }
      tempData[user.username] = {
        user: {
          username: user.username,
          realName: user.realName,
        },
        solved: 0,
        penalty: 0,
        problems: {},
        submissions: {},
      }
      for (const problem of problems) {
        if (!tempData[user.username].submissions[problem.alias]) {
          tempData[user.username].submissions[problem.alias] = []
          tempData[user.username].problems[problem.alias] = {
            success: false,
            tried: 0,
            tryAfterFreeze: 0,
          }
        }
      }
    }
    submissions.sort((a, b) => a.id - b.id)
    for (const submission of submissions) {
      if (!contest.users.map(it => it.username).includes(submission.user.username)) { continue }
      const { submitTime, user } = submission
      if (!tempData[user.username]) { continue }
      const time = dayjs(submitTime).unix()
      const relativeTime = time - startTime
      tempData[user.username].submissions[submission.alias].push({
        id: submission.id,
        result: submission.result,
        time: relativeTime,
      })
      if (!tempData[user.username].problems[submission.alias].success) {
        tempData[user.username].problems[submission.alias].penalty = relativeTime
        if (!finalStanding && time >= freezeUnixTime) {
          // 封榜后
          tempData[user.username].problems[submission.alias].tryAfterFreeze++
          tempData[user.username].problems[submission.alias].tried++
        } else {
          switch (submission.result) {
            case 'COMPILE_ERROR':
            case 'SYSTEM_ERROR':
              continue
            case 'ACCEPTED':
              tempData[user.username].solved++
              tempData[user.username].penalty += relativeTime
              tempData[user.username].problems[submission.alias].success = true
              tempData[user.username].problems[submission.alias].successTime = relativeTime
              tempData[user.username].problems[submission.alias].tried++
              if (!firstSolved[submission.alias]) {
                tempData[user.username].problems[submission.alias].firstSolved = true
                firstSolved[submission.alias] = true
              }
              break
            default:
              tempData[user.username].problems[submission.alias].tried++
              tempData[user.username].penalty += 20 * 60 // +20min
          }
        }
      }
    }
    Object.keys(tempData).forEach((user) => {
      standing.push(tempData[user])
    })
    standing.sort((a, b) => {
      if (a.solved === b.solved) { return a.penalty - b.penalty }
      return b.solved - a.solved
    })
    setStanding(standing)
  }, [startTime, contest, problems, submissions, finalStanding, freezeUnixTime, endTime, contestProgressInterval])

  return (
    <div p-2 flex flex-col items-start max-w="1200px" m-auto gap-2>
      <div w-full flex justify-between>
        <h2>比赛排名</h2>
        <div>
          {/* 操作 */}
        </div>
      </div>
      <div w-full flex flex-col>
        <div w-full><Progress percent={contestProgress} showInfo={false} /></div>
        <div w-full justify-between flex text-sm>
          <div>{/* 进行时间 */}</div>
          <div flex gap-1>
            <div style={{ backgroundColor: colorMap.FIRST_SOLVE }}>First to solve problem</div>
            <div style={{ backgroundColor: colorMap.SOLVED }}>Solved problem</div>
            <div style={{ backgroundColor: colorMap.ATTEMPTED }}>Attempted problem</div>
            <div style={{ backgroundColor: colorMap.PENDING }}>Pending judgement</div>
          </div>
          <div>{/* 剩余时间 */}</div>
        </div>
      </div>
      <div w-full>
        <table w-full text-sm>
          <thead>
            <tr bg="#eeeeee" text="#888888">
              <th px-4 py-3 font-normal w-60px></th>
              <th px-4 py-3 font-normal>选手</th>
              <th px-4 py-3 font-normal w-100px>解题数量</th>
              <th px-4 py-3 font-normal w-80px>罚时</th>
              {problems?.map(problem => (
                <th key={problem.alias} px-4 py-3 font-normal w-14>
                  <LinkComponent href={`/c/${contestId}/p/${problem.alias}`}>
                    {problem.alias}
                  </LinkComponent>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {standing.map((user, index) => (
              <tr key={user.user.username} bg={index % 2 === 0 ? '#ffffff' : '#eeeeee'}>
                <td px-1 py-1 text-center>{index + 1}</td>
                <td px-1 py-1 text-center>
                  <div>
                    {user.user.realName && user.user.realName}<br/>
                    {user.user.username}
                  </div>
                </td>
                <td px-1 py-1 text-center>{user.solved}</td>
                <td px-1 py-1 text-center>{penaltyToTimeString(user.penalty)}</td>
                {problems?.map(problem => (
                  <td key={problem.alias} text-center style={{
                    backgroundColor: getCellColor(user.problems[problem.alias]),
                  }}> {getCellContent(user.problems[problem.alias])} </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default ContestStandingPage
