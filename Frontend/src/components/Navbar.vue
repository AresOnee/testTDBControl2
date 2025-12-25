<template>
  <v-app-bar color="primary" density="comfortable" flat app>
    <v-app-bar-title class="font-weight-bold">Gestión TBD</v-app-bar-title>

    <div class="d-flex align-center gap-2">
      <v-btn size="small" color="white" variant="text" @click="goHome">Inicio</v-btn>
      <v-btn size="small" color="white" variant="text" @click="goTasks">Mis Tareas</v-btn>
      <v-btn size="small" color="white" variant="text" @click="goCreateTask">Nueva Tarea</v-btn>
      <v-btn size="small" color="white" variant="text" @click="goStats">Estadísticas</v-btn>
      <v-btn size="small" color="white" variant="text" @click="goBack">Atrás</v-btn>
    </div>

    <v-spacer />

    <div class="d-flex align-center">
      <!-- Icono de Notificaciones -->
      <v-menu
        v-model="mostrarNotificaciones"
        :close-on-content-click="false"
        location="bottom end"
        max-width="400"
        @update:model-value="onMenuToggle"
      >
        <template v-slot:activator="{ props }">
          <v-btn
            v-bind="props"
            icon
            variant="text"
            color="white"
            class="mr-2"
          >
            <v-badge
              v-if="totalNotificaciones > 0"
              :content="totalNotificaciones"
              color="error"
              floating
            >
              <v-icon>mdi-bell</v-icon>
            </v-badge>
            <v-icon v-else>mdi-bell-outline</v-icon>
          </v-btn>
        </template>

        <v-card min-width="350">
          <v-card-title class="d-flex justify-space-between align-center py-2">
            <span class="text-body-1">Notificaciones de Vencimiento</span>
            <v-chip v-if="totalNotificaciones > 0" size="x-small" color="error">
              {{ totalNotificaciones }}
            </v-chip>
          </v-card-title>

          <v-divider></v-divider>

          <!-- Loading state -->
          <v-card-text v-if="cargandoNotificaciones" class="text-center py-4">
            <v-progress-circular indeterminate color="primary" size="24"></v-progress-circular>
            <div class="mt-2">Verificando tareas...</div>
          </v-card-text>

          <v-list v-else-if="notificaciones.length > 0" density="compact" max-height="300" class="overflow-y-auto">
            <v-list-item
              v-for="notif in notificaciones"
              :key="notif.id"
              :class="getNotifClass(notif.tipo)"
              class="py-2"
            >
              <template v-slot:prepend>
                <v-icon :color="getNotifColor(notif.tipo)" size="small">
                  {{ getNotifIcon(notif.tipo) }}
                </v-icon>
              </template>

              <v-list-item-title class="text-body-2 font-weight-medium">
                {{ notif.titulo }}
              </v-list-item-title>
              <v-list-item-subtitle class="text-caption">
                {{ notif.mensaje }}
              </v-list-item-subtitle>

              <template v-slot:append>
                <v-btn
                  icon
                  variant="text"
                  size="x-small"
                  @click="cerrarNotificacion(notif.id)"
                >
                  <v-icon size="small">mdi-close</v-icon>
                </v-btn>
              </template>
            </v-list-item>
          </v-list>

          <v-card-text v-else class="text-center text-grey py-4">
            <v-icon size="large" color="grey-lighten-1" class="mb-2">mdi-bell-check</v-icon>
            <div>No hay tareas próximas a vencer</div>
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions class="py-2">
            <v-btn variant="text" size="small" color="primary" @click="irATareas" block>
              Ver todas las tareas
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-menu>

      <span class="text-white" style="margin-right:12px">{{ nombreUsuario }}</span>
      <v-btn color="white" variant="outlined" size="small" @click="logout">Cerrar Sesión</v-btn>
    </div>
  </v-app-bar>

  <!-- Snackbar para notificaciones emergentes -->
  <v-snackbar
    v-model="mostrarSnackbar"
    :timeout="10000"
    :color="snackbarColor"
    location="top right"
    multi-line
  >
    <div class="d-flex align-center">
      <v-icon class="mr-2">{{ snackbarIcon }}</v-icon>
      <div>
        <strong>{{ snackbarTitulo }}</strong>
        <div class="text-caption">{{ snackbarMensaje }}</div>
      </div>
    </div>
    <template v-slot:actions>
      <v-btn variant="text" size="small" @click="mostrarSnackbar = false">
        Cerrar
      </v-btn>
      <v-btn variant="text" size="small" @click="irATareas">
        Ver Tareas
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { usuarioService } from '../services/usuarioService';
import { tareaService } from '../services/tareaService';

const router = useRouter();
const route = useRoute();
const nombreUsuario = ref(localStorage.getItem('usuarioNombre') || 'Usuario');

// Variables para notificaciones
const notificaciones = ref([]);
const notificacionesCerradas = ref(new Set());
const mostrarNotificaciones = ref(false);
const cargandoNotificaciones = ref(false);
const mostrarSnackbar = ref(false);
const snackbarTitulo = ref('');
const snackbarMensaje = ref('');
const snackbarColor = ref('warning');
const snackbarIcon = ref('mdi-alert');

let intervaloVerificacion = null;
let notificacionesMostradas = new Set();

// Actualizar nombre de usuario
nombreUsuario.value = localStorage.getItem('usuarioNombre') || 'Usuario';

const totalNotificaciones = computed(() => notificaciones.value.length);

// Navegación
const goHome = () => router.push('/tareas');
const goTasks = () => router.push('/tareas');
const goCreateTask = () => router.push('/crear-tarea');
const goStats = () => router.push('/estadisticas');
const goBack = () => router.back();

const irATareas = () => {
  mostrarNotificaciones.value = false;
  mostrarSnackbar.value = false;
  router.push('/tareas');
};

const logout = () => {
  usuarioService.logout();
  nombreUsuario.value = '';
  notificaciones.value = [];
  if (intervaloVerificacion) {
    clearInterval(intervaloVerificacion);
  }
  router.replace('/login');
};

// Cuando se abre/cierra el menú de notificaciones
const onMenuToggle = (isOpen) => {
  if (isOpen) {
    verificarTareas();
  }
};

// Verificar tareas y generar notificaciones
const verificarTareas = async () => {
  const token = localStorage.getItem('token');
  if (!token) {
    console.log('No hay token, no se verifican notificaciones');
    return;
  }

  cargandoNotificaciones.value = true;

  try {
    const tareas = await tareaService.getTareasByUsuario();
    console.log('Tareas obtenidas para notificaciones:', tareas);

    const ahora = new Date();
    const nuevasNotificaciones = [];

    if (Array.isArray(tareas)) {
      tareas.forEach(tarea => {
        // Solo verificar tareas pendientes con fecha de vencimiento
        if (tarea.estado !== 'PENDIENTE' || !tarea.fechaVencimiento) return;

        // Usar idTarea o id según lo que venga del backend
        const tareaId = tarea.idTarea || tarea.id;

        // Si ya se cerró esta notificación, no mostrarla
        if (notificacionesCerradas.value.has(tareaId)) return;

        const fechaVencimiento = new Date(tarea.fechaVencimiento);
        const diferenciaHoras = (fechaVencimiento - ahora) / (1000 * 60 * 60);

        let notif = null;

        if (diferenciaHoras < 0) {
          // Tarea vencida
          notif = {
            id: tareaId,
            titulo: tarea.titulo,
            mensaje: `Tarea vencida desde ${formatearFecha(tarea.fechaVencimiento)}`,
            tipo: 'error'
          };
        } else if (diferenciaHoras <= 24) {
          // Vence en menos de 24 horas
          notif = {
            id: tareaId,
            titulo: tarea.titulo,
            mensaje: `Vence en menos de 24 horas`,
            tipo: 'warning'
          };
        } else if (diferenciaHoras <= 48) {
          // Vence en menos de 48 horas
          notif = {
            id: tareaId,
            titulo: tarea.titulo,
            mensaje: `Vence el ${formatearFecha(tarea.fechaVencimiento)}`,
            tipo: 'info'
          };
        }

        if (notif) {
          nuevasNotificaciones.push(notif);

          // Mostrar snackbar solo para nuevas notificaciones urgentes (primera vez)
          if (!notificacionesMostradas.has(notif.id) && (notif.tipo === 'error' || notif.tipo === 'warning')) {
            mostrarSnackbarNotificacion(notif);
            notificacionesMostradas.add(notif.id);
          }
        }
      });
    }

    console.log('Notificaciones generadas:', nuevasNotificaciones);
    notificaciones.value = nuevasNotificaciones;
  } catch (error) {
    console.error('Error verificando notificaciones:', error);
  } finally {
    cargandoNotificaciones.value = false;
  }
};

// Mostrar notificación en snackbar
const mostrarSnackbarNotificacion = (notif) => {
  snackbarTitulo.value = notif.titulo;
  snackbarMensaje.value = notif.mensaje;
  snackbarColor.value = notif.tipo === 'error' ? 'error' : 'warning';
  snackbarIcon.value = notif.tipo === 'error' ? 'mdi-alert-circle' : 'mdi-alert';
  mostrarSnackbar.value = true;
};

// Cerrar una notificación
const cerrarNotificacion = (id) => {
  notificacionesCerradas.value.add(id);
  notificaciones.value = notificaciones.value.filter(n => n.id !== id);
};

// Obtener color según tipo
const getNotifColor = (tipo) => {
  switch (tipo) {
    case 'error': return 'error';
    case 'warning': return 'warning';
    default: return 'info';
  }
};

// Obtener icono según tipo
const getNotifIcon = (tipo) => {
  switch (tipo) {
    case 'error': return 'mdi-alert-circle';
    case 'warning': return 'mdi-alert';
    default: return 'mdi-information';
  }
};

// Obtener clase CSS según tipo
const getNotifClass = (tipo) => {
  switch (tipo) {
    case 'error': return 'notif-error';
    case 'warning': return 'notif-warning';
    default: return 'notif-info';
  }
};

// Formatear fecha
const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  return new Date(fecha).toLocaleDateString() + ' ' + new Date(fecha).toLocaleTimeString().slice(0, 5);
};

// Observar cambios de ruta para recargar notificaciones
watch(() => route.path, () => {
  if (localStorage.getItem('token')) {
    verificarTareas();
  }
});

onMounted(() => {
  // Pequeño delay para asegurar que el token esté disponible
  setTimeout(() => {
    if (localStorage.getItem('token')) {
      verificarTareas();
      // Verificar cada 2 minutos
      intervaloVerificacion = setInterval(verificarTareas, 2 * 60 * 1000);
    }
  }, 500);
});

onUnmounted(() => {
  if (intervaloVerificacion) {
    clearInterval(intervaloVerificacion);
  }
});
</script>

<style scoped>
.notif-error {
  border-left: 3px solid #D32F2F;
}

.notif-warning {
  border-left: 3px solid #F9A825;
}

.notif-info {
  border-left: 3px solid #0288D1;
}
</style>
