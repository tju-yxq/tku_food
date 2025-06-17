import request from '@/utils/request'

export function login(data) {
    return request({
        url: '/admin/login',
        method: 'post',
        data
    })
}

export function getInfo() {
    return request({
        url: '/admin/me',
        method: 'get'
    })
}

export function logout(token) {
    return request({
        url: '/admin/logout',
        method: 'post',
        headers: {
            'Authorization': token
        }
    })
}