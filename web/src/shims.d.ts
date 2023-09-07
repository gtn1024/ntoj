import * as React from 'react'
declare module 'react' {
  interface HTMLAttributes<T> extends AriaAttributes, DOMAttributes<T> {
    flex?: boolean
    m?: string
    ml?: string
    mb?: string
    mr?: string
    mt?: string
    p?: string
    pl?: string
    pb?: string
    pr?: string
    pt?: string
    w?: string
    h?: string
    border?: string | boolean
    grow?: boolean
    rounded?: string | boolean
    bg?: string
    text?: string
    block?: boolean
    left?: string
    absolute?: boolean
    top?: string
    relative?: boolean
    shadow?: boolean
    leading?: string
  }
}
