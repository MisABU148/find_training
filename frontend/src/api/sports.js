import { api } from './client'

// Sport: бэкенд отдаёт только list/create/delete (без update) - см. SportController.
export const sportsApi = {
  list: () => api.get('/sports'),
  create: (data) => api.post('/sports', data),
  remove: (id) => api.del(`/sports/${id}`),
}
