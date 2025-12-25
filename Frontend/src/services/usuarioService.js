import api from './api'

export const usuarioService = {
  async registrar(usuarioData) {
    const response = await api.post('/api/usuarios/registro', usuarioData)
    return response.data  
  },

  async login(usuarioData) {
    const response = await api.post('/api/usuarios/login', usuarioData)
    if (response.data.token) {
      localStorage.setItem('jwt_token', response.data.token) 
      localStorage.setItem('user', JSON.stringify(response.data.user)) 
    }
    return response.data
  },

  logout() {
    localStorage.removeItem('jwt_token') // Eliminar el token de localStorage
    localStorage.removeItem('user')  // Eliminar los datos del usuario de localStorage
    window.location.href = '/login' // Redirigir al login
  },

  getCurrentUser() {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null 
  },

  getToken() {
    return localStorage.getItem('jwt_token') 
  },

  isAuthenticated() {
    return !!this.getToken() 
  }
}

