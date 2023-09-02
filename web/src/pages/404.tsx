import { Link } from 'react-router-dom'

export const NotFoundPage: React.FC = () => {
  return (
    <div h-screen flex flex-col items-center justify-center>
      <h2>
        404 Not Found
      </h2>
      <div mt-2>
        <Link to={'/'} border px-2 py-1>返回首页</Link>
      </div>
    </div>
  )
}
