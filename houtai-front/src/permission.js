import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // 进度条
import 'nprogress/nprogress.css' // 进度条样式
/* eslint-disable no-unused-vars */


NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/login'] // 无需重定向的白名单

router.beforeEach(async(to, from, next) => {
    NProgress.start()

    const hasToken = store.getters.token

    if (hasToken) {
        if (to.path === '/login') {
            // 如果已登录，重定向到首页
            next({ path: '/' })
            NProgress.done()
        } else {
            // ★★★ 核心修正：判断当前用户是否已通过 getInfo 获取了他的角色信息 ★★★
            const hasRoles = store.getters.roles && store.getters.roles.length > 0

            if (hasRoles) {
                // 如果Vuex中已有角色信息，说明动态路由已经生成，直接放行
                next()
            } else {
                try {
                    // 如果Vuex中没有角色信息，则调用getInfo获取
                    const userInfo = await store.dispatch('user/getInfo')
                    const roles = userInfo.roles // 从返回的用户信息中获取角色

                    // 根据角色生成可访问的路由表
                    const accessRoutes = await store.dispatch('permission/generateRoutes', roles)

                    // 动态添加可访问的路由
                    router.addRoutes(accessRoutes)

                    // hack方法 确保addRoutes已完成
                    // set the replace: true, so the navigation will not leave a history record
                    next({ ...to, replace: true })
                } catch (error) {
                    // 如果获取用户信息失败（例如token过期），则重置token并跳转到登录页
                    await store.dispatch('user/resetToken')
                    Message.error(error.message || '获取用户信息失败，请重新登录')
                    next(`/login?redirect=${to.path}`)
                    NProgress.done()
                }
            }
        }
    } else {
        /* has no token*/
        if (whiteList.indexOf(to.path) !== -1) {
            // 在免登录白名单中，直接进入
            next()
        } else {
            // 其他没有访问权限的页面将被重定向到登录页面
            next(`/login?redirect=${to.path}`)
            NProgress.done()
        }
    }
})

router.afterEach(() => {
    NProgress.done()
})