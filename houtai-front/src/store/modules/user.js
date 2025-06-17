import userService from '@/services/userService';
import { getToken, setToken, removeToken } from '@/utils/auth';
import { resetRouter } from '@/router';

const state = {
    token: getToken(),
    name: '',
    avatar: 'https://i.imgur.com/kYoN8mC.png',
    roles: []
};

const mutations = {
    SET_TOKEN: (state, token) => { state.token = token; },
    SET_NAME: (state, name) => { state.name = name; },
    SET_AVATAR: (state, avatar) => { state.avatar = avatar; },
    SET_ROLES: (state, roles) => { state.roles = roles; },
    RESET_STATE: (state) => {
        Object.assign(state, {
            token: getToken() || '', // 退出后也可能需要保留token，这里以清空为例
            name: '',
            avatar: 'https://i.imgur.com/kYoN8mC.png',
            roles: []
        });
        removeToken(); // 确保Cookie也被清除
    }
};

const actions = {
    // login action
    login({ commit }, loginData) {
        return new Promise((resolve, reject) => {
            userService.login(loginData).then(response => {
                // ★★★ 核心修正 #1: 确认 response 就是后端返回的 data 部分 (AdminInfoVO) ★★★
                const userInfo = response;
                if (!userInfo || !userInfo.token) {
                    return reject('登录失败，未能获取到Token。');
                }

                commit('SET_TOKEN', userInfo.token);
                commit('SET_NAME', userInfo.name || userInfo.username);
                commit('SET_AVATAR', userInfo.avatar || state.avatar);
                commit('SET_ROLES', userInfo.roles || []);
                setToken(userInfo.token);
                resolve();
            }).catch(error => {
                reject(error);
            });
        });
    },

    // get user info action
    getInfo({ commit, state }) {
        return new Promise((resolve, reject) => {
            // 确保我们有token再发请求
            if (!state.token) {
                return reject('getInfo: a token is required');
            }

            userService.getUserInfo().then(response => {
                // ★★★ 核心修正 #2: 确认 response 就是后端 /me 接口返回的 data ★★★
                const userInfo = response;

                if (!userInfo || !userInfo.roles || userInfo.roles.length <= 0) {
                    reject('验证失败，请重新登录。');
                }

                const { roles, name, username, avatar } = userInfo;

                // 使用 mutations 更新 state
                commit('SET_ROLES', roles);
                commit('SET_NAME', name || username);
                commit('SET_AVATAR', avatar || state.avatar);

                // ★★★ 核心修正 #3: resolve 的数据结构要和 permission.js 期望的一致 ★★★
                resolve(userInfo); // 直接将包含roles的用户信息对象传递出去
            }).catch(error => {
                reject(error);
            });
        });
    },

    // user logout
    logout({ commit }) {
        return new Promise((resolve, reject) => {
            userService.logout().then(() => {
                commit('RESET_STATE');
                resetRouter();
                resolve();
            }).catch(error => {
                // 即使后端退出失败，前端也应强制清除状态
                commit('RESET_STATE');
                resetRouter();
                reject(error);
            });
        });
    },

    // remove token
    resetToken({ commit }) {
        return new Promise(resolve => {
            commit('RESET_STATE');
            resolve();
        });
    }
};

export default {
    namespaced: true,
    state,
    mutations,
    actions
};