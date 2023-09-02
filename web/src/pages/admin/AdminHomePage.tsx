import React from 'react'
import { MarkdownArticle } from '../../components/MarkdownArticle.tsx'

export const AdminHomePage: React.FC = () => {
  const data = `## 资助开发

  [![牛客算法入门班](https://uploadfiles.nowcoder.com/images/20220318/59_1647595008108/61B9F57A591BC69479BD00C6ADBAE99B) ](https://www.nowcoder.com/courses/cover/live/724?coupon=A6t6JmF)
  `
  return (
    <div p-4>
      <MarkdownArticle data={data} />
    </div>
  )
}

export default AdminHomePage
