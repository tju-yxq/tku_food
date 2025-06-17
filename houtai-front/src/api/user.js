import request from '@/utils/request'

/**
 * 管理员登录
 * @param {Object} data - 包含用户名和密码的登录数据
 * @returns {Promise} - 返回登录结果的Promise
 */
export function login(data) {
    return request({
        url: '/admin/login',
        method: 'post',
        data
    })
}

/**
 * 获取当前登录管理员信息
 * @returns {Promise} - 返回用户信息的Promise
 */
export function getInfo() {
    return request({
        url: '/admin/me',
        method: 'get'
    })
}

/**
 * 管理员登出
 * @param {String} token - 用户登录令牌
 * @returns {Promise} - 返回登出结果的Promise
 */
export function logout(token) {
    return request({
        url: '/admin/logout',
        method: 'post',
        headers: {
            'Authorization': token
        }
    })
}