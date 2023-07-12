import { createBreakpoint } from 'react-use'
import { useEffect, useState } from 'react'

const useBreakpoint = createBreakpoint({
  'xs': 0,
  'sm': 576,
  'md': 768,
  'lg': 992,
  'xl': 1200,
  '2xl': 1600,
})

export function useLayout() {
  const breakpoint = useBreakpoint()
  const [isMobile, setMobile] = useState(breakpoint === 'xs')
  useEffect(() => {
    setMobile(breakpoint === 'xs')
  }, [breakpoint])
  return { isMobile, breakpoint }
}
