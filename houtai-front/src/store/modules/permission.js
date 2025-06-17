import { asyncRoutes, constantRoutes } from '@/router'

/**
 * 检查当前用户角色是否满足路由权限要求
 * @param {string[]} roles - 用户拥有的角色列表
 * @param {object} route - 需要检查的路由对象
 */
function hasPermission(roles, route) {
    // 我们添加的调试日志，用于在浏览器控制台观察匹配过程
    console.log(`[权限检查] 路由: ${route.path}, 需要角色: ${route.meta?.roles}, 用户拥有角色: ${roles}`);

    if (route.meta && route.meta.roles) {
        // some() 方法测试数组中是不是至少有1个元素通过了被提供的函数。
        // 这里检查用户角色列表(roles)中，是否至少有一个角色，被包含在路由所需的角色列表(route.meta.roles)中。
        const hasAccess = roles.some(role => route.meta.roles.includes(role))

        // 打印出检查结果
        console.log(`[权限检查] -> 结果: ${hasAccess ? '通过' : '拒绝'}`);

        return hasAccess
    } else {
        // 如果路由没有定义 meta.roles，则默认所有人都可以访问
        return true
    }
}

/**
 * 通过递归，过滤出当前用户可访问的异步路由
 * @param {object[]} routes - 所有异步路由 (asyncRoutes)
 * @param {string[]} roles - 用户拥有的角色列表
 */
export function filterAsyncRoutes(routes, roles) {
    const res = []

    routes.forEach(route => {
        const tmp = { ...route }
        // 检查用户是否有权访问该路由
        if (hasPermission(roles, tmp)) {
            // 如果有子路由，则递归过滤子路由
            if (tmp.children) {
                tmp.children = filterAsyncRoutes(tmp.children, roles)
            }
            res.push(tmp)
        }
    })

    return res
}

const state = {
    routes: [],
    addRoutes: []
}

const mutations = {
    SET_ROUTES: (state, routes) => {
        state.addRoutes = routes
        state.routes = constantRoutes.concat(routes) // 组合静态路由和动态路由
    }
}

const actions = {
    generateRoutes({ commit }, roles) {
        return new Promise(resolve => {
            let accessedRoutes
            // 如果用户角色包含 '系统管理员'，则直接授予所有异步路由的权限
            if (roles.includes('系统管理员')) {
                accessedRoutes = asyncRoutes || []
            } else {
                // 否则，根据角色过滤路由
                accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
            }
            commit('SET_ROUTES', accessedRoutes)
            resolve(accessedRoutes)
        })
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}