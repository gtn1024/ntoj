import MarkdownIt from 'markdown-it'
import { imgLazyload } from '@mdit/plugin-img-lazyload'
import mathjax3 from 'markdown-it-mathjax3'
import hljs from 'highlight.js/lib/core'
import c from 'highlight.js/lib/languages/c'
import cpp from 'highlight.js/lib/languages/cpp'
import java from 'highlight.js/lib/languages/java'
import markdown from 'highlight.js/lib/languages/markdown'
import python from 'highlight.js/lib/languages/python'

export class MdIt {
  instance: MarkdownIt

  constructor() {
    hljs.registerLanguage('c', c)
    hljs.registerLanguage('cpp', cpp)
    hljs.registerLanguage('java', java)
    hljs.registerLanguage('markdown', markdown)
    hljs.registerLanguage('python', python)
    this.instance = MarkdownIt({
      highlight(code, language) {
        const validLang = !!(language && hljs.getLanguage(language))
        if (validLang) {
          const lang = language ?? ''
          return highlightBlock(hljs.highlight(code, { language: lang }).value, lang)
        }
        return highlightBlock(hljs.highlightAuto(code).value, '')
      },
    })
      .use(imgLazyload)
      .use(mathjax3)
  }

  render(data: string): string {
    return this.instance.render(data)
  }
}

export const mdit = new MdIt()

function highlightBlock(str: string, lang?: string) {
  return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`
}
