import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api',
  timeout: 5000
})

// === 请求拦截器 ===
service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      // 让每个请求都携带 'Authorization' 请求头
      config.headers['Authorization'] = getToken()
    }
    return config
  },
  error => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// === 响应拦截器 ===
service.interceptors.response.use(
  response => {
    const res = response.data

    // 【业务成功处理】
    // 如果后端返回的 success 字段为 true，说明操作成功，我们直接返回数据部分
    if (res.success) {
      return res.data
    } else {
      // 【业务失败处理】
      // 如果 success 字段为 false，说明是业务逻辑上的错误
      Message({
        message: res.errorMsg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.errorMsg || 'Error'))
    }
  },
  error => {
    // 【网络/HTTP错误处理】
    console.log('err' + error) // for debug
    
    // 处理401未授权错误
    if (error.response && error.response.status === 401) {
      // 清除token并重定向到登录页
      store.dispatch('user/resetToken').then(() => {
        location.reload()
      })
    }
    
    Message({
      message: error.message || '请求失败，请稍后再试',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service