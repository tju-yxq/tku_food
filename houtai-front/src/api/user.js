// 文件路径: yxq/houtai-front/src/api/user.js (修改)
import request from '@/utils/request'

export function login(data) {
    // 修正URL路径
    return request({
        url: '/admin/login',
        method: 'post',
        data
    })
}

export function getInfo() {
    // 修正URL路径，指向新增的 /api/admin/me 接口
    return request({
        url: '/admin/me',
        method: 'get'
    })
}

export function logout(token) {
    // 修正URL路径，并传递token
    return request({
        url: '/admin/logout',
        method: 'post',
        headers: {
            'authorization': token
        }
    })
}