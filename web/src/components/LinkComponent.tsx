import React from 'react'
import { Link } from 'react-router-dom'

interface Props {
  href: string
  children?: React.ReactNode
  className?: string
}

export const LinkComponent: React.FC<Props> = ({ href, children, className }) => {
  const isExternal = href.startsWith('http://') || href.startsWith('https://')

  return (
    isExternal
      ? <a href={href} target="_blank" rel="noreferrer" className={className}>{children}</a>
      : <Link to={href} className={className}>{children}</Link>
  )
}
