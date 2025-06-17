import * as userApi from '@/api/user';
import { setToken, removeToken, getToken } from '@/utils/auth';

class UserService {
    /**
     * 用户登录业务
     */
    async login(loginData) {
        try {
            // 调用API
            const response = await userApi.login(loginData);
            return response;
        } catch (error) {
            console.error('登录失败:', error);
            return Promise.reject(error);
        }
    }

    /**
     * 获取当前登录管理员的信息
     */
    async getUserInfo() {
        try {
            // 调用API
            const response = await userApi.getInfo();
            return response;
        } catch (error) {
            console.error('获取用户信息失败:', error);
            return Promise.reject(error);
        }
    }

    /**
     * 处理用户登出业务
     */
    async logout() {
        try {
            const token = getToken();
            if (token) {
                // 调用后端的登出接口
                await userApi.logout(token);
            }
            removeToken();
            return Promise.resolve();
        } catch (error) {
            console.error('登出失败:', error);
            // 即使API调用失败，也要清除本地token
            removeToken();
            return Promise.reject(error);
        }
    }
}

export default new UserService();