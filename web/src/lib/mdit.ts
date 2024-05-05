import MarkdownIt from 'markdown-it'
import { imgLazyload } from '@mdit/plugin-img-lazyload'
import mathjax3 from 'markdown-it-mathjax3'
import Shiki from '@shikijs/markdown-it'

class Mdit {
  mdit: MarkdownIt

  constructor() {
    this.mdit = MarkdownIt()
    this.init()
  }

  private async init() {
    this.mdit.use(await Shiki({
      themes: {
        light: 'vitesse-light',
        dark: 'vitesse-dark',
      },
    }))
    this.mdit.use(imgLazyload)
    this.mdit.use(mathjax3)
  }

  render(content: string): string {
    return this.mdit.render(content)
  }
}

const mdit = new Mdit()

export { mdit }
