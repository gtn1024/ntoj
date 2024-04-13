import antfu from '@antfu/eslint-config'
import { FlatCompat } from '@eslint/eslintrc'

const compat = new FlatCompat()

export default antfu(
  {
    unocss: true,
    react: true,
    ignores: [
      '**/*.json',
    ],
  },
  ...compat.config({
    extends: [
      'plugin:@typescript-eslint/recommended',
      'plugin:react-hooks/recommended',
    ],
    rules: {
      'react-refresh/only-export-components': 'off',
      'curly': 'off',
      'style/brace-style': 'off',
      '@typescript-eslint/brace-style': 'off',
      'max-statements-per-line': 'off',
      'react/no-unknown-property': 'off',
      'ts/no-unused-vars': 'warn',
      'ts/no-explicit-any': 'warn',
    },
  }),
)
