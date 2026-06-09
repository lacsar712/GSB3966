import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: '/strategy',
        name: 'Strategy',
        component: () => import('@/views/strategy/index.vue'),
        meta: { title: '策略管理', icon: 'TrendCharts' }
      },
      {
        path: '/strategy/create',
        name: 'StrategyCreate',
        component: () => import('@/views/strategy/edit.vue'),
        meta: { title: '创建策略', hidden: true }
      },
      {
        path: '/strategy/edit/:id',
        name: 'StrategyEdit',
        component: () => import('@/views/strategy/edit.vue'),
        meta: { title: '编辑策略', hidden: true }
      },
      {
        path: '/strategy/detail/:id',
        name: 'StrategyDetail',
        component: () => import('@/views/strategy/detail.vue'),
        meta: { title: '策略详情', hidden: true }
      },
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', admin: true }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User', hidden: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.public) {
    next()
    return
  }

  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      userStore.logout()
      next('/login')
      return
    }
  }

  if (to.meta.admin && !userStore.isAdmin) {
    next('/')
    return
  }

  next()
})

export default router
