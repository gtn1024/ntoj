import React from 'react'
import c from 'classnames'
import { useNavigate, useParams } from 'react-router-dom'
import { useLayout } from '../../hooks/useLayout.ts'
import { LinkComponent } from '../../components/LinkComponent.tsx'
import { useContestClarification } from '../../hooks/useContestClarification.ts'

export const ContestClarificationListPage: React.FC = () => {
  const { isMobile } = useLayout()
  const { id } = useParams()
  const nav = useNavigate()

  const { clarifications } = useContestClarification(id)

  return (
    <div p-2 flex flex-col items-start max-w="1200px" m-auto gap-2 className={c(isMobile && 'flex-col')}>
      <div flex justify-end w-full>
        <button onClick={() => nav(`/c/${id}/clarification/new`)}>发起提问</button>
      </div>
      <div w-full>
        <table w-full>
          <thead>
            <tr bg="#eeeeee" text="#888888">
              <th px-4 py-3 font-normal>标题</th>
              <th px-4 py-3 font-normal>作者</th>
              <th px-4 py-3 font-normal>发表时间</th>
              <th px-4 py-3 font-normal>回复数量</th>
            </tr>
          </thead>
          <tbody>
            {clarifications?.map((clarification, index) => (
              <tr key={index} bg="hover:#f0f0f0" cursor-pointer>
                <td width="60%" px-4 py-3>
                  <LinkComponent href={`/c/${id}/clarification/${clarification.id}`} className="text-gray-500">
                    <div text-center>
                      {clarification.sticky && <span className="text-red-500">[置顶]</span>}
                      {' '}
                      {clarification.contestProblemAlias ? `[${clarification.contestProblemAlias}] ${clarification.title}` : clarification.title}
                    </div>
                  </LinkComponent>
                </td>
                <td width="10%" px-4 py-3>
                  <div text-center>{clarification.user}</div>
                </td>
                <td width="20%" px-4 py-3>
                  <div text-center>{clarification.createdAt}</div>
                </td>
                <td width="10%" px-4 py-3>
                  <div text-center>{clarification.replyCount}</div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default ContestClarificationListPage
