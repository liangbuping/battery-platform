import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard/index.vue'),
    meta: {
      title: '实时监控大屏',
      icon: 'DataLine'
    }
  },
  {
    path: '/alert',
    name: 'AlertCenter',
    component: () => import('@/views/AlertCenter/index.vue'),
    meta: {
      title: '预警中心',
      icon: 'Warning'
    }
  },
  {
    path: '/maintenance',
    name: 'Maintenance',
    component: () => import('@/views/Maintenance/index.vue'),
    meta: {
      title: '维保管理',
      icon: 'Tools'
    }
  },
  {
    path: '/mobile/alert',
    name: 'MobileAlert',
    component: () => import('@/views/Mobile/Alert.vue'),
    meta: {
      title: '移动告警',
      isMobile: true
    }
  },
  {
    path: '/mobile/workorder',
    name: 'MobileWorkOrder',
    component: () => import('@/views/Mobile/WorkOrder.vue'),
    meta: {
      title: '移动工单',
      isMobile: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 电池安全管理平台` : '电池安全管理平台'
  next()
})

export default router
