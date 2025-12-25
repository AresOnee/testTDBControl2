<template>
  <div class="notificaciones-container">
    <!-- Snackbar para notificaciones emergentes -->
    <v-snackbar
      v-model="mostrarSnackbar"
      :timeout="8000"
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
        <v-btn variant="text" @click="mostrarSnackbar = false">
          Cerrar
        </v-btn>
        <v-btn variant="text" @click="irATareas">
          Ver Tareas
        </v-btn>
      </template>
    </v-snackbar>

    <!-- Badge de notificaciones en el navbar (opcional) -->
    <v-badge
      v-if="totalNotificaciones > 0"
      :content="totalNotificaciones"
      color="error"
      floating
      class="notificacion-badge"
    >
      <v-btn
        icon
        variant="text"
        @click="mostrarPanel = !mostrarPanel"
        class="mr-2"
      >
        <v-icon>mdi-bell</v-icon>
      </v-btn>
    </v-badge>

    <!-- Panel desplegable de notificaciones -->
    <v-menu
      v-model="mostrarPanel"
      :close-on-content-click="false"
      location="bottom end"
      max-width="400"
    >
      <template v-slot:activator="{ props }">
        <div v-bind="props"></div>
      </template>

      <v-card min-width="350">
        <v-card-title class="d-flex justify-space-between align-center">
          <span>Notificaciones</span>
          <v-chip size="small" color="primary">{{ totalNotificaciones }}</v-chip>
        </v-card-title>

        <v-divider></v-divider>

        <v-list v-if="notificaciones.length > 0" density="compact" max-height="300" class="overflow-y-auto">
          <v-list-item
            v-for="notif in notificaciones"
            :key="notif.id"
            :class="getNotifClass(notif.tipo)"
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

        <v-card-text v-else class="text-center text-grey">
          No hay notificaciones pendientes
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn variant="text" size="small" @click="irATareas" block>
            Ver todas las tareas
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-menu>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { tareaService } from '../services/tareaService';

const router = useRouter();
const route = useRoute();

const notificaciones = ref([]);
const notificacionesCerradas = ref(new Set());
const mostrarSnackbar = ref(false);
const mostrarPanel = ref(false);
const snackbarTitulo = ref('');
const snackbarMensaje = ref('');
const snackbarColor = ref('warning');
const snackbarIcon = ref('mdi-alert');

let intervaloVerificacion = null;
let notificacionesMostradas = new Set();

const totalNotificaciones = computed(() => notificaciones.value.length);

// Verificar tareas y generar notificaciones
const verificarTareas = async () => {
  // Solo verificar si el usuario está autenticado
  const token = localStorage.getItem('token');
  if (!token) return;

  try {
    const tareas = await tareaService.getTareasByUsuario();
    const ahora = new Date();
    const nuevasNotificaciones = [];

    tareas.forEach(tarea => {
      if (tarea.estado !== 'PENDIENTE' || !tarea.fechaVencimiento) return;
      if (notificacionesCerradas.value.has(tarea.idTarea)) return;

      const fechaVencimiento = new Date(tarea.fechaVencimiento);
      const diferenciaHoras = (fechaVencimiento - ahora) / (1000 * 60 * 60);

      let notif = null;

      if (diferenciaHoras < 0) {
        notif = {
          id: tarea.idTarea,
          titulo: tarea.titulo,
          mensaje: `Tarea vencida desde ${formatearFecha(tarea.fechaVencimiento)}`,
          tipo: 'error'
        };
      } else if (diferenciaHoras <= 24) {
        notif = {
          id: tarea.idTarea,
          titulo: tarea.titulo,
          mensaje: `Vence en menos de 24 horas`,
          tipo: 'warning'
        };
      } else if (diferenciaHoras <= 48) {
        notif = {
          id: tarea.idTarea,
          titulo: tarea.titulo,
          mensaje: `Vence el ${formatearFecha(tarea.fechaVencimiento)}`,
          tipo: 'info'
        };
      }

      if (notif) {
        nuevasNotificaciones.push(notif);

        // Mostrar snackbar solo para nuevas notificaciones urgentes
        if (!notificacionesMostradas.has(notif.id) && (notif.tipo === 'error' || notif.tipo === 'warning')) {
          mostrarSnackbarNotificacion(notif);
          notificacionesMostradas.add(notif.id);
        }
      }
    });

    notificaciones.value = nuevasNotificaciones;
  } catch (error) {
    console.error('Error verificando notificaciones:', error);
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

// Ir a la página de tareas
const irATareas = () => {
  mostrarPanel.value = false;
  mostrarSnackbar.value = false;
  router.push('/tareas');
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
  verificarTareas();
});

onMounted(() => {
  verificarTareas();
  // Verificar cada 5 minutos
  intervaloVerificacion = setInterval(verificarTareas, 5 * 60 * 1000);
});

onUnmounted(() => {
  if (intervaloVerificacion) {
    clearInterval(intervaloVerificacion);
  }
});

// Exponer funciones para uso externo
defineExpose({
  verificarTareas,
  totalNotificaciones
});
</script>

<style scoped>
.notificaciones-container {
  display: inline-flex;
  align-items: center;
}

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
