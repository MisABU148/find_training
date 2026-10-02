import { useEffect, useState } from 'react'
import { sportsApi } from '../api/sports'
import ErrorBanner from '../components/ErrorBanner'
import FieldError from '../components/FieldError'

export default function SportsPage() {
  const [sports, setSports] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState(null)
  const [name, setName] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      setSports(await sportsApi.list())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    setFieldErrors(null)
    try {
      await sportsApi.create({ name })
      setName('')
      await load()
    } catch (e) {
      setError(e.message)
      setFieldErrors(e.fieldErrors)
    } finally {
      setSubmitting(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Удалить этот вид спорта?')) return
    setError('')
    try {
      await sportsApi.remove(id)
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <section>
      <h1>Виды спорта</h1>
      <p className="page-hint">Здесь можно только добавлять и удалять - без редактирования.</p>

      <ErrorBanner message={error} onClose={() => setError('')} />

      <form className="inline-form" onSubmit={handleSubmit}>
        <div className="field">
          <input
            type="text"
            placeholder="Например, Йога"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <FieldError fieldErrors={fieldErrors} name="name" />
        </div>
        <button type="submit" disabled={submitting}>
          Добавить
        </button>
      </form>

      {loading ? (
        <p>Загрузка…</p>
      ) : sports.length === 0 ? (
        <p className="empty">Пока нет ни одного вида спорта.</p>
      ) : (
        <ul className="chip-list">
          {sports.map((sport) => (
            <li key={sport.id} className="chip">
              <span>{sport.name}</span>
              <button type="button" className="chip__remove" onClick={() => handleDelete(sport.id)} aria-label="Удалить">
                ×
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
