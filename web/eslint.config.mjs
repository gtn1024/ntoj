import antfu from '@antfu/eslint-config'

export default antfu(
  {
    // Enable stylistic formatting rules
    stylistic: true,

    unocss: true,

    typescript: true,
    react: true,

    // Disable jsonc and yaml support
    jsonc: false,
    yaml: false,
  },
  {
    rules: {
      'curly': 'off',
      'style/brace-style': 'off',
    },
  },
)
