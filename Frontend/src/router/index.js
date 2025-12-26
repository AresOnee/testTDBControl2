import { createRouter, createWebHistory } from 'vue-router'
import { usuarioService } from '@/services/usuarioService'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Rutas públicas
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresGuest: true } // Solo pueden acceder los no autenticados
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { requiresGuest: true } // Solo pueden acceder los no autenticados
    },

    // Rutas protegidas (requieren autenticación)
    {
      path: '/inicio',
      name: 'inicio',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: true } // Requiere autenticación
    },
    {
      path: '/tareas',
      name: 'tareas',
      component: () => import('@/views/TaskListView.vue'),
      meta: { requiresAuth: true } // Requiere autenticación
    },
    {
      path: '/crear-tarea',
      name: 'crear-tarea',
      component: () => import('@/views/CreateTaskView.vue'),
      meta: { requiresAuth: true } // Requiere autenticación
    },
    {
      path: '/editar-tarea/:id',
      name: 'editar-tarea',
      component: () => import('@/views/EditTaskView.vue'),
      meta: { requiresAuth: true },
      props: true
    },
    {
      path: '/estadisticas',
      name: 'estadisticas',
      component: () => import('@/views/StatsView.vue'),
      meta: { requiresAuth: true } // Requiere autenticación
    },
    { path: '/', redirect: '/login' } // Redirigir a login si no hay ruta definida
  ]
})

// Proteger las rutas con beforeEach
router.beforeEach((to, from, next) => {
  const isAuthenticated = usuarioService.isAuthenticated() // Usamos usuarioService para verificar la autenticación

  // Rutas que requieren autenticación
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' }) // Redirigir al login si no está autenticado
  }
  else if (to.meta.requiresGuest && isAuthenticated) {
    next({ name: 'inicio' }) // Redirigir a inicio si ya está autenticado
  } else {
    next() // Permitir acceso a la ruta
  }
})

export default router
