import { Navigate, useLocation, useRouteError } from 'react-router-dom'
import { ErrorForbidden, ErrorUnauthorized } from '../errors'

export const ErrorPage: React.FC = () => {
  const error = useRouteError() as Error
  const location = useLocation()
  const redirect = encodeURIComponent(`${location.pathname}${location.search}`)
  if (error instanceof ErrorUnauthorized) {
    return <Navigate to={`/sign_in?redirect=${redirect}`} />
  } else if (error instanceof ErrorForbidden) {
    return <div>403 Forbidden</div>
  } else {
    return <div>未知错误</div>
  }
}
