const getters = {
    sidebar: state => state.app.sidebar,
    device: state => state.app.device,
    token: state => state.user.token,
    avatar: state => state.user.avatar,
    name: state => state.user.name,
    roles: state => state.user.roles,
    permission_routes: state => state.permission.routes,
    // settings
    showSettings: state => state.settings.showSettings,
    fixedHeader: state => state.settings.fixedHeader,
    sidebarLogo: state => state.settings.sidebarLogo
}
export default getters
