const BASE_URL = '/api'

/**
 * Тонкая обёртка над fetch, заточенная под формат ошибок бэкенда
 * (m.ilyina.find_trining.exception.ApiError):
 * { timestamp, status, error, message, path, fieldErrors }
 *
 * При не-2xx ответе бросает Error с полями:
 *   - message: человекочитаемое сообщение от сервера
 *   - status: HTTP-статус
 *   - fieldErrors: { имяПоля: текстОшибки } | null - для отображения под инпутами
 */
async function request(path, { method = 'GET', body } = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method,
    headers: body !== undefined ? { 'Content-Type': 'application/json' } : undefined,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (res.status === 204) {
    return null
  }

  const text = await res.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = null
    }
  }

  if (!res.ok) {
    const error = new Error(data?.message || `Ошибка запроса (${res.status})`)
    error.status = res.status
    error.fieldErrors = data?.fieldErrors ?? null
    error.apiError = data
    throw error
  }

  return data
}

export const api = {
  get: (path) => request(path, { method: 'GET' }),
  post: (path, body) => request(path, { method: 'POST', body }),
  put: (path, body) => request(path, { method: 'PUT', body }),
  del: (path) => request(path, { method: 'DELETE' }),
}
