import React, { useEffect, useState } from 'react'
import type { L } from '../lib/Http.tsx'
import { http } from '../lib/Http.tsx'
import { SingleContestCard } from '../components/contest/list/SingleContestCard.tsx'

export const ContestListPage: React.FC = () => {
  const [list, setList] = useState<Contest[]>([])
  useEffect(() => {
    void http.get<L<Contest>>('/contest')
      .then((res) => {
        setList(res.data.data.list)
      })
      .catch((err) => {
        throw err
      })
  }, [])
  return (
    <div className="mx-auto max-w-[1200px] py-4">
      <div className="flex flex-col gap-4">
        {
          list.map((contest) => {
            return (
              <SingleContestCard contest={contest} key={contest.id} />
            )
          })
        }
      </div>
    </div>
  )
}

export default ContestListPage
