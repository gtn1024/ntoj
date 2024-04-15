import React from 'react'
import { AnnouncementList } from '../components/AnnouncementList.tsx'

export const HomePage: React.FC = () => {
  return (
    <div className="m-[15px] flex justify-between">
      <div className="w-full">
        <div className="pb-4 text-xl font-bold">
          <div className="i-mingcute:announcement-line" />
          {' '}
          公告
        </div>
        <div>
          <AnnouncementList />
        </div>
      </div>
    </div>
  )
}

export default HomePage
