import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录', hideLayout: true },
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页', icon: 'Odometer' },
      },
      {
        path: 'fund',
        name: 'FundList',
        component: () => import('@/views/fund/FundList.vue'),
        meta: { title: '基金搜索', icon: 'Search' },
      },
      {
        path: 'fund/:fundCode',
        name: 'FundDetail',
        component: () => import('@/views/fund/FundDetail.vue'),
        meta: { title: '基金详情', hideMenu: true },
      },
      {
        path: 'compare',
        name: 'FundCompare',
        component: () => import('@/views/fund/FundCompare.vue'),
        meta: { title: '基金对比', icon: 'Histogram' },
      },
      {
        path: 'portfolio',
        name: 'Portfolio',
        component: () => import('@/views/portfolio/Portfolio.vue'),
        meta: { title: '我的组合', icon: 'Briefcase' },
      },
      {
        path: 'portfolio/:portfolioId/analysis',
        name: 'PortfolioAnalysis',
        component: () => import('@/views/portfolio/PortfolioAnalysis.vue'),
        meta: { title: '组合分析', hideMenu: true },
      },
      {
        path: 'advisor',
        name: 'Advisor',
        component: () => import('@/views/advisor/Advisor.vue'),
        meta: { title: 'AI 投顾', icon: 'ChatDotRound' },
      },
      {
        path: 'report/:type/:id',
        name: 'Report',
        component: () => import('@/views/report/ReportView.vue'),
        meta: { title: '分析报告', hideMenu: true },
      },
      {
        path: 'user',
        name: 'UserCenter',
        component: () => import('@/views/user/UserCenter.vue'),
        meta: { title: '用户中心', icon: 'User' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'FundPilot AI'} - FundPilot AI`
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
