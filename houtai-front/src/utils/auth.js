import Cookies from 'js-cookie'

// 使用一个更明确的Key来存储Token
const TokenKey = 'TjuFood-Admin-Token'

export function getToken() {
    return Cookies.get(TokenKey)
}

export function setToken(token) {
    // 确保我们存入的是一个字符串
    if (typeof token !== 'string') {
        console.error('setToken: token must be a string, but got', typeof token);
        return;
    }
    return Cookies.set(TokenKey, token)
}

export function removeToken() {
    return Cookies.remove(TokenKey)
}