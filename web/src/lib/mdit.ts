import MarkdownIt from 'markdown-it'
import { imgLazyload } from '@mdit/plugin-img-lazyload'
import mathjax3 from 'markdown-it-mathjax3'
import Shiki from '@shikijs/markdown-it'

const mdit = MarkdownIt()
mdit.use(await Shiki({
  themes: {
    light: 'vitesse-light',
    dark: 'vitesse-dark',
  }
}))
mdit.use(imgLazyload)
mdit.use(mathjax3)

export { mdit }
