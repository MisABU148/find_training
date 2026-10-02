import { api } from './client'

export const trainingSlotsApi = {
  listByGym: (gymId) => api.get(`/training-slots?gymId=${gymId}`),
  listByUser: (userId) => api.get(`/training-slots?bookedByUserId=${userId}`),
  create: (data) => api.post('/training-slots', data),
  book: (id, userId) => api.post(`/training-slots/${id}/book`, { userId }),
  cancelBooking: (id, userId) => api.del(`/training-slots/${id}/book?userId=${userId}`),
  remove: (id) => api.del(`/training-slots/${id}`),
}
