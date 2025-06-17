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
// 在这里，我们会为每一个发出去的请求都附上 Token
service.interceptors.request.use(
    config => {
        if (store.getters.token) {
            // 让每个请求都携带 'Authorization' 请求头，这是后端验证身份的凭据
            config.headers['Authorization'] = 'Bearer ' + getToken()
        }
        return config
    },
    error => {
        console.log(error) // for debug
        return Promise.reject(error)
    }
)

// === 响应拦截器 ===
// 在这里，我们会统一处理后端的响应，判断是成功还是失败
service.interceptors.response.use(
    response => {
        const res = response.data

        // 【业务成功处理】
        // 如果后端返回的 success 字段为 true，说明操作成功，我们直接返回数据部分
        if (res.success) {
            return res.data
        } else {
            // 【业务失败处理】
            // 如果 success 字段为 false，说明是业务逻辑上的错误（例如“用户名已存在”）
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
        // 这个代码块会处理所有非 2xx 的HTTP状态码，比如我们现在遇到的 401 (未授权) 或 500 (服务器内部错误)
        console.log('err' + error) // for debug
        Message({
            message: error.message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service