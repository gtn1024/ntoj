import React, { Fragment, useEffect } from 'react'
import nprogress from 'nprogress'
import 'nprogress/nprogress.css'

export const NProgress: React.FC = () => {
  useEffect(() => {
    nprogress.start()
    return () => {
      nprogress.done()
    }
  }, [])
  return (
    <Fragment />
  )
}
