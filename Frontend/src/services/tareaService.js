import api from './api'

export const tareaService = {

  async getTareasByUsuario() {
    const response = await api.get('/api/tareas/usuario');
    return response.data;
  },

  async create(tareaData) {
    const response = await api.post('/api/tareas', tareaData)
    return response.data
  },

  async completar(id) {
    const response = await api.put(`/api/tareas/${id}/completar`)
    return response.data
  },

  async tareaMasCercana(usuarioId) {
    const response = await api.get(`/api/tareas/cercana/${usuarioId}`)
    return response.data
  },

  async tareasPorSector(usuarioId) {
    const response = await api.get(`/api/tareas/por-sector/${usuarioId}`)
    return response.data
  },

  async sectorTopRadio(usuarioId, radioKm) {
    const response = await api.get(`/api/tareas/sector-top/${usuarioId}?radioKm=${radioKm}`)
    return response.data
  },

  async promedioDistancia(usuarioId) {
    const response = await api.get(`/api/tareas/promedio-distancia/${usuarioId}`)
    return response.data
  },

  async tareasPendientesPorSector() {
    const response = await api.get('/api/tareas/tareas-pendientes/sectores')
    return response.data
  },

  async tareaPendienteMasCercanaAUsuario(usuarioId) {
    const response = await api.get(`/api/tareas/cercanas/${usuarioId}`)
    return response.data
  },

  async tareasCompletadasPorUsuarioYSector() {
    const response = await api.get('/api/tareas/tareas-completadas/por-sector')
    return response.data
  },

  async editar(id, tareaData) {
  const response = await api.put(`/api/tareas/${id}/editar`, tareaData);
  return response.data;
  },

  async eliminar(id) {
    await api.delete(`/api/tareas/${id}/eliminar`);
  },

  async getTareaById(id) {
    const response = await api.get(`/api/tareas/${id}`);  
    return response.data;
  },
}

