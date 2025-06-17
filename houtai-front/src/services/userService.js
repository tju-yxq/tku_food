// 文件路径: yxq/houtai-front/src/services/userService.js (修改)
import * as userApi from '@/api/user'; // 引入真实的API
import { setToken, removeToken, getToken } from '@/utils/auth';

class UserService {
    /**
     * 用户登录业务
     */
    async login(loginData) {
        // 直接调用API
        const response = await userApi.login(loginData);
        // 后端成功时返回 AdminInfoVO 对象，包含token
        if (response && response.token) {
            setToken(response.token);
            // 返回整个用户信息对象，以便存入store
            return Promise.resolve(response);
        } else {
            return Promise.reject(new Error('登录失败，未收到Token'));
        }
    }

    /**
     * 获取当前登录管理员的信息
     */
    async getUserInfo() {
        // 直接调用API
        return userApi.getInfo();
    }

    /**
     * 处理用户登出业务
     */
    async logout() {
        const token = getToken();
        if (token) {
            // 调用后端的登出接口
            await userApi.logout(token);
        }
        removeToken();
        return Promise.resolve();
    }

    // register 方法可以保持不变或按需实现
    async register(registerData) {
        console.log('模拟注册:', registerData);
        return Promise.resolve();
    }
}

export default new UserService();