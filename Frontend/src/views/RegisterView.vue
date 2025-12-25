<template>
  <div class="page-wrapper">
    <div class="register-card shadow-lg">
      <div class="row g-0 h-100">
        
        <div class="col-md-6 d-flex flex-column justify-content-center p-5 bg-white rounded-start form-side">
          <h2 class="fw-bold text-primary mb-3">Bienvenido</h2>
          <p class="text-muted mb-4">Crea tu cuenta para gestionar tareas georreferenciadas.</p>

          <form @submit.prevent="registrarUsuario">
            
            <div class="form-floating mb-3">
              <input v-model="usuario.username" type="text" class="form-control" id="floatingUser" placeholder="Usuario" required>
              <label for="floatingUser">Nombre de Usuario</label>
            </div>

            <div class="form-floating mb-4">
              <input v-model="usuario.password" type="password" class="form-control" id="floatingPass" placeholder="Contraseña" required>
              <label for="floatingPass">Contraseña</label>
            </div>

            <div class="d-flex align-items-center mb-4">
              <div v-if="markerLatLng" class="text-success small fw-bold">
                Ubicación lista
              </div>
              <div v-else class="text-secondary small">
                Haz clic en el mapa para ubicarte
              </div>
            </div>

            <button type="submit" class="btn btn-primary w-100 py-2 fs-6 fw-bold text-uppercase spacing-1" :disabled="!markerLatLng">
              Crear Cuenta
            </button>
          </form>

          <div v-if="mensaje" :class="['alert mt-3 text-center py-2 small', error ? 'alert-danger' : 'alert-success']">
            {{ mensaje }}
          </div>
        </div>

        <div class="col-md-6 position-relative map-side">
          <l-map 
            ref="map" 
            v-model:zoom="zoom" 
            :center="center" 
            :use-global-leaflet="false"
            @click="onMapClick"
            class="map-container"
          >
            <l-tile-layer
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              layer-type="base"
              name="OpenStreetMap"
            ></l-tile-layer>

            <l-marker v-if="markerLatLng" :lat-lng="markerLatLng">
              <l-tooltip>Tu ubicación</l-tooltip>
            </l-marker>
          </l-map>
          
          <div class="map-overlay">
            Selecciona tu ubicación
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { LMap, LTileLayer, LMarker, LTooltip } from "@vue-leaflet/vue-leaflet";
import "leaflet/dist/leaflet.css";
import { usuarioService } from '../services/usuarioService'; // Importar el servicio de usuario
import { useRouter } from 'vue-router';

const router = useRouter();
const usuario = ref({ username: '', password: '' });
const zoom = ref(12);
const center = ref([-33.4489, -70.6693]); 
const markerLatLng = ref(null);
const mensaje = ref('');
const error = ref(false);

const onMapClick = (e) => {
  markerLatLng.value = e.latlng; 
};

const registrarUsuario = async () => {
  if (!markerLatLng.value) return;
  try {
    const payload = {
      username: usuario.value.username,
      password: usuario.value.password,
      latitud: markerLatLng.value.lat,
      longitud: markerLatLng.value.lng
    };

    // Usar el servicio para registrar el usuario
    await usuarioService.registrar(payload); 

    mensaje.value = "¡Registro exitoso!";
    error.value = false;
    setTimeout(() => router.push('/login'), 1500); // Redirigir al login sin guardar el token
  } catch (e) {
    mensaje.value = "Error: " + (e.response?.data?.message || e.message);
    error.value = true;
  }
};
</script>

<style scoped>
/* Contenedor que centra todo en la pantalla */
.page-wrapper {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #212529; 
}

/* La tarjeta con tamaño fijo para que no se deforme */
.register-card {
  width: 900px; 
  height: 550px; 
  background-color: white;
  border-radius: 15px;
  overflow: hidden; 
}

/* Ajustes del mapa */
.map-container {
  height: 100%;
  width: 100%;
  z-index: 1;
}

.map-side {
  background-color: #e9ecef;
}

.map-overlay {
  position: absolute;
  top: 20px;
  right: 20px;
  background: white;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: bold;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  z-index: 1000; 
  pointer-events: none;
}

/* Animación simple para el dedo */
.animate-bounce {
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(5px); }
}

/* Responsividad: En celulares se apila */
@media (max-width: 768px) {
  .register-card {
    width: 95%;
    height: auto;
    flex-direction: column;
  }
  .map-side {
    height: 300px;
  }
}
</style>


