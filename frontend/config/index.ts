import type { UserConfigExport } from '@tarojs/cli'

export default {
  projectName: 'mini-ecommerce-frontend',
  date: '2026-02-06',
  designWidth: 750,
  deviceRatio: {
    640: 1.17,
    750: 1,
    828: 0.905
  },
  sourceRoot: 'src',
  outputRoot: 'dist',
  plugins: ['@tarojs/plugin-platform-h5', '@tarojs/plugin-platform-weapp', '@tarojs/plugin-framework-react'],
  framework: 'react',
  compiler: {
    type: 'webpack5',
    prebundle: { enable: false }
  },
  mini: {},
  h5: {
    publicPath: '/',
    staticDirectory: 'static',
    webpackChain(chain) {
      // Suppress a known noisy upstream warning from @tarojs/components.
      // This keeps webpack-dev-server overlay clean and does not affect functionality.
      chain.merge({
        ignoreWarnings: [
          (warning) => {
            const msg = String(warning?.message ?? '')
            const moduleResource = String(warning?.module?.resource ?? '')
            return (
              moduleResource.includes('taro-video-core.js') &&
              msg.includes('webpackExports') &&
              msg.includes('dynamic import')
            )
          }
        ]
      })
    }
  }
} satisfies UserConfigExport


