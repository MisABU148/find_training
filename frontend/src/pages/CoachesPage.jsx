import { useEffect, useState } from 'react'
import { coachesApi } from '../api/coaches'
import { gymsApi } from '../api/gyms'
import { sportsApi } from '../api/sports'
import ErrorBanner from '../components/ErrorBanner'
import FieldError from '../components/FieldError'

const emptyForm = {
  fullName: '',
  email: '',
  phone: '',
  bio: '',
  experienceYears: '0',
  gymId: '',
  sportIds: [],
}

export default function CoachesPage() {
  const [coaches, setCoaches] = useState([])
  const [gyms, setGyms] = useState([])
  const [sports, setSports] = useState([])
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
      const [coachesData, gymsData, sportsData] = await Promise.all([
        coachesApi.list(),
        gymsApi.list(),
        sportsApi.list(),
      ])
      setCoaches(coachesData)
      setGyms(gymsData)
      setSports(sportsData)
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

  const startEdit = (coach) => {
    setEditingId(coach.id)
    setForm({
      fullName: coach.fullName,
      email: coach.email,
      phone: coach.phone ?? '',
      bio: coach.bio ?? '',
      experienceYears: String(coach.experienceYears ?? 0),
      gymId: coach.gym ? String(coach.gym.id) : '',
      sportIds: coach.sports ? coach.sports.map((s) => s.id) : [],
    })
    setFieldErrors(null)
  }

  const handleChange = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const toggleSport = (sportId) => {
    setForm((prev) => {
      const has = prev.sportIds.includes(sportId)
      return {
        ...prev,
        sportIds: has ? prev.sportIds.filter((id) => id !== sportId) : [...prev.sportIds, sportId],
      }
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    setFieldErrors(null)
    const payload = {
      fullName: form.fullName,
      email: form.email,
      phone: form.phone || null,
      bio: form.bio || null,
      experienceYears: form.experienceYears === '' ? 0 : Number(form.experienceYears),
      gymId: form.gymId ? Number(form.gymId) : null,
      sportIds: form.sportIds,
    }
    try {
      if (editingId) {
        await coachesApi.update(editingId, payload)
      } else {
        await coachesApi.create(payload)
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
    if (!window.confirm('Удалить этого тренера?')) return
    setError('')
    try {
      await coachesApi.remove(id)
      if (editingId === id) startCreate()
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <section>
      <h1>Тренеры</h1>

      <ErrorBanner message={error} onClose={() => setError('')} />

      <form className="card-form" onSubmit={handleSubmit}>
        <h2>{editingId ? 'Редактировать тренера' : 'Новый тренер'}</h2>

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
            <label>Телефон</label>
            <input type="text" value={form.phone} onChange={handleChange('phone')} placeholder="+79990001122" />
            <FieldError fieldErrors={fieldErrors} name="phone" />
          </div>
          <div className="field">
            <label>Стаж (лет)</label>
            <input
              type="number"
              min="0"
              max="60"
              value={form.experienceYears}
              onChange={handleChange('experienceYears')}
            />
            <FieldError fieldErrors={fieldErrors} name="experienceYears" />
          </div>
        </div>

        <div className="field">
          <label>О тренере</label>
          <textarea rows={3} value={form.bio} onChange={handleChange('bio')} />
          <FieldError fieldErrors={fieldErrors} name="bio" />
        </div>

        <div className="field">
          <label>Зал</label>
          <select value={form.gymId} onChange={handleChange('gymId')}>
            <option value="">Без зала</option>
            {gyms.map((gym) => (
              <option key={gym.id} value={gym.id}>
                {gym.name}
              </option>
            ))}
          </select>
        </div>

        <div className="field">
          <label>Виды спорта</label>
          {sports.length === 0 ? (
            <p className="empty">Сначала добавь виды спорта на соответствующей странице.</p>
          ) : (
            <div className="checkbox-group">
              {sports.map((sport) => (
                <label key={sport.id} className="checkbox-item">
                  <input
                    type="checkbox"
                    checked={form.sportIds.includes(sport.id)}
                    onChange={() => toggleSport(sport.id)}
                  />
                  {sport.name}
                </label>
              ))}
            </div>
          )}
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
      ) : coaches.length === 0 ? (
        <p className="empty">Пока нет ни одного тренера.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Имя</th>
              <th>Email</th>
              <th>Зал</th>
              <th>Виды спорта</th>
              <th>Стаж</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {coaches.map((coach) => (
              <tr key={coach.id}>
                <td>{coach.fullName}</td>
                <td>{coach.email}</td>
                <td>{coach.gym ? coach.gym.name : '—'}</td>
                <td>{coach.sports?.length ? coach.sports.map((s) => s.name).join(', ') : '—'}</td>
                <td>{coach.experienceYears}</td>
                <td className="data-table__actions">
                  <button type="button" onClick={() => startEdit(coach)}>
                    Изменить
                  </button>
                  <button type="button" className="btn-danger" onClick={() => handleDelete(coach.id)}>
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
