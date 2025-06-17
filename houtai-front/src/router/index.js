import Vue from 'vue'
import VueRouter from 'vue-router'

const Layout = () => import('@/views/layout/index.vue');

// 导入所有页面级组件
const LoginView = () => import('@/views/login/LoginView.vue');
const DashboardView = () => import('@/views/dashboard/DashboardView.vue');
const NotFoundView = () => import('@/views/NotFoundView.vue');
const PersonnelManagementView = () => import('@/views/system_mgr/PersonnelManagementView.vue');
const RoleManagementView = () => import('@/views/system_mgr/RoleManagementView.vue');
const CampusManagementView = () => import('@/views/content_mgr/CampusView.vue');
const CanteenManagementView = () => import('@/views/content_mgr/CanteenView.vue');
const CanteenTypeManagementView = () => import('@/views/content_mgr/CanteenTypeView.vue');
const StallManagementView = () => import('@/views/content_mgr/StallView.vue');
const StallTypeManagementView = () => import('@/views/content_mgr/StallTypeView.vue');
const DishManagementView = () => import('@/views/content_mgr/DishView.vue');
const ReportManagementView = () => import('@/views/community_mgr/ReportManagementView.vue');
const BlogManagementView = () => import('@/views/community_mgr/BlogManagementView.vue');
const SensitiveWordsManagementView = () => import('@/views/community_mgr/SensitiveWordsManagementView.vue');
const UserManagementView = () => import('@/views/community_mgr/UserManagementView.vue');
const NoticeManagementView = () => import('@/views/operation_mgr/NoticeManagementView.vue');
const LogManagementView = () => import('@/views/operation_mgr/LogManagementView.vue');
const BannerManagementView = () => import('@/views/operation_mgr/BannerManagementView.vue');
const IncentiveManagementView = () => import('@/views/operation_mgr/IncentiveManagementView.vue');
const HelpDocView = () => import('@/views/toolbox/HelpDocView.vue');
const ExportLogView = () => import('@/views/toolbox/ExportLogView.vue');
const ExportDbView = () => import('@/views/toolbox/ExportDbView.vue');
const StatisticsDashboardView = () => import('@/views/toolbox/StatisticsDashboardView.vue');
const ApiDocView = () => import('@/views/toolbox/ApiDocView.vue');
const TaskManagementView = () => import('@/views/toolbox/TaskManagementView.vue');

Vue.use(VueRouter)

export const constantRoutes = [
  {
    path: '/login',
    component: LoginView,
    hidden: true
  },
  {
    path: '/404',
    component: NotFoundView,
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: DashboardView,
      meta: { title: '首页', icon: 'el-icon-s-home' }
    }]
  }
];

export const asyncRoutes = [
  {
    path: '/content-mgr',
    component: Layout,
    redirect: '/content-mgr/campus',
    name: 'ContentManagement',
    meta: { title: '内容管理', icon: 'el-icon-folder-opened', roles: ['内容管理员', '系统管理员'] },
    children: [
      { path: 'campus', name: 'CampusManagement', component: CampusManagementView, meta: { title: '校区管理' } },
      { path: 'canteen-type', name: 'CanteenTypeManagement', component: CanteenTypeManagementView, meta: { title: '食堂类型管理' } },
      { path: 'canteen', name: 'CanteenManagement', component: CanteenManagementView, meta: { title: '食堂管理' } },
      { path: 'stall-type', name: 'StallTypeManagement', component: StallTypeManagementView, meta: { title: '窗口类型管理' } },
      { path: 'stall', name: 'StallManagement', component: StallManagementView, meta: { title: '窗口管理' } },
      { path: 'dish', name: 'DishManagement', component: DishManagementView, meta: { title: '菜品管理' } },
    ]
  },
  {
    path: '/community-mgr',
    component: Layout,
    redirect: '/community-mgr/blog',
    name: 'CommunityManagement',
    meta: { title: '社区管理', icon: 'el-icon-chat-dot-round', roles: ['社区管理员', '系统管理员'] },
    children: [
      { path: 'report', name: 'ReportManagement', component: ReportManagementView, meta: { title: '用户举报管理' } },
      { path: 'blog', name: 'BlogManagement', component: BlogManagementView, meta: { title: '博客发表管理' } },
      { path: 'sensitive-words', name: 'SensitiveWordsManagement', component: SensitiveWordsManagementView, meta: { title: '敏感词管理' } },
      { path: 'user', name: 'UserManagement', component: UserManagementView, meta: { title: '普通用户管理' } },
    ]
  },
  {
    path: '/operation-mgr',
    component: Layout,
    redirect: '/operation-mgr/notice',
    name: 'OperationManagement',
    meta: { title: '运营管理', icon: 'el-icon-s-marketing', roles: ['运营管理员', '系统管理员'] },
    children: [
      { path: 'notice', name: 'NoticeManagement', component: NoticeManagementView, meta: { title: '公告管理' } },
      { path: 'banner', name: 'BannerManagement', component: BannerManagementView, meta: { title: '轮播图管理' } },
      { path: 'incentive', name: 'IncentiveManagement', component: IncentiveManagementView, meta: { title: '激励管理' } },
      { path: 'log', name: 'LogManagement', component: LogManagementView, meta: { title: '日志管理' } },
    ]
  },
  {
    path: '/system-mgr',
    component: Layout,
    redirect: '/system-mgr/personnel',
    name: 'SystemManagement',
    meta: { title: '平台设置', icon: 'el-icon-s-tools', roles: ['系统管理员'] },
    children: [
      { path: 'personnel', name: 'PersonnelManagement', component: PersonnelManagementView, meta: { title: '人员管理' } },
      { path: 'role', name: 'RoleManagement', component: RoleManagementView, meta: { title: '角色管理' } },
    ]
  },
  {
    path: '/toolbox',
    component: Layout,
    redirect: '/toolbox/help-doc',
    name: 'Toolbox',
    meta: { title: '工具箱', icon: 'el-icon-s-cooperation', roles: ['系统管理员'] },
    children: [
      { path: 'help-doc', name: 'HelpDoc', component: HelpDocView, meta: { title: '在线帮助文档' } },
      { path: 'export-log', name: 'ExportLog', component: ExportLogView, meta: { title: '操作日志导出' } },
      { path: 'export-db', name: 'ExportDb', component: ExportDbView, meta: { title: '数据库数据导出' } },
      { path: 'statistics', name: 'StatisticsDashboard', component: StatisticsDashboardView, meta: { title: '数据大屏' } },
      { path: 'api-doc', name: 'ApiDoc', component: ApiDocView, meta: { title: 'API文档' } },
      { path: 'api-management', name: 'ApiManagement', component: () => import('@/views/toolbox/ApiManagementView.vue'), meta: { title: 'API管理' } },
      { path: 'audit-log', name: 'AuditLog', component: () => import('@/views/toolbox/AuditLogView.vue'), meta: { title: '审计日志' } },
      { path: 'task', name: 'TaskManagement', component: TaskManagementView, meta: { title: '定时任务管理' } },
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
];

const createRouter = () => new VueRouter({
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router