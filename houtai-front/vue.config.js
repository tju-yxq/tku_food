const { defineConfig } = require('@vue/cli-service');
const path = require('path');

// 判断是否为生产环境
const IS_PROD = process.env.NODE_ENV === 'production';
// 生产环境的 CDN 地址 (请根据您的实际情况替换)
const CDN_URL = 'https://cdn.example.com/tju-food-admin/';

module.exports = defineConfig({
  // publicPath 决定了应用被部署在哪个根路径。
  // 在生产环境中，我们将其设置为您的 CDN 地址；开发环境则使用根路径。
  publicPath: IS_PROD ? CDN_URL : '/',

  // 此选项用于显式转译某个依赖包
  transpileDependencies: true,

  // 开发服务器配置
  devServer: {
    port: 8082, // 设置前端开发服务器端口为8082
    proxy: {
      // 代理所有以 /api 开头的请求
      '/api': {
        target: 'http://localhost:8090', // 代理的目标后端服务地址
        changeOrigin: true, // 设置为 true 表示允许跨域
        // pathRewrite: { '^/api': '' } // 可选：如果后端接口本身不包含 /api 前缀，可以取消此行注释来重写路径
      }
    }
  },

  // 通过 chainWebpack 链式操作来修改 Webpack 配置
  chainWebpack: config => {
    // 设置路径别名，例如 @ 指向 src 目录
    config.resolve.alias
        .set('@', path.join(__dirname, 'src'));
  },

  // 通过 configureWebpack 直接修改或新增 Webpack 配置
  configureWebpack: {
    resolve: {
      // 为 'path' 模块提供一个浏览器端的 polyfill (回退方案)
      fallback: {
        "path": require.resolve("path-browserify")
      }
    }
  }
});