import { api } from './client'

export const coachesApi = {
  list: () => api.get('/coaches'),
  get: (id) => api.get(`/coaches/${id}`),
  create: (data) => api.post('/coaches', data),
  update: (id, data) => api.put(`/coaches/${id}`, data),
  remove: (id) => api.del(`/coaches/${id}`),
}
