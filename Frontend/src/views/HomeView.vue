<template>
  <v-container class="py-8">
    <!-- Header de bienvenida -->
    <v-row class="mb-6">
      <v-col cols="12">
        <div class="text-center">
          <v-icon size="64" color="primary" class="mb-4">mdi-clipboard-check-multiple</v-icon>
          <h1 class="text-h3 font-weight-bold mb-2">Sistema de Gestion de Tareas</h1>
          <p class="text-h6 text-grey">Bienvenido, <strong>{{ nombreUsuario }}</strong></p>
        </div>
      </v-col>
    </v-row>

    <!-- Descripcion del sistema -->
    <v-row class="mb-6">
      <v-col cols="12">
        <v-card elevation="2" class="pa-6">
          <v-card-title class="text-h5 mb-2">
            <v-icon class="mr-2" color="info">mdi-information</v-icon>
            Acerca del Sistema
          </v-card-title>
          <v-card-text class="text-body-1">
            <p>
              Este sistema permite gestionar tareas asignadas a diferentes <strong>sectores geograficos</strong>.
              Cada tarea tiene una ubicacion espacial asociada a un sector, lo que permite realizar
              <strong>consultas geoespaciales avanzadas</strong> utilizando PostGIS.
            </p>
            <p class="mt-3">
              El sistema responde preguntas como: cual es la tarea mas cercana, que sectores tienen mas
              tareas pendientes, y calcular distancias promedio entre tareas y usuarios.
            </p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Tarjetas de acceso rapido -->
    <v-row>
      <v-col cols="12" md="4">
        <v-card
          class="h-100 card-hover"
          elevation="3"
          @click="$router.push('/tareas')"
          style="cursor: pointer"
        >
          <v-card-text class="text-center pa-6">
            <v-icon size="56" color="primary" class="mb-4">mdi-format-list-checks</v-icon>
            <h3 class="text-h5 font-weight-bold mb-2">Mis Tareas</h3>
            <p class="text-grey">
              Ver, filtrar y gestionar tus tareas pendientes y completadas
            </p>
            <v-chip color="primary" variant="outlined" class="mt-2">
              <v-icon start>mdi-arrow-right</v-icon>
              Ir a Tareas
            </v-chip>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card
          class="h-100 card-hover"
          elevation="3"
          @click="$router.push('/crear-tarea')"
          style="cursor: pointer"
        >
          <v-card-text class="text-center pa-6">
            <v-icon size="56" color="success" class="mb-4">mdi-plus-circle</v-icon>
            <h3 class="text-h5 font-weight-bold mb-2">Nueva Tarea</h3>
            <p class="text-grey">
              Crear una nueva tarea y asignarla a un sector geografico
            </p>
            <v-chip color="success" variant="outlined" class="mt-2">
              <v-icon start>mdi-arrow-right</v-icon>
              Crear Tarea
            </v-chip>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card
          class="h-100 card-hover"
          elevation="3"
          @click="$router.push('/estadisticas')"
          style="cursor: pointer"
        >
          <v-card-text class="text-center pa-6">
            <v-icon size="56" color="warning" class="mb-4">mdi-chart-bar</v-icon>
            <h3 class="text-h5 font-weight-bold mb-2">Estadisticas</h3>
            <p class="text-grey">
              Consultas geoespaciales y estadisticas de tareas por sector
            </p>
            <v-chip color="warning" variant="outlined" class="mt-2">
              <v-icon start>mdi-arrow-right</v-icon>
              Ver Estadisticas
            </v-chip>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Funcionalidades del sistema -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title class="bg-primary text-white pa-4">
            <v-icon class="mr-2">mdi-feature-search</v-icon>
            Funcionalidades Geoespaciales
          </v-card-title>
          <v-card-text class="pa-4">
            <v-row>
              <v-col cols="12" md="6">
                <v-list density="compact">
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Tareas por sector del usuario</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Tarea pendiente mas cercana</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Sector con mas tareas en radio de 2km</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Promedio de distancia de tareas</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-col>
              <v-col cols="12" md="6">
                <v-list density="compact">
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Concentracion de tareas pendientes</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Sector con mas tareas en radio de 5km</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Tareas completadas por usuario y sector</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="success">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Promedio de distancia global</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Tecnologias utilizadas -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title class="pa-4">
            <v-icon class="mr-2" color="secondary">mdi-cog</v-icon>
            Tecnologias Utilizadas
          </v-card-title>
          <v-card-text>
            <v-chip-group>
              <v-chip color="green" variant="elevated">
                <v-icon start>mdi-vuejs</v-icon>
                Vue.js 3
              </v-chip>
              <v-chip color="blue" variant="elevated">
                <v-icon start>mdi-vuetify</v-icon>
                Vuetify 3
              </v-chip>
              <v-chip color="green-darken-3" variant="elevated">
                <v-icon start>mdi-leaf</v-icon>
                Spring Boot
              </v-chip>
              <v-chip color="blue-darken-3" variant="elevated">
                <v-icon start>mdi-database</v-icon>
                PostgreSQL
              </v-chip>
              <v-chip color="teal" variant="elevated">
                <v-icon start>mdi-map-marker</v-icon>
                PostGIS
              </v-chip>
              <v-chip color="orange" variant="elevated">
                <v-icon start>mdi-shield-key</v-icon>
                JWT Auth
              </v-chip>
            </v-chip-group>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const nombreUsuario = ref('Usuario');

onMounted(() => {
  const nombre = localStorage.getItem('usuarioNombre');
  if (nombre) {
    nombreUsuario.value = nombre;
  }
});
</script>

<style scoped>
.card-hover {
  transition: transform 0.2s, box-shadow 0.2s;
}

.card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
}

.h-100 {
  height: 100%;
}
</style>
