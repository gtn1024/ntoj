import React, { useEffect, useState } from 'react'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { message } from 'antd'
import { useParams } from 'react-router-dom'
import dayjs from 'dayjs'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'
import { LinkComponent } from '../../components/LinkComponent.tsx'
import type { ContestProblem } from './ContestProblemList.tsx'

interface ContestStandingSubmission {
  id: number
  user: string
  alias: string
  result: SubmissionStatus
  submitTime: string
}

interface Standing {
  user: string
  solved: number
  penalty: number
  problems: {
    [key: string]: {
      success: boolean
      tried: number
      tryAfterFreeze: number
      successTime?: number
    }
  }
  submissions: {
    [key: string]: {
      id: number
      result: SubmissionStatus
      time: number
    }[]
  }
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
  const [startTime, setStartTime] = useState<number>()
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

    if (!contest || !problems || !submissions || !startTime) { return }
    const standing: Standing[] = []
    const tempData: { [key: string]: Standing } = {}
    for (const user of contest.users) {
      tempData[user] = {
        user,
        solved: 0,
        penalty: 0,
        problems: {},
        submissions: {},
      }
      for (const problem of problems) {
        if (!tempData[user].submissions[problem.alias]) {
          tempData[user].submissions[problem.alias] = []
          tempData[user].problems[problem.alias] = {
            success: false,
            tried: 0,
            tryAfterFreeze: 0,
          }
        }
      }
    }
    submissions.sort((a, b) => a.id - b.id)
    for (const submission of submissions) {
      if (!contest.users.includes(submission.user)) { continue }
      const { submitTime, user } = submission
      const time = dayjs(submitTime).unix()
      const relativeTime = Math.floor((time - startTime) / 60)
      tempData[user].submissions[submission.alias].push({
        id: submission.id,
        result: submission.result,
        time: relativeTime,
      })
      if (!tempData[user].problems[submission.alias].success) {
        if (!finalStanding && time >= freezeUnixTime) {
          // 封榜后
          tempData[user].problems[submission.alias].tryAfterFreeze++
          tempData[user].problems[submission.alias].tried++
        } else {
          switch (submission.result) {
            case 'COMPILE_ERROR':
            case 'SYSTEM_ERROR':
              continue
            case 'ACCEPTED':
              tempData[user].solved++
              tempData[user].penalty += relativeTime
              tempData[user].problems[submission.alias].success = true
              tempData[user].problems[submission.alias].successTime = relativeTime
              tempData[user].problems[submission.alias].tried++
              break
            default:
              tempData[user].problems[submission.alias].tried++
              tempData[user].penalty += 20
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
  }, [startTime, contest, problems, submissions, finalStanding, freezeUnixTime])
  return (
    <div p-2 flex flex-col items-start max-w="1200px" m-auto gap-2>
      <div w-full flex justify-between>
        <h2>比赛排名</h2>
        <div>
          {/* 操作 */}
        </div>
      </div>
      <div w-full>
        {/* 进度条 */}
      </div>
      <div w-full>
        <table w-full>
          <thead>
            <tr bg="#eeeeee" text="#888888">
              <th px-4 py-3 font-normal w-60px></th>
              <th px-4 py-3 font-normal>选手</th>
              <th px-4 py-3 font-normal w-100px>解题数量</th>
              <th px-4 py-3 font-normal w-80px>罚时</th>
              {problems?.map(problem => (
                <th key={problem.alias} px-4 py-3 font-normal w-100px>
                  <LinkComponent href={`/c/${contestId}/p/${problem.alias}`}>
                    {problem.alias}
                  </LinkComponent>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {standing.map((user, index) => (
              <tr key={user.user} bg={index % 2 === 0 ? '#ffffff' : '#eeeeee'}>
                <td px-4 py-3 text-center>{index + 1}</td>
                <td px-4 py-3 text-center>{user.user}</td>
                <td px-4 py-3 text-center>{user.solved}</td>
                <td px-4 py-3 text-center>{user.penalty}</td>
                {problems?.map(problem => (
                  <td key={problem.alias} text-center style={{
                    backgroundColor: user.problems[problem.alias].success
                      ? '#b7eb8f'
                      : (user.problems[problem.alias].tryAfterFreeze > 0
                          ? '#f3f077'
                          : (user.problems[problem.alias].tried > 0 ? '#ff0049' : 'inherit')
                        ),
                  }}>
                    {user.problems[problem.alias].tried
                      && (<div relative mx-4 my-3>
                          {user.problems[problem.alias].success ? user.problems[problem.alias].successTime : '+'}
                          ({user.problems[problem.alias].tried - user.problems[problem.alias].tryAfterFreeze})
                          {!user.problems[problem.alias].success && user.problems[problem.alias].tryAfterFreeze > 0
                              && (
                                  <span absolute text-sm left="-4" bottom="-3">
                                    {user.problems[problem.alias].tryAfterFreeze}
                                  </span>
                              )}
                        </div>)
                    }
                  </td>
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
