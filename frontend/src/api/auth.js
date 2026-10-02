import { api } from './client'

export const authApi = {
  register: (data) => api.post('/auth/register', data),
}
