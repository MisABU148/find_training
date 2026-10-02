import { useEffect, useState } from 'react'
import { usersApi } from '../api/users'
import ErrorBanner from '../components/ErrorBanner'
import FieldError from '../components/FieldError'

const ROLES = ['USER', 'COACH', 'ADMIN']

const emptyForm = { fullName: '', email: '', password: '', phone: '', role: 'USER' }

export default function UsersPage() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      setUsers(await usersApi.list())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const startCreate = () => {
    setEditingId(null)
    setForm(emptyForm)
    setFieldErrors(null)
  }

  const startEdit = (user) => {
    setEditingId(user.id)
    setForm({
      fullName: user.fullName,
      email: user.email,
      password: '',
      phone: user.phone ?? '',
      role: user.role,
    })
    setFieldErrors(null)
  }

  const handleChange = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    setFieldErrors(null)
    try {
      if (editingId) {
        // Пустой пароль при редактировании = "не менять" (см. UserUpdateRequest на бэкенде).
        const payload = {
          fullName: form.fullName,
          email: form.email,
          phone: form.phone || null,
          role: form.role,
          ...(form.password ? { password: form.password } : {}),
        }
        await usersApi.update(editingId, payload)
      } else {
        await usersApi.create({
          fullName: form.fullName,
          email: form.email,
          password: form.password,
          phone: form.phone || null,
          role: form.role,
        })
      }
      startCreate()
      await load()
    } catch (e) {
      setError(e.message)
      setFieldErrors(e.fieldErrors)
    } finally {
      setSubmitting(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Удалить этого пользователя?')) return
    setError('')
    try {
      await usersApi.remove(id)
      if (editingId === id) startCreate()
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <section>
      <h1>Пользователи</h1>
      <p className="page-hint">
        Это админский CRUD (можно назначить любую роль). Для обычной саморегистрации есть отдельная страница
        «Регистрация».
      </p>

      <ErrorBanner message={error} onClose={() => setError('')} />

      <form className="card-form" onSubmit={handleSubmit}>
        <h2>{editingId ? 'Редактировать пользователя' : 'Новый пользователь'}</h2>

        <div className="field-row">
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
        </div>

        <div className="field-row">
          <div className="field">
            <label>{editingId ? 'Новый пароль (необязательно)' : 'Пароль'}</label>
            <input
              type="password"
              value={form.password}
              onChange={handleChange('password')}
              placeholder={editingId ? 'Оставь пустым, чтобы не менять' : undefined}
              required={!editingId}
              minLength={8}
            />
            <FieldError fieldErrors={fieldErrors} name="password" />
          </div>
          <div className="field">
            <label>Телефон</label>
            <input type="text" value={form.phone} onChange={handleChange('phone')} placeholder="+79990001122" />
            <FieldError fieldErrors={fieldErrors} name="phone" />
          </div>
        </div>

        <div className="field">
          <label>Роль</label>
          <select value={form.role} onChange={handleChange('role')}>
            {ROLES.map((role) => (
              <option key={role} value={role}>
                {role}
              </option>
            ))}
          </select>
        </div>

        <div className="form-actions">
          <button type="submit" disabled={submitting}>
            {editingId ? 'Сохранить' : 'Добавить'}
          </button>
          {editingId && (
            <button type="button" className="btn-secondary" onClick={startCreate}>
              Отмена
            </button>
          )}
        </div>
      </form>

      {loading ? (
        <p>Загрузка…</p>
      ) : users.length === 0 ? (
        <p className="empty">Пока нет ни одного пользователя.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Имя</th>
              <th>Email</th>
              <th>Телефон</th>
              <th>Роль</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.fullName}</td>
                <td>{user.email}</td>
                <td>{user.phone || '—'}</td>
                <td>{user.role}</td>
                <td className="data-table__actions">
                  <button type="button" onClick={() => startEdit(user)}>
                    Изменить
                  </button>
                  <button type="button" className="btn-danger" onClick={() => handleDelete(user.id)}>
                    Удалить
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}
