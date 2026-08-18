import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '驾驶舱' }
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理' }
      },
      { path: 'system/role', name: 'RoleManage', component: () => import('@/views/system/role/index.vue'), meta: { title: '角色管理' } },
      { path: 'system/menu', name: 'MenuManage', component: () => import('@/views/Placeholder.vue'), meta: { title: '菜单管理' } },
      { path: 'system/dict', name: 'DictManage', component: () => import('@/views/Placeholder.vue'), meta: { title: '字典管理' } },
      { path: 'system/log', name: 'LogManage', component: () => import('@/views/system/log/index.vue'), meta: { title: '操作日志' } },
      { path: 'archive/overview', name: 'ArchiveOverview', component: () => import('@/views/archive/overview/index.vue'), meta: { title: '档案浏览' } },
      { path: 'archive/fonds', name: 'FondsManage', component: () => import('@/views/archive/fonds/index.vue'), meta: { title: '全宗管理' } },
      { path: 'archive/type', name: 'TypeManage', component: () => import('@/views/archive/type/index.vue'), meta: { title: '门类管理' } },
      { path: 'archive/volume', name: 'VolumeManage', component: () => import('@/views/archive/volume/index.vue'), meta: { title: '案卷管理' } },
      { path: 'archive/file', name: 'FileManage', component: () => import('@/views/archive/file/index.vue'), meta: { title: '档案管理' } }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  },
  { path: '/:pathMatch(.*)*', redirect: '/404' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：登录校验 + 首次进入拉取用户信息/菜单
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const menuStore = useMenuStore()
  const token = userStore.token

  // 未登录：只能去登录页
  if (!token) {
    if (to.path === '/login') {
      next()
    } else {
      next('/login')
    }
    return
  }

  // 已登录访问登录页：跳首页
  if (to.path === '/login') {
    next('/')
    return
  }

  // 已登录但本地无用户信息（如刷新页面）：拉取用户信息 + 菜单树
  if (!userStore.userInfo.id) {
    try {
      const menus = await userStore.fetchMenus()
      menuStore.setMenus(menus)
      await userStore.fetchUserInfo()
      next({ ...to, replace: true })
    } catch (e) {
      userStore.reset()
      next('/login')
    }
    return
  }

  next()
})

export default router
