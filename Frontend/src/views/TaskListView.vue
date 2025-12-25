<template>
  <v-container class="py-6">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="mb-0">Mis Tareas</h2>
      <div>
        <router-link to="/estadisticas" class="me-2">
          <v-btn variant="outlined" color="info">Ver Estadísticas Geo</v-btn>
        </router-link>
        <router-link to="/crear-tarea">
          <v-btn color="success">Nueva Tarea</v-btn>
        </router-link>
      </div>
    </div>

    <!-- Sección de Notificaciones de Vencimiento -->
    <v-alert
      v-for="notificacion in notificacionesVencimiento"
      :key="notificacion.id"
      :type="notificacion.tipo"
      variant="tonal"
      class="mb-2"
      closable
      @click:close="cerrarNotificacion(notificacion.id)"
    >
      <strong>{{ notificacion.titulo }}</strong> - {{ notificacion.mensaje }}
    </v-alert>

    <!-- Filtros y Búsqueda -->
    <v-card class="mb-4 pa-4" variant="outlined">
      <v-row align="center">
        <!-- Búsqueda por palabras clave -->
        <v-col cols="12" md="6">
          <v-text-field
            v-model="busqueda"
            label="Buscar por título o descripción"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            clearable
            hide-details
            @input="aplicarFiltros"
          />
        </v-col>

        <!-- Filtro por estado -->
        <v-col cols="12" md="4">
          <v-select
            v-model="filtroEstado"
            :items="opcionesEstado"
            item-title="texto"
            item-value="valor"
            label="Filtrar por estado"
            variant="outlined"
            density="compact"
            hide-details
            @update:model-value="aplicarFiltros"
          />
        </v-col>

        <!-- Botón limpiar filtros -->
        <v-col cols="12" md="2">
          <v-btn
            variant="text"
            color="secondary"
            @click="limpiarFiltros"
            block
          >
            Limpiar
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <!-- Resumen de resultados -->
    <v-chip class="mb-3" color="primary" variant="tonal" size="small">
      Mostrando {{ tareasFiltradas.length }} de {{ tareas.length }} tareas
    </v-chip>

    <!-- Mensaje cuando no hay tareas -->
    <v-alert
      v-if="tareas.length === 0 && !errorLoading"
      type="info"
      variant="tonal"
      class="mb-4 text-center"
    >
      No tienes tareas registradas. Crea una nueva.
    </v-alert>

    <!-- Mensaje cuando no hay resultados de búsqueda -->
    <v-alert
      v-if="tareas.length > 0 && tareasFiltradas.length === 0"
      type="warning"
      variant="tonal"
      class="mb-4 text-center"
    >
      No se encontraron tareas con los filtros aplicados.
    </v-alert>

    <!-- Mensaje de error si hay un problema cargando las tareas -->
    <v-alert
      v-if="errorLoading"
      type="error"
      variant="tonal"
      class="mb-4 text-center"
    >
      Ocurrió un error al cargar las tareas. Intenta nuevamente.
    </v-alert>

    <!-- Tabla de tareas -->
    <v-card v-if="tareasFiltradas.length > 0" class="elevation-2">
      <v-table density="comfortable">
        <thead>
          <tr>
            <th class="text-left text-white">Título</th>
            <th class="text-left text-white">Sector</th>
            <th class="text-left text-white">Descripción</th>
            <th class="text-left text-white">Vencimiento</th>
            <th class="text-left text-white">Estado</th>
            <th class="text-left text-white">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tarea in tareasFiltradas" :key="tarea.id" :class="getRowClass(tarea)">
            <td class="fw-bold text-white">{{ tarea.titulo }}</td>
            <td class="text-white">{{ tarea.sectorNombre }}</td>
            <td class="text-white">{{ truncarTexto(tarea.descripcion, 50) }}</td>
            <td :class="getVencimientoClass(tarea)">
              {{ formatearFecha(tarea.fechaVencimiento) }}
              <v-icon
                v-if="estaProximoAVencer(tarea) && tarea.estado === 'PENDIENTE'"
                color="warning"
                size="small"
                class="ml-1"
              >
                mdi-alert
              </v-icon>
              <v-icon
                v-if="estaVencida(tarea) && tarea.estado === 'PENDIENTE'"
                color="error"
                size="small"
                class="ml-1"
              >
                mdi-alert-circle
              </v-icon>
            </td>
            <td>
              <v-chip :color="tarea.estado === 'COMPLETADA' ? 'success' : 'warning'"
                      :variant="tarea.estado === 'COMPLETADA' ? 'elevated' : 'tonal'" size="small">
                {{ tarea.estado }}
              </v-chip>
            </td>
            <td>
              <v-btn
                v-if="tarea.estado === 'PENDIENTE'"
                size="small"
                variant="outlined"
                color="success"
                @click="completarTarea(tarea.idTarea)"
                class="ms-2"
              >
                Completar
              </v-btn>

              <v-btn
                size="small"
                variant="outlined"
                color="primary"
                @click="editarTarea(tarea.idTarea)"
                class="ms-2"
              >
                Editar
              </v-btn>

              <v-btn
                size="small"
                variant="outlined"
                color="error"
                @click="eliminarTarea(tarea.idTarea)"
                class="ms-2"
              >
                Eliminar
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { tareaService } from '../services/tareaService';
import { useRouter } from 'vue-router';

const tareas = ref([]);
const errorLoading = ref(false);
const router = useRouter();

// Variables para filtros y búsqueda
const busqueda = ref('');
const filtroEstado = ref('TODOS');

// Opciones del filtro de estado
const opcionesEstado = [
  { texto: 'Todos', valor: 'TODOS' },
  { texto: 'Pendientes', valor: 'PENDIENTE' },
  { texto: 'Completadas', valor: 'COMPLETADA' }
];

// Variables para notificaciones
const notificacionesVencimiento = ref([]);
const notificacionesCerradas = ref(new Set());
let intervaloNotificaciones = null;

// Computed: Tareas filtradas según búsqueda y estado
const tareasFiltradas = computed(() => {
  let resultado = tareas.value;

  // Filtrar por estado
  if (filtroEstado.value !== 'TODOS') {
    resultado = resultado.filter(tarea => tarea.estado === filtroEstado.value);
  }

  // Filtrar por búsqueda (título o descripción)
  if (busqueda.value && busqueda.value.trim() !== '') {
    const terminoBusqueda = busqueda.value.toLowerCase().trim();
    resultado = resultado.filter(tarea => {
      const titulo = (tarea.titulo || '').toLowerCase();
      const descripcion = (tarea.descripcion || '').toLowerCase();
      return titulo.includes(terminoBusqueda) || descripcion.includes(terminoBusqueda);
    });
  }

  return resultado;
});

// Función para aplicar filtros (llamada en cambios)
const aplicarFiltros = () => {
  // Los filtros se aplican automáticamente mediante el computed
};

// Función para limpiar filtros
const limpiarFiltros = () => {
  busqueda.value = '';
  filtroEstado.value = 'TODOS';
};

// Cargar tareas al iniciar
const cargarTareas = async () => {
  try {
    const response = await tareaService.getTareasByUsuario();
    tareas.value = response;
    verificarNotificaciones();
  } catch (e) {
    console.error("Error cargando tareas", e);
    errorLoading.value = true;
  }
};

// Verificar y generar notificaciones de vencimiento
const verificarNotificaciones = () => {
  const ahora = new Date();
  const nuevasNotificaciones = [];

  tareas.value.forEach(tarea => {
    // Solo verificar tareas pendientes
    if (tarea.estado !== 'PENDIENTE' || !tarea.fechaVencimiento) return;

    // Si ya se cerró esta notificación, no mostrarla
    if (notificacionesCerradas.value.has(tarea.idTarea)) return;

    const fechaVencimiento = new Date(tarea.fechaVencimiento);
    const diferenciaHoras = (fechaVencimiento - ahora) / (1000 * 60 * 60);

    // Tarea vencida
    if (diferenciaHoras < 0) {
      nuevasNotificaciones.push({
        id: tarea.idTarea,
        titulo: tarea.titulo,
        mensaje: `Esta tarea venció el ${formatearFecha(tarea.fechaVencimiento)}`,
        tipo: 'error'
      });
    }
    // Vence en menos de 24 horas
    else if (diferenciaHoras <= 24) {
      nuevasNotificaciones.push({
        id: tarea.idTarea,
        titulo: tarea.titulo,
        mensaje: `Vence en menos de 24 horas (${formatearFecha(tarea.fechaVencimiento)})`,
        tipo: 'warning'
      });
    }
    // Vence en menos de 48 horas
    else if (diferenciaHoras <= 48) {
      nuevasNotificaciones.push({
        id: tarea.idTarea,
        titulo: tarea.titulo,
        mensaje: `Vence pronto: ${formatearFecha(tarea.fechaVencimiento)}`,
        tipo: 'info'
      });
    }
  });

  notificacionesVencimiento.value = nuevasNotificaciones;
};

// Cerrar una notificación
const cerrarNotificacion = (id) => {
  notificacionesCerradas.value.add(id);
  notificacionesVencimiento.value = notificacionesVencimiento.value.filter(n => n.id !== id);
};

// Verificar si una tarea está próxima a vencer (menos de 48 horas)
const estaProximoAVencer = (tarea) => {
  if (!tarea.fechaVencimiento) return false;
  const ahora = new Date();
  const fechaVencimiento = new Date(tarea.fechaVencimiento);
  const diferenciaHoras = (fechaVencimiento - ahora) / (1000 * 60 * 60);
  return diferenciaHoras > 0 && diferenciaHoras <= 48;
};

// Verificar si una tarea está vencida
const estaVencida = (tarea) => {
  if (!tarea.fechaVencimiento) return false;
  const ahora = new Date();
  const fechaVencimiento = new Date(tarea.fechaVencimiento);
  return fechaVencimiento < ahora;
};

// Obtener clase CSS para la fila según el estado de vencimiento
const getRowClass = (tarea) => {
  if (tarea.estado === 'COMPLETADA') return '';
  if (estaVencida(tarea)) return 'row-vencida';
  if (estaProximoAVencer(tarea)) return 'row-proxima';
  return '';
};

// Obtener clase CSS para la celda de vencimiento
const getVencimientoClass = (tarea) => {
  if (tarea.estado === 'COMPLETADA') return 'text-white';
  if (estaVencida(tarea)) return 'text-error font-weight-bold';
  if (estaProximoAVencer(tarea)) return 'text-warning font-weight-bold';
  return 'text-white';
};

// Truncar texto largo
const truncarTexto = (texto, maxLength) => {
  if (!texto) return '-';
  if (texto.length <= maxLength) return texto;
  return texto.substring(0, maxLength) + '...';
};

// Completar una tarea
const completarTarea = async (id) => {
  try {
    await tareaService.completar(id);
    cargarTareas();
  } catch (e) {
    alert("Error al completar tarea");
  }
};

// Editar una tarea
const editarTarea = (id) => {
  router.push(`/editar-tarea/${id}`);
};

// Eliminar una tarea
const eliminarTarea = async (id) => {
  const confirmDelete = confirm("¿Estás seguro de que deseas eliminar esta tarea?");
  if (confirmDelete) {
    try {
      await tareaService.eliminar(id);
      cargarTareas();
      alert("Tarea eliminada con éxito");
    } catch (e) {
      alert("Error al eliminar la tarea");
    }
  }
};

// Formatear la fecha en formato legible
const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  return new Date(fecha).toLocaleDateString() + ' ' + new Date(fecha).toLocaleTimeString().slice(0,5);
};

onMounted(() => {
  cargarTareas();
  // Verificar notificaciones cada 5 minutos
  intervaloNotificaciones = setInterval(verificarNotificaciones, 5 * 60 * 1000);
});

onUnmounted(() => {
  if (intervaloNotificaciones) {
    clearInterval(intervaloNotificaciones);
  }
});
</script>

<style scoped>
.row-vencida {
  background-color: rgba(211, 47, 47, 0.1) !important;
}

.row-proxima {
  background-color: rgba(249, 168, 37, 0.1) !important;
}

.text-error {
  color: #D32F2F !important;
}

.text-warning {
  color: #F9A825 !important;
}
</style>
