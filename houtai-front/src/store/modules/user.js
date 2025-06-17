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
            token: '',
            name: '',
            avatar: 'https://i.imgur.com/kYoN8mC.png',
            roles: []
        });
        removeToken();
    }
};

const actions = {
    // login action
    login({ commit }, loginData) {
        return new Promise((resolve, reject) => {
            userService.login(loginData).then(response => {
                // 确保response是后端返回的AdminInfoVO对象
                if (!response || !response.token) {
                    return reject('登录失败，未能获取到Token。');
                }

                commit('SET_TOKEN', response.token);
                commit('SET_NAME', response.name || response.username);
                commit('SET_AVATAR', response.avatar || state.avatar);
                
                // 如果后端直接返回了角色信息，则立即设置
                if (response.roles && response.roles.length > 0) {
                    commit('SET_ROLES', response.roles);
                }
                
                setToken(response.token);
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
                return reject('getInfo: token is required');
            }

            userService.getUserInfo().then(response => {
                // 确保response是后端/me接口返回的数据
                if (!response) {
                    reject('验证失败，请重新登录。');
                    return;
                }

                const { roles, name, username, avatar } = response;

                // 验证返回的roles是否是非空数组
                if (!roles || roles.length <= 0) {
                    reject('getInfo: roles must be a non-null array!');
                    return;
                }

                commit('SET_ROLES', roles);
                commit('SET_NAME', name || username);
                commit('SET_AVATAR', avatar || state.avatar);

                resolve(response);
            }).catch(error => {
                reject(error);
            });
        });
    },

    // user logout
    logout({ commit, state }) {
        return new Promise((resolve, reject) => {
            userService.logout(state.token).then(() => {
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