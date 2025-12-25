<template>
  <v-container class="py-6 d-flex justify-center">
    <v-card max-width="720" class="w-100">
      <v-card-title class="text-h5">Editar Tarea</v-card-title>
      <v-divider></v-divider>
      <v-card-text>
        <v-form @submit.prevent="editarTarea">
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
            <v-btn type="submit" color="primary" :loading="loading">Actualizar Tarea</v-btn>
          </div>

          <v-alert v-if="loading" type="info" class="mt-4" variant="tonal">Cargando...</v-alert>
          <v-alert v-if="error" type="error" class="mt-4" variant="tonal">{{ errorMessage }}</v-alert>
          <v-alert v-if="success" type="success" class="mt-4" variant="tonal">Tarea actualizada con éxito</v-alert>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import { tareaService } from '@/services/tareaService'; 
import { sectorService } from '@/services/sectorService'; 

export default {
  data() {
    return {
      tarea: {
        id: null,
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
    // Obtener la ID de la tarea desde la URL
    const tareaId = this.$route.params.id;
    // Obtener la tarea y los sectores cuando se carga el componente
    this.obtenerTarea(tareaId);
    this.obtenerSectores();
  },
  methods: {
    // Obtener la tarea por su ID desde el servicio
    async obtenerTarea(tareaId) {
      this.loading = true;  // Mostrar el indicador de carga
      this.error = false;   // Restablecer estado de error

      try {
        const response = await tareaService.getTareaById(tareaId);  // Llamar al servicio para obtener la tarea
        this.tarea = response;  // Guardar la tarea en el estado
        if (!this.tarea.id) {
            this.tarea.id = tareaId;
        }
        this.loading = false;  
      } catch (error) {
        console.error('Error al obtener la tarea:', error);
        this.error = true;
        this.errorMessage = 'No se pudo cargar la tarea.';
        this.loading = false;  
      }
    },

    // Obtener los sectores disponibles desde el servicio
    async obtenerSectores() {
      this.loading = true;  
      this.error = false;   

      try {
        const response = await sectorService.getAll(); // Llamada al servicio para obtener los sectores
        this.sectores = response;  // Guardamos la respuesta de los sectores
        this.loading = false;  // Ocultar el indicador de carga
      } catch (error) {
        console.error('Error al obtener sectores:', error);
        this.error = true;
        this.errorMessage = 'No se pudieron cargar los sectores.';
        this.loading = false;  // Ocultar el indicador de carga
      }
    },

    // Método para actualizar la tarea
    async editarTarea() {
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
          id: this.tarea.id,
          titulo: this.tarea.titulo,
          descripcion: this.tarea.descripcion,
          fechaVencimiento: this.tarea.fechaVencimiento,
          sectorId: this.tarea.sectorId,
        };

        const response = await tareaService.editar(this.tarea.id, tareaDTO); // Llamada al servicio para actualizar la tarea
        this.success = true;
        this.loading = false;
        this.$router.push({ name: 'tareas' }); // Redirigir al listado de tareas
      } catch (error) {
        this.error = true;
        this.errorMessage = error.message || 'Hubo un error al actualizar la tarea.'; // Mensaje de error
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
</style>
