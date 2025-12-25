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

    <!-- Mensaje cuando no hay tareas -->
    <v-alert
      v-if="tareas.length === 0"
      type="info"
      variant="tonal"
      class="mb-4 text-center"
    >
      No tienes tareas registradas. Crea una nueva.
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
    <v-card v-else class="elevation-2">
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
          <tr v-for="tarea in tareas" :key="tarea.id">
            <td class="fw-bold text-white">{{ tarea.titulo }}</td>
            <td class="text-white">{{ tarea.sectorNombre }}</td>
            <td class="text-white">{{ tarea.descripcion }}</td>
            <td>{{ formatearFecha(tarea.fechaVencimiento) }}</td>
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
import { ref, onMounted } from 'vue';
import { tareaService } from '../services/tareaService';  // Importar el servicio de tareas
import { useRouter } from 'vue-router';  // Importar el hook de Vue Router

const tareas = ref([]);   // Almacenar las tareas del usuario
const errorLoading = ref(false);  // Para manejar errores de carga
const router = useRouter(); // Inicializar el router

// Cargar tareas al iniciar
const cargarTareas = async () => {
  try {
    const response = await tareaService.getTareasByUsuario();  // Llamar al servicio para obtener las tareas por usuario
    tareas.value = response;  // Almacenar las tareas en el estado
  } catch (e) {
    console.error("Error cargando tareas", e);
    errorLoading.value = true;  // Si hay un error, activar el indicador de error
  }
};

// Completar una tarea
const completarTarea = async (id) => {
  try {
    await tareaService.completar(id);  // Llamar al servicio para marcar la tarea como completada
    cargarTareas();  // Recargar las tareas después de completar una
  } catch (e) {
    alert("Error al completar tarea");
  }
};

// Editar una tarea
const editarTarea = (id) => {
  router.push(`/editar-tarea/${id}`); // Redirige a la ruta de edición de tarea con el ID
};

// Eliminar una tarea
const eliminarTarea = async (id) => {
  const confirmDelete = confirm("¿Estás seguro de que deseas eliminar esta tarea?");
  if (confirmDelete) {
    try {
      await tareaService.eliminar(id);  // Llamar al servicio para eliminar la tarea
      cargarTareas();  // Recargar las tareas después de eliminar una
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
});
</script>






