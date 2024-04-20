import { Navigate, useLocation, useRouteError } from 'react-router-dom'
import React from 'react'
import { ErrorForbidden, ErrorNotFound, ErrorUnauthorized } from '../errors'
import NotFoundPage from './404.tsx'

export const ErrorPage: React.FC = () => {
  const error = useRouteError() as Error
  const location = useLocation()
  const redirect = encodeURIComponent(`${location.pathname}${location.search}`)
  if (error instanceof ErrorUnauthorized) {
    return <Navigate to={`/sign_in?redirect=${redirect}`} />
  } else if (error instanceof ErrorForbidden) {
    return <div>403 Forbidden</div>
  } else if (error instanceof ErrorNotFound) {
    return <NotFoundPage />
  } else {
    return <div>未知错误</div>
  }
}
