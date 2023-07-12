import React from 'react'
import { NotificationOutlined } from '@ant-design/icons'
import { AnnouncementList } from '../components/AnnouncementList.tsx'

export const HomePage: React.FC = () => {
  return (
    <div className="flex justify-between m-[15px]">
      <div className="w-full">
        <div className="text-xl font-bold pb-4">
          <NotificationOutlined/> 公告
        </div>
        <div>
          <AnnouncementList/>
        </div>
      </div>
    </div>
  )
}
