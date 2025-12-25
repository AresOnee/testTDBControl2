<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import Navbar from './components/Navbar.vue'

const route = useRoute()

// Mostrar el Navbar solo si no estamos en /login ni en /register
const showNavbar = computed(() => {
  return !['/login', '/register'].includes(route.path)
})
</script>

<template>
  <v-app>
    <Navbar v-if="showNavbar" />
    <v-main>
      <RouterView />
    </v-main>
  </v-app>
</template>
