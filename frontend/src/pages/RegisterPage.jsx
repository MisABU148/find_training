import { useState } from 'react'
import { authApi } from '../api/auth'
import ErrorBanner from '../components/ErrorBanner'
import FieldError from '../components/FieldError'

const emptyForm = { fullName: '', email: '', password: '', phone: '' }

export default function RegisterPage() {
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [registered, setRegistered] = useState(null)

  const handleChange = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    setFieldErrors(null)
    try {
      const user = await authApi.register({
        fullName: form.fullName,
        email: form.email,
        password: form.password,
        phone: form.phone || null,
      })
      setRegistered(user)
      setForm(emptyForm)
    } catch (e) {
      setError(e.message)
      setFieldErrors(e.fieldErrors)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <h1>Регистрация</h1>
      <p className="page-hint">
        Публичная саморегистрация - роль всегда «USER», без выбора. Вход по паролю (JWT) появится позже.
      </p>

      <ErrorBanner message={error} onClose={() => setError('')} />

      {registered ? (
        <div className="card-form">
          <h2>Готово!</h2>
          <p>
            Аккаунт создан: <strong>{registered.fullName}</strong> ({registered.email}), роль {registered.role}.
          </p>
          <button type="button" onClick={() => setRegistered(null)}>
            Зарегистрировать ещё одного
          </button>
        </div>
      ) : (
        <form className="card-form" onSubmit={handleSubmit}>
          <div className="field">
            <label>Имя</label>
            <input type="text" value={form.fullName} onChange={handleChange('fullName')} required />
            <FieldError fieldErrors={fieldErrors} name="fullName" />
          </div>

          <div className="field">
            <label>Email</label>
            <input type="email" value={form.email} onChange={handleChange('email')} required />
            <FieldError fieldErrors={fieldErrors} name="email" />
          </div>

          <div className="field">
            <label>Пароль</label>
            <input
              type="password"
              value={form.password}
              onChange={handleChange('password')}
              required
              minLength={8}
            />
            <FieldError fieldErrors={fieldErrors} name="password" />
          </div>

          <div className="field">
            <label>Телефон (необязательно)</label>
            <input type="text" value={form.phone} onChange={handleChange('phone')} placeholder="+79990001122" />
            <FieldError fieldErrors={fieldErrors} name="phone" />
          </div>

          <div className="form-actions">
            <button type="submit" disabled={submitting}>
              Зарегистрироваться
            </button>
          </div>
        </form>
      )}
    </section>
  )
}
