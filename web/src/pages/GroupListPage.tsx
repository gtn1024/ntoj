import type { FC } from 'react'
import { Link } from 'react-router-dom'
import { Col, Row } from 'antd'
import { useUserStore } from '../stores/useUserStore.tsx'

const GroupItem: FC<{
  group: {
    id: number
    name: string
    userNumber: number
  }
}> = ({ group }) => {
  return (
    <div className="m-4 border border-gray-200 rounded-md border-solid p-2">
      <div className="mb-2 text-5">
        <Link to={`/group/${group.id}`}>{group.name}</Link>
      </div>
      <div className="text-4 text-gray-500">
        <div className="i-mdi:person mr-1" />
        {group.userNumber}
      </div>
    </div>
  )
}

export const GroupListPage: FC = () => {
  const userStore = useUserStore()
  return (
    <div className="mx-auto max-w-1200px">
      <div className="py-4">
        <div className="flex items-center justify-between">
          <div className="text-2xl font-bold">小组</div>
        </div>
        <Row>
          {userStore.user.groups?.map((group) => {
            return (
              <Col span={6} key={group.id}>
                <GroupItem group={group} />
              </Col>
            )
          })}
        </Row>
      </div>
    </div>
  )
}
