import request from '@/utils/request'

export function login(data) {
    // 修正URL路径，确保与后端接口一致
    return request({
        url: '/admin/login',
        method: 'post',
        data
    })
}

export function getInfo() {
    // 修正URL路径，指向正确的管理员信息接口
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