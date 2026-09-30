import { api } from './client'

export const gymsApi = {
  list: () => api.get('/gyms'),
  get: (id) => api.get(`/gyms/${id}`),
  create: (data) => api.post('/gyms', data),
  update: (id, data) => api.put(`/gyms/${id}`, data),
  remove: (id) => api.del(`/gyms/${id}`),
}
