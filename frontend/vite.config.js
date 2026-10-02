import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Бэкенд (Spring Boot) слушает 8080 - см. backend/find_trining/.env (SERVER_PORT).
      // Запросы фронтенда на /api/* в dev-режиме прозрачно уходят туда же, без CORS.
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
