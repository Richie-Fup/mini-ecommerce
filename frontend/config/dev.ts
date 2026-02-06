import type { UserConfigExport } from '@tarojs/cli'

export default {
  env: {
    NODE_ENV: '"development"'
  },
  defineConstants: {},
  mini: {},
  h5: {
    devServer: {
      host: 'localhost',
      port: 10086,
      client: {
        overlay: false
      }
    },
    webpackChain(chain) {
      // Force dev server to only listen on localhost
      chain.devServer.host('localhost')
    }
  }
} satisfies UserConfigExport


