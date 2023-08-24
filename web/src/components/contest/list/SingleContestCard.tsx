import React from 'react'
import c from 'classnames'
import { Link } from 'react-router-dom'

interface Props {
  contest: Contest
}

export const SingleContestCard: React.FC<Props> = (props) => {
  return (
    <div className={c('p-3 h-[160px]', 'border-l-4 border-l-[#54ab4f] border-solid border-[#dddddd]')}>
      <h2>
        <Link to={`/c/${props.contest.id}`}>
          {props.contest.title}
        </Link>
      </h2>
      <p>{props.contest.description}</p>
    </div>
  )
}
