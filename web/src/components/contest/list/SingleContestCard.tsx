import React from 'react'
import c from 'classnames'
import { Link } from 'react-router-dom'
import { timeDiff, timeDiffString } from '../../../lib/misc.ts'

interface Props {
  contest: Contest
}

function permissionToString(permission: ContestPermission): string {
  let res = ''
  switch (permission) {
    case 'PUBLIC':
      res = '公开比赛'
      break
    case 'PRIVATE':
      res = '私有比赛'
      break
    case 'PASSWORD':
      res = '需要密码'
      break
  }
  return res
}

export const SingleContestCard: React.FC<Props> = (props) => {
  return (
    <div className={c('flex flex-col', 'p-3', 'border-l-4 border-l-[#54ab4f] border-solid border-[#dddddd]')}>
      <div className={c('py-2')}>
        <h2>
          <Link to={`/c/${props.contest.id}`}>
            {props.contest.title}
          </Link>
        </h2>
      </div>
      <div className={c('text-base flex flex-col gap-0.5')}>
        <div className={c('flex items-center gap-1')}>
          <div className="i-mdi:calendar" />
          {' '}
          <span>
            {props.contest.startTime}
            {' '}
            ~
            {' '}
            {props.contest.endTime}
          </span>
        </div>
        <div className={c('flex gap-2')}>
          <div>
            <div className="i-mdi:clock-outline" />
            {' '}
            <span>{timeDiffString(timeDiff(props.contest.startTime, props.contest.endTime))}</span>
          </div>
          <span className={c('text-gray-400')}>|</span>
          <div>
            <div className="i-material-symbols:gavel-rounded" />
            {' '}
            <span>{props.contest.type}</span>
          </div>
          <span className={c('text-gray-400')}>|</span>
          <div>
            <div className="i-mdi:account-multiple" />
            {' '}
            <span>
              {props.contest.userCount}
              {' '}
              人
            </span>
          </div>
          <span className={c('text-gray-400')}>|</span>
          <div>
            <div className="i-material-symbols:lock-open-outline" />
            {' '}
            <span>{permissionToString(props.contest.permission)}</span>
          </div>
        </div>
      </div>
    </div>
  )
}
