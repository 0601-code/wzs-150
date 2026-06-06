import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('../views/Appointment.vue')
      },
      {
        path: 'queue',
        name: 'Queue',
        component: () => import('../views/Queue.vue')
      },
      {
        path: 'home-service',
        name: 'HomeService',
        component: () => import('../views/HomeService.vue')
      },
      {
        path: 'tools',
        name: 'Tools',
        component: () => import('../views/Tools.vue')
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('../views/Report.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
