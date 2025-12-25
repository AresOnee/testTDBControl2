import api from './api'

export const sectorService = {
  async getAll() {
    const response = await api.get('/api/sectores')
    return response.data
  },

  async create(sectorData) {
    const response = await api.post('/api/sectores', sectorData)
    return response.data
  }
}
