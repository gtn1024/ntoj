import MarkdownIt from 'markdown-it'
import { imgLazyload } from '@mdit/plugin-img-lazyload'
import mathjax3 from 'markdown-it-mathjax3'

export class MdIt {
  instance: MarkdownIt

  constructor() {
    this.instance = MarkdownIt()
      .use(imgLazyload)
      .use(mathjax3)
  }

  render(data: string): string {
    return this.instance.render(data)
  }
}

export const mdit = new MdIt()
