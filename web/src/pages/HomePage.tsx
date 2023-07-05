import React from 'react'
import { NotificationOutlined } from '@ant-design/icons'
import { AnnouncementList } from '../components/AnnouncementList.tsx'
import s from './HomePage.module.scss'

export const HomePage: React.FC = () => {
  return (
    <div className={s.wrapper}>
      <div className={s.announcement}>
        <div className={s.title}>
          <NotificationOutlined/> 公告
        </div>
        <div className={s.content}>
          <AnnouncementList/>
        </div>
      </div>
    </div>
  )
}
