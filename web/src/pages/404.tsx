import { Link } from 'react-router-dom'

export const NotFoundPage: React.FC = () => {
  return (
    <div className="h-screen flex flex-col justify-center items-center">
      <h2>
        404 Not Found
      </h2>
      <div className='mt-2'>
        <Link to={'/'} className='border px-2 py-1'>返回首页</Link>
      </div>
    </div>
  )
}
