import Cookies from 'js-cookie'

// 使用一个更明确的Key来存储Token
const TokenKey = 'TjuFood-Admin-Token'

/**
 * 获取存储在Cookie中的Token
 * @returns {String} - 存储的Token
 */
export function getToken() {
    return Cookies.get(TokenKey)
}

/**
 * 将Token存储到Cookie中
 * @param {String} token - 要存储的Token
 * @returns {String|undefined} - 存储结果
 */
export function setToken(token) {
    // 确保我们存入的是一个字符串
    if (typeof token !== 'string') {
        console.error('setToken: token must be a string, but got', typeof token);
        return;
    }
    return Cookies.set(TokenKey, token)
}

/**
 * 从Cookie中移除Token
 * @returns {String|undefined} - 移除结果
 */
export function removeToken() {
    return Cookies.remove(TokenKey)
}