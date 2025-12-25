<template>
  <v-container class="py-6">
    <div class="d-flex justify-space-between align-center mb-6">
      <h2 class="text-h4 font-weight-bold">Estadísticas Geoespaciales</h2>
      <v-btn color="primary" variant="outlined" @click="recargarTodo">
        <v-icon left>mdi-refresh</v-icon>
        Actualizar Datos
      </v-btn>
    </div>

    <!-- Alerta si no hay usuario ID -->
    <v-alert v-if="!usuarioId" type="warning" variant="tonal" class="mb-4">
      No se pudo obtener el ID del usuario. Algunas estadísticas no estarán disponibles.
    </v-alert>

    <!-- Loading general -->
    <v-progress-linear v-if="cargandoGeneral" indeterminate color="primary" class="mb-4"></v-progress-linear>

    <!-- Grid de estadísticas -->
    <v-row>
      <!-- 1. Tareas por Sector del Usuario -->
      <v-col cols="12" md="6">
        <v-card class="h-100" elevation="2">
          <v-card-title class="bg-primary text-white">
            <v-icon class="mr-2">mdi-chart-pie</v-icon>
            Mis Tareas por Sector
          </v-card-title>
          <v-card-subtitle class="pt-2">
            ¿Cuántas tareas ha hecho el usuario por sector?
          </v-card-subtitle>
          <v-card-text>
            <v-progress-circular v-if="cargando.tareasPorSector" indeterminate size="24"></v-progress-circular>
            <v-table v-else-if="tareasPorSector.length > 0" density="compact">
              <thead>
                <tr>
                  <th>Sector</th>
                  <th class="text-right">Cantidad</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in tareasPorSector" :key="index">
                  <td>{{ item[0] }}</td>
                  <td class="text-right">
                    <v-chip size="small" color="primary">{{ item[1] }}</v-chip>
                  </td>
                </tr>
              </tbody>
            </v-table>
            <div v-else class="text-grey text-center py-4">Sin datos disponibles</div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 2. Tarea Más Cercana Pendiente -->
      <v-col cols="12" md="6">
        <v-card class="h-100" elevation="2">
          <v-card-title class="bg-success text-white">
            <v-icon class="mr-2">mdi-map-marker-radius</v-icon>
            Tarea Pendiente Más Cercana
          </v-card-title>
          <v-card-subtitle class="pt-2">
            ¿Cuál es la tarea más cercana al usuario (que esté pendiente)?
          </v-card-subtitle>
          <v-card-text>
            <v-progress-circular v-if="cargando.tareaCercana" indeterminate size="24"></v-progress-circular>
            <div v-else-if="tareaCercana">
              <v-list-item class="px-0">
                <v-list-item-title class="font-weight-bold">{{ tareaCercana.titulo }}</v-list-item-title>
                <v-list-item-subtitle>{{ tareaCercana.descripcion }}</v-list-item-subtitle>
                <template v-slot:append>
                  <v-chip color="warning" size="small">{{ tareaCercana.estado }}</v-chip>
                </template>
              </v-list-item>
              <div class="text-caption text-grey mt-2">
                Sector: {{ tareaCercana.sector?.nombre || 'N/A' }}
              </div>
            </div>
            <div v-else class="text-grey text-center py-4">No hay tareas pendientes cercanas</div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 3. Sector con más tareas en 2km -->
      <v-col cols="12" md="6">
        <v-card class="h-100" elevation="2">
          <v-card-title class="bg-info text-white">
            <v-icon class="mr-2">mdi-radius-outline</v-icon>
            Sector Top (Radio 2 km)
          </v-card-title>
          <v-card-subtitle class="pt-2">
            ¿Cuál es el sector con más tareas completadas en un radio de 2 km?
          </v-card-subtitle>
          <v-card-text>
            <v-progress-circular v-if="cargando.sector2km" indeterminate size="24"></v-progress-circular>
            <div v-else-if="sector2km && sector2km !== 'No encontrado'" class="text-center py-4">
              <v-icon size="48" color="info" class="mb-2">mdi-map-marker-check</v-icon>
              <div class="text-h6">{{ sector2km }}</div>
            </div>
            <div v-else class="text-grey text-center py-4">No hay sectores con tareas en este radio</div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 4. Sector con más tareas en 5km -->
      <v-col cols="12" md="6">
        <v-card class="h-100" elevation="2">
          <v-card-title class="bg-warning text-white">
            <v-icon class="mr-2">mdi-radius</v-icon>
            Sector Top (Radio 5 km)
          </v-card-title>
          <v-card-subtitle class="pt-2">
            ¿Cuál es el sector con más tareas completadas dentro de un radio de 5 km?
          </v-card-subtitle>
          <v-card-text>
            <v-progress-circular v-if="cargando.sector5km" indeterminate size="24"></v-progress-circular>
            <div v-else-if="sector5km && sector5km !== 'No encontrado'" class="text-center py-4">
              <v-icon size="48" color="warning" class="mb-2">mdi-map-marker-check</v-icon>
              <div class="text-h6">{{ sector5km }}</div>
            </div>
            <div v-else class="text-grey text-center py-4">No hay sectores con tareas en este radio</div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 5. Promedio de Distancia -->
      <v-col cols="12" md="6">
        <v-card class="h-100" elevation="2">
          <v-card-title class="bg-purple text-white">
            <v-icon class="mr-2">mdi-map-marker-distance</v-icon>
            Promedio de Distancia
          </v-card-title>
          <v-card-subtitle class="pt-2">
            ¿Cuál es el promedio de distancia de las tareas completadas respecto a tu ubicación?
          </v-card-subtitle>
          <v-card-text>
            <v-progress-circular v-if="cargando.promedioDistancia" indeterminate size="24"></v-progress-circular>
            <div v-else class="text-center py-4">
              <v-icon size="48" color="purple" class="mb-2">mdi-ruler</v-icon>
              <div class="text-h5">{{ formatearDistancia(promedioDistancia) }}</div>
              <div class="text-caption text-grey">desde tu ubicación</div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 6. Concentración de Tareas Pendientes -->
      <v-col cols="12" md="6">
        <v-card class="h-100" elevation="2">
          <v-card-title class="bg-error text-white">
            <v-icon class="mr-2">mdi-map-marker-multiple</v-icon>
            Concentración de Pendientes
          </v-card-title>
          <v-card-subtitle class="pt-2">
            ¿En qué sectores geográficos se concentran la mayoría de las tareas pendientes?
          </v-card-subtitle>
          <v-card-text>
            <v-progress-circular v-if="cargando.concentracion" indeterminate size="24"></v-progress-circular>
            <v-table v-else-if="concentracionPendientes.length > 0" density="compact">
              <thead>
                <tr>
                  <th>Sector</th>
                  <th class="text-right">Pendientes</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in concentracionPendientes" :key="index">
                  <td>{{ item[0] }}</td>
                  <td class="text-right">
                    <v-chip size="small" color="error">{{ item[1] }}</v-chip>
                  </td>
                </tr>
              </tbody>
            </v-table>
            <div v-else class="text-grey text-center py-4">No hay tareas pendientes</div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 7. Tareas Completadas por Usuario y Sector (Global) -->
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title class="bg-teal text-white">
            <v-icon class="mr-2">mdi-account-group</v-icon>
            Tareas Completadas por Usuario y Sector
          </v-card-title>
          <v-card-subtitle class="pt-2">
            ¿Cuántas tareas ha realizado cada usuario por sector? (Estadística global)
          </v-card-subtitle>
          <v-card-text>
            <v-progress-circular v-if="cargando.completadasGlobal" indeterminate size="24"></v-progress-circular>
            <v-table v-else-if="completadasPorUsuarioSector.length > 0" density="compact">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Sector</th>
                  <th class="text-right">Completadas</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in completadasPorUsuarioSector" :key="index">
                  <td>
                    <v-chip size="small" variant="outlined">{{ item[0] }}</v-chip>
                  </td>
                  <td>{{ item[1] }}</td>
                  <td class="text-right">
                    <v-chip size="small" color="success">{{ item[2] }}</v-chip>
                  </td>
                </tr>
              </tbody>
            </v-table>
            <div v-else class="text-grey text-center py-4">No hay datos de tareas completadas</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { tareaService } from '../services/tareaService';

// Estado de carga individual
const cargando = ref({
  tareasPorSector: false,
  tareaCercana: false,
  sector2km: false,
  sector5km: false,
  promedioDistancia: false,
  concentracion: false,
  completadasGlobal: false
});

const cargandoGeneral = ref(false);

// Datos
const usuarioId = ref(null);
const tareasPorSector = ref([]);
const tareaCercana = ref(null);
const sector2km = ref(null);
const sector5km = ref(null);
const promedioDistancia = ref(0);
const concentracionPendientes = ref([]);
const completadasPorUsuarioSector = ref([]);

// Obtener usuario ID del localStorage
const obtenerUsuarioId = () => {
  const id = localStorage.getItem('usuarioId');
  if (id) {
    usuarioId.value = parseInt(id);
  }
};

// Formatear distancia en metros/km
const formatearDistancia = (metros) => {
  if (!metros || metros === 0) return '0 m';
  if (metros < 1000) {
    return `${metros.toFixed(0)} m`;
  }
  return `${(metros / 1000).toFixed(2)} km`;
};

// Cargar todas las estadísticas
const cargarEstadisticas = async () => {
  obtenerUsuarioId();

  if (!usuarioId.value) {
    console.warn('No se encontró usuarioId en localStorage');
    return;
  }

  // Cargar en paralelo
  await Promise.all([
    cargarTareasPorSector(),
    cargarTareaCercana(),
    cargarSector2km(),
    cargarSector5km(),
    cargarPromedioDistancia(),
    cargarConcentracionPendientes(),
    cargarCompletadasGlobal()
  ]);
};

// 1. Tareas por sector del usuario
const cargarTareasPorSector = async () => {
  if (!usuarioId.value) return;
  cargando.value.tareasPorSector = true;
  try {
    const data = await tareaService.tareasPorSector(usuarioId.value);
    tareasPorSector.value = data || [];
  } catch (error) {
    console.error('Error cargando tareas por sector:', error);
    tareasPorSector.value = [];
  } finally {
    cargando.value.tareasPorSector = false;
  }
};

// 2. Tarea más cercana pendiente
const cargarTareaCercana = async () => {
  if (!usuarioId.value) return;
  cargando.value.tareaCercana = true;
  try {
    const data = await tareaService.tareaMasCercana(usuarioId.value);
    tareaCercana.value = data;
  } catch (error) {
    console.error('Error cargando tarea cercana:', error);
    tareaCercana.value = null;
  } finally {
    cargando.value.tareaCercana = false;
  }
};

// 3. Sector con más tareas en 2km
const cargarSector2km = async () => {
  if (!usuarioId.value) return;
  cargando.value.sector2km = true;
  try {
    const data = await tareaService.sectorTopRadio(usuarioId.value, 2);
    sector2km.value = data;
  } catch (error) {
    console.error('Error cargando sector 2km:', error);
    sector2km.value = null;
  } finally {
    cargando.value.sector2km = false;
  }
};

// 4. Sector con más tareas en 5km
const cargarSector5km = async () => {
  if (!usuarioId.value) return;
  cargando.value.sector5km = true;
  try {
    const data = await tareaService.sectorTopRadio(usuarioId.value, 5);
    sector5km.value = data;
  } catch (error) {
    console.error('Error cargando sector 5km:', error);
    sector5km.value = null;
  } finally {
    cargando.value.sector5km = false;
  }
};

// 5. Promedio de distancia
const cargarPromedioDistancia = async () => {
  if (!usuarioId.value) return;
  cargando.value.promedioDistancia = true;
  try {
    const data = await tareaService.promedioDistancia(usuarioId.value);
    promedioDistancia.value = data || 0;
  } catch (error) {
    console.error('Error cargando promedio distancia:', error);
    promedioDistancia.value = 0;
  } finally {
    cargando.value.promedioDistancia = false;
  }
};

// 6. Concentración de pendientes por sector
const cargarConcentracionPendientes = async () => {
  cargando.value.concentracion = true;
  try {
    const data = await tareaService.tareasPendientesPorSector();
    concentracionPendientes.value = data || [];
  } catch (error) {
    console.error('Error cargando concentración:', error);
    concentracionPendientes.value = [];
  } finally {
    cargando.value.concentracion = false;
  }
};

// 7. Tareas completadas por usuario y sector (global)
const cargarCompletadasGlobal = async () => {
  cargando.value.completadasGlobal = true;
  try {
    const data = await tareaService.tareasCompletadasPorUsuarioYSector();
    completadasPorUsuarioSector.value = data || [];
  } catch (error) {
    console.error('Error cargando completadas global:', error);
    completadasPorUsuarioSector.value = [];
  } finally {
    cargando.value.completadasGlobal = false;
  }
};

// Recargar todo
const recargarTodo = async () => {
  cargandoGeneral.value = true;
  await cargarEstadisticas();
  cargandoGeneral.value = false;
};

onMounted(() => {
  cargarEstadisticas();
});
</script>

<style scoped>
.bg-purple {
  background-color: #9C27B0 !important;
}

.bg-teal {
  background-color: #009688 !important;
}

.h-100 {
  height: 100%;
}
</style>
