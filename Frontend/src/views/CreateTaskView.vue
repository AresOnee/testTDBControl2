<template>
  <v-container class="py-6 d-flex justify-center">
    <v-card max-width="720" class="w-100">
      <v-card-title class="text-h5">Crear Tarea</v-card-title>
      <v-divider></v-divider>
      <v-card-text>
        <v-form @submit.prevent="crearTarea">
          <v-row dense>
            <v-col cols="12">
              <v-text-field
                v-model="tarea.titulo"
                label="Título"
                placeholder="Título de la tarea"
                required
              />
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="tarea.descripcion"
                label="Descripción"
                placeholder="Descripción de la tarea"
                auto-grow
                required
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="tarea.fechaVencimiento"
                type="datetime-local"
                label="Fecha de vencimiento"
                required
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="tarea.sectorId"
                :items="sectores"
                item-title="nombre"
                item-value="id"
                label="Sector"
                placeholder="Selecciona un sector"
                required
              />
            </v-col>
          </v-row>
          <div class="d-flex gap-2 mt-2">
            <v-btn type="submit" color="primary" :loading="loading">Crear Tarea</v-btn>
          </div>

          <v-alert v-if="loading" type="info" class="mt-4" variant="tonal">Cargando...</v-alert>
          <v-alert v-if="error" type="error" class="mt-4" variant="tonal">{{ errorMessage }}</v-alert>
          <v-alert v-if="success" type="success" class="mt-4" variant="tonal">Tarea creada con éxito</v-alert>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import { sectorService } from '@/services/sectorService'; 
import { tareaService } from '@/services/tareaService'; 

export default {
  data() {
    return {
      tarea: {
        titulo: '',
        descripcion: '',
        fechaVencimiento: '',
        sectorId: null,
      },
      sectores: [], // Lista de sectores para mostrar en el formulario
      loading: false, // Estado para mostrar el mensaje de carga
      error: false,   // Estado para el mensaje de error
      errorMessage: '', // Mensaje de error
      success: false, // Estado para el mensaje de éxito
    };
  },
  created() {
    // Obtener los sectores cuando se carga el componente
    this.obtenerSectores();
  },
  methods: {
    // Usamos el servicio sectorService para obtener los sectores
    async obtenerSectores() {
      this.loading = true;  // Mostramos el mensaje de carga
      this.error = false;   // Reseteamos el estado de error
      this.success = false; // Reseteamos el estado de éxito

      try {
        const response = await sectorService.getAll(); // Llamada al servicio para obtener los sectores
        this.sectores = response; // Guardamos la respuesta de los sectores
        this.loading = false;
      } catch (error) {
        console.error('Error al obtener sectores:', error);
        this.error = true;
        this.errorMessage = 'No se pudieron cargar los sectores.'; // Mensaje de error
        this.loading = false;
      }
    },

    // Método para crear la tarea
    async crearTarea() {
      if (!this.tarea.titulo || !this.tarea.descripcion || !this.tarea.fechaVencimiento || !this.tarea.sectorId) {
        this.error = true;
        this.errorMessage = 'Por favor, complete todos los campos.';
        return;
      }

      this.loading = true;
      this.error = false;
      this.success = false;

      try {
        const tareaDTO = {
          titulo: this.tarea.titulo,
          descripcion: this.tarea.descripcion,
          fechaVencimiento: this.tarea.fechaVencimiento,
          sectorId: this.tarea.sectorId,
        };

        const response = await tareaService.create(tareaDTO); // Llamada al servicio para crear la tarea
        this.success = true;
        this.loading = false;

        // Redirigir a la página de tareas después de crear la tarea
        this.$router.push('/tareas'); 

      } catch (error) {
        this.error = true;
        this.errorMessage = error.message || 'Hubo un error al crear la tarea.'; // Mensaje de error
        this.loading = false;
      }
    },
  },
};
</script>





 



