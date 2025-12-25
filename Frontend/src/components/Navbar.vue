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
      <span class="text-white" style="margin-right:12px">{{ nombreUsuario }}</span>
      <v-btn color="white" variant="outlined" size="small" @click="logout">Cerrar Sesión</v-btn>
    </div>
  </v-app-bar>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { usuarioService } from '../services/usuarioService'; // Asegúrate de que este servicio esté importado

const router = useRouter();
const nombreUsuario = ref(localStorage.getItem('usuarioNombre') || 'Usuario');

// Actualizar nombre de usuario
nombreUsuario.value = localStorage.getItem('usuarioNombre') || 'Usuario';

const goHome = () => router.push('/tareas');
const goTasks = () => router.push('/tareas');
const goCreateTask = () => router.push('/crear-tarea');
const goStats = () => router.push('/estadisticas');
const goBack = () => router.back();

const logout = () => {
  // Usar el método de logout del usuarioService
  usuarioService.logout();  // Esto elimina el token y los datos del usuario en el localStorage

  // Restablecer el nombre de usuario
  nombreUsuario.value = '';

  // Redirigir al login
  router.replace('/login'); // Usamos replace para evitar que el usuario regrese al login
};
</script>
