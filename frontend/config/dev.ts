import type { UserConfigExport } from '@tarojs/cli'

export default {
  env: {
    NODE_ENV: '"development"'
  },
  defineConstants: {},
  mini: {},
  h5: {
    devServer: {
      port: 10086,
      client: {
        // Disable the full-screen overlay (warnings in particular are too noisy in dev)
        overlay: false
      }
    }
  }
} satisfies UserConfigExport


