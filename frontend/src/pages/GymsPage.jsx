import { useEffect, useState } from 'react'
import { gymsApi } from '../api/gyms'
import ErrorBanner from '../components/ErrorBanner'
import FieldError from '../components/FieldError'

const emptyForm = { name: '', address: '', city: '', phone: '', description: '' }

export default function GymsPage() {
  const [gyms, setGyms] = useState([])
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
      setGyms(await gymsApi.list())
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

  const startEdit = (gym) => {
    setEditingId(gym.id)
    setForm({
      name: gym.name,
      address: gym.address,
      city: gym.city,
      phone: gym.phone ?? '',
      description: gym.description ?? '',
    })
    setFieldErrors(null)
  }

  const handleChange = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    setFieldErrors(null)
    const payload = {
      name: form.name,
      address: form.address,
      city: form.city,
      phone: form.phone || null,
      description: form.description || null,
    }
    try {
      if (editingId) {
        await gymsApi.update(editingId, payload)
      } else {
        await gymsApi.create(payload)
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
    if (!window.confirm('Удалить этот зал?')) return
    setError('')
    try {
      await gymsApi.remove(id)
      if (editingId === id) startCreate()
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <section>
      <h1>Залы</h1>

      <ErrorBanner message={error} onClose={() => setError('')} />

      <form className="card-form" onSubmit={handleSubmit}>
        <h2>{editingId ? 'Редактировать зал' : 'Новый зал'}</h2>

        <div className="field">
          <label>Название</label>
          <input type="text" value={form.name} onChange={handleChange('name')} required />
          <FieldError fieldErrors={fieldErrors} name="name" />
        </div>

        <div className="field-row">
          <div className="field">
            <label>Город</label>
            <input type="text" value={form.city} onChange={handleChange('city')} required />
            <FieldError fieldErrors={fieldErrors} name="city" />
          </div>
          <div className="field">
            <label>Телефон</label>
            <input type="text" value={form.phone} onChange={handleChange('phone')} placeholder="+79990001122" />
            <FieldError fieldErrors={fieldErrors} name="phone" />
          </div>
        </div>

        <div className="field">
          <label>Адрес</label>
          <input type="text" value={form.address} onChange={handleChange('address')} required />
          <FieldError fieldErrors={fieldErrors} name="address" />
        </div>

        <div className="field">
          <label>Описание</label>
          <textarea rows={3} value={form.description} onChange={handleChange('description')} />
          <FieldError fieldErrors={fieldErrors} name="description" />
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
      ) : gyms.length === 0 ? (
        <p className="empty">Пока нет ни одного зала.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Название</th>
              <th>Город</th>
              <th>Адрес</th>
              <th>Телефон</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {gyms.map((gym) => (
              <tr key={gym.id}>
                <td>{gym.name}</td>
                <td>{gym.city}</td>
                <td>{gym.address}</td>
                <td>{gym.phone || '—'}</td>
                <td className="data-table__actions">
                  <button type="button" onClick={() => startEdit(gym)}>
                    Изменить
                  </button>
                  <button type="button" className="btn-danger" onClick={() => handleDelete(gym.id)}>
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
