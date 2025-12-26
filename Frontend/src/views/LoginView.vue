<template>
  <div class="page-wrapper">
    <div class="login-card shadow-lg">
      <div class="p-5">
        <h2 class="fw-bold text-center text-primary mb-4">Iniciar Sesión</h2>
        
        <form @submit.prevent="login">
          <div class="mb-3">
            <label class="form-label" for="user">Nombre de Usuario</label>
            <input v-model="credenciales.username" type="text" class="form-control" id="user" placeholder="Nombre de Usuario" required>
          </div>

          <div class="mb-4">
            <label class="form-label" for="pass">Contraseña</label>
            <input v-model="credenciales.password" type="password" class="form-control" id="pass" placeholder="********" required>
          </div>

          <div class="d-grid gap-2">
            <button type="submit" class="btn btn-primary btn-lg fw-bold">Entrar</button>
            <button type="button" class="btn btn-outline-secondary" @click="$router.push('/register')">
              ¿No tienes cuenta? Regístrate
            </button>
          </div>
        </form>

        <div v-if="mensaje" :class="['alert mt-4 text-center', error ? 'alert-danger' : 'alert-success']">
          {{ mensaje }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { usuarioService } from '../services/usuarioService'; // Importar el servicio de usuario

const router = useRouter();
const credenciales = ref({ username: '', password: '' });
const mensaje = ref('');
const error = ref(false);

const login = async () => {
  try {
    // Llamar al servicio de login
    const response = await usuarioService.login(credenciales.value);

    const usuario = response;

    // Guardar el token y datos del usuario en localStorage
    localStorage.setItem('jwt_token', usuario.token); 
    localStorage.setItem('usuarioId', usuario.id);
    localStorage.setItem('usuarioNombre', usuario.username || usuario.nombre || 'Usuario');

    // Mostrar un mensaje de bienvenida
    mensaje.value = "¡Bienvenido " + (usuario.username || 'Usuario') + "!";
    error.value = false;

    // Redirigir a la página de inicio
    setTimeout(() => {
      router.replace('/inicio'); // Usamos replace para evitar que el usuario regrese al login
    }, 1000);

  } catch (e) {
    console.error(e); 
    error.value = true;
    if (e.response && e.response.status === 401) {
      mensaje.value = "Usuario o contraseña incorrectos.";
    } else {
      mensaje.value = "Usuario o contraseña incorrectos.";
    }
  }
};
</script>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #212529;
}

.login-card {
  width: 400px;
  background: white;
  border-radius: 15px;
}

.form-control {
  background-color: #ffffff;
  color: #212529;
  border-color: #ced4da;
}

.form-control::placeholder {
  color: #6c757d;
  opacity: 0.9;
}

.form-label {
  color: #4a5563;
  font-weight: 600;
}

.btn-primary {
  background-color: #1976D2;
  border-color: #1976D2;
}

.btn-outline-secondary {
  background-color: #444546;
  border-color: #444546;
}
</style>

