import { useEffect, useState } from 'react'
import { trainingSlotsApi } from '../api/trainingSlots'
import ErrorBanner from '../components/ErrorBanner'
import CurrentUserPicker from '../components/CurrentUserPicker'

const dateTimeFormat = new Intl.DateTimeFormat('ru-RU', {
  weekday: 'short',
  day: '2-digit',
  month: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
})

function formatSlotTime(startsAt, endsAt) {
  const start = new Date(startsAt)
  const end = new Date(endsAt)
  const startLabel = dateTimeFormat.format(start)
  const endLabel = end.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
  return `${startLabel}–${endLabel}`
}

export default function MyBookingsPage() {
  const [userId, setUserId] = useState(null)
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [actingSlotId, setActingSlotId] = useState(null)

  const loadBookings = (id) => {
    if (!id) {
      setBookings([])
      return
    }
    setLoading(true)
    setError('')
    trainingSlotsApi
      .listByUser(id)
      .then(setBookings)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadBookings(userId)
  }, [userId])

  const upcoming = bookings.filter((b) => new Date(b.startsAt) >= new Date())
  const past = bookings.filter((b) => new Date(b.startsAt) < new Date())

  const handleCancel = async (slotId) => {
    if (!userId) return
    setActingSlotId(slotId)
    setError('')
    try {
      await trainingSlotsApi.cancelBooking(slotId, userId)
      loadBookings(userId)
    } catch (e) {
      setError(e.message)
    } finally {
      setActingSlotId(null)
    }
  }

  return (
    <section>
      <h1>Мои записи</h1>
      <p className="page-hint">
        Входа по паролю пока нет, поэтому выбери пользователя ниже, чтобы увидеть его записи на тренировки.
      </p>

      <ErrorBanner message={error} onClose={() => setError('')} />

      <CurrentUserPicker value={userId} onChange={setUserId} label="Чьи записи показать" />

      {!userId ? (
        <p className="empty">Выбери пользователя, чтобы увидеть его записи.</p>
      ) : loading ? (
        <p>Загрузка…</p>
      ) : (
        <>
          <h2>Предстоящие</h2>
          {upcoming.length === 0 ? (
            <p className="empty">Нет предстоящих записей.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Время</th>
                  <th>Зал</th>
                  <th>Тренер</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {upcoming.map((b) => (
                  <tr key={b.id}>
                    <td>{formatSlotTime(b.startsAt, b.endsAt)}</td>
                    <td>{b.gym.name}</td>
                    <td>{b.coach.fullName}</td>
                    <td className="data-table__actions">
                      <button
                        type="button"
                        className="btn-danger"
                        onClick={() => handleCancel(b.id)}
                        disabled={actingSlotId === b.id}
                      >
                        Отменить запись
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          <h2>Прошедшие</h2>
          {past.length === 0 ? (
            <p className="empty">Прошедших записей нет.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Время</th>
                  <th>Зал</th>
                  <th>Тренер</th>
                </tr>
              </thead>
              <tbody>
                {past.map((b) => (
                  <tr key={b.id}>
                    <td>{formatSlotTime(b.startsAt, b.endsAt)}</td>
                    <td>{b.gym.name}</td>
                    <td>{b.coach.fullName}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </>
      )}
    </section>
  )
}
