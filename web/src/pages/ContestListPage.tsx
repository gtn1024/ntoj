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
    <div max-w="[1200px]" mx-auto py-4>
      <div flex flex-col gap-4>
        {
          list.map((contest, idx) => {
            return (
              <SingleContestCard contest={contest} key={idx} />
            )
          })
        }
      </div>
    </div>
  )
}

export default ContestListPage
