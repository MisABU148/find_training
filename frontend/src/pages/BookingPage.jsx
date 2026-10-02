import { useEffect, useState } from 'react'
import { gymsApi } from '../api/gyms'
import { coachesApi } from '../api/coaches'
import { trainingSlotsApi } from '../api/trainingSlots'
import ErrorBanner from '../components/ErrorBanner'
import FieldError from '../components/FieldError'
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

const emptyRentForm = { coachId: '', startsAt: '' }

export default function BookingPage() {
  const [gyms, setGyms] = useState([])
  const [coaches, setCoaches] = useState([])
  const [gymId, setGymId] = useState('')
  const [slots, setSlots] = useState([])
  const [currentUserId, setCurrentUserId] = useState(null)

  const [loadingSlots, setLoadingSlots] = useState(false)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState(null)
  const [rentForm, setRentForm] = useState(emptyRentForm)
  const [submitting, setSubmitting] = useState(false)
  const [actingSlotId, setActingSlotId] = useState(null)

  useEffect(() => {
    Promise.all([gymsApi.list(), coachesApi.list()])
      .then(([gymsData, coachesData]) => {
        setGyms(gymsData)
        setCoaches(coachesData)
        if (gymsData.length > 0) setGymId(String(gymsData[0].id))
      })
      .catch((e) => setError(e.message))
  }, [])

  const loadSlots = (id) => {
    if (!id) {
      setSlots([])
      return
    }
    setLoadingSlots(true)
    setError('')
    trainingSlotsApi
      .listByGym(id)
      .then(setSlots)
      .catch((e) => setError(e.message))
      .finally(() => setLoadingSlots(false))
  }

  useEffect(() => {
    loadSlots(gymId)
  }, [gymId])

  const handleRentChange = (field) => (e) => setRentForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleRentSubmit = async (e) => {
    e.preventDefault()
    if (!gymId) {
      setError('Сначала выбери зал')
      return
    }
    setSubmitting(true)
    setError('')
    setFieldErrors(null)
    try {
      await trainingSlotsApi.create({
        gymId: Number(gymId),
        coachId: Number(rentForm.coachId),
        // datetime-local отдаёт "YYYY-MM-DDTHH:mm" - бэкенду этого достаточно (секунды необязательны)
        startsAt: rentForm.startsAt,
      })
      setRentForm(emptyRentForm)
      loadSlots(gymId)
    } catch (e) {
      setError(e.message)
      setFieldErrors(e.fieldErrors)
    } finally {
      setSubmitting(false)
    }
  }

  const handleBook = async (slotId) => {
    if (!currentUserId) {
      setError('Сначала выбери, от чьего имени записываться (поле «Я»)')
      return
    }
    setActingSlotId(slotId)
    setError('')
    try {
      await trainingSlotsApi.book(slotId, currentUserId)
      loadSlots(gymId)
    } catch (e) {
      setError(e.message)
    } finally {
      setActingSlotId(null)
    }
  }

  const handleCancel = async (slotId) => {
    if (!currentUserId) return
    setActingSlotId(slotId)
    setError('')
    try {
      await trainingSlotsApi.cancelBooking(slotId, currentUserId)
      loadSlots(gymId)
    } catch (e) {
      setError(e.message)
    } finally {
      setActingSlotId(null)
    }
  }

  return (
    <section>
      <h1>Запись на тренировку</h1>
      <p className="page-hint">
        Слоты — пн-пт, с 10:00 до 23:00, по часу. Тренер арендует зал на конкретный час, пользователи
        записываются на свободные слоты. Входа по паролю ещё нет, поэтому ниже нужно выбрать, «от чьего лица»
        вы действуете.
      </p>

      <ErrorBanner message={error} onClose={() => setError('')} />

      <form className="card-form" onSubmit={handleRentSubmit}>
        <h2>Тренер арендует зал</h2>

        <div className="field-row">
          <div className="field">
            <label>Зал</label>
            <select value={gymId} onChange={(e) => setGymId(e.target.value)} required>
              <option value="" disabled>
                — выбери зал —
              </option>
              {gyms.map((gym) => (
                <option key={gym.id} value={gym.id}>
                  {gym.name} ({gym.city})
                </option>
              ))}
            </select>
          </div>

          <div className="field">
            <label>Тренер</label>
            <select value={rentForm.coachId} onChange={handleRentChange('coachId')} required>
              <option value="" disabled>
                — выбери тренера —
              </option>
              {coaches.map((coach) => (
                <option key={coach.id} value={coach.id}>
                  {coach.fullName}
                </option>
              ))}
            </select>
            <FieldError fieldErrors={fieldErrors} name="coachId" />
          </div>

          <div className="field">
            <label>Дата и время</label>
            <input
              type="datetime-local"
              value={rentForm.startsAt}
              onChange={handleRentChange('startsAt')}
              required
            />
            <FieldError fieldErrors={fieldErrors} name="startsAt" />
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" disabled={submitting}>
            Забронировать час для тренера
          </button>
        </div>
      </form>

      <CurrentUserPicker value={currentUserId} onChange={setCurrentUserId} label="Я записываюсь как" />

      <h2>Расписание {gyms.find((g) => String(g.id) === gymId)?.name ?? ''}</h2>

      {loadingSlots ? (
        <p>Загрузка…</p>
      ) : slots.length === 0 ? (
        <p className="empty">В этом зале пока нет ни одного слота - арендуй час тренером выше.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Время</th>
              <th>Тренер</th>
              <th>Статус</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {slots.map((slot) => {
              const isPast = new Date(slot.startsAt) < new Date()
              const isMine = slot.bookedBy && currentUserId && slot.bookedBy.id === currentUserId
              return (
                <tr key={slot.id}>
                  <td>{formatSlotTime(slot.startsAt, slot.endsAt)}</td>
                  <td>{slot.coach.fullName}</td>
                  <td>
                    {isPast
                      ? 'Прошло'
                      : slot.bookedBy
                        ? isMine
                          ? 'Ваша запись'
                          : 'Занято'
                        : 'Свободно'}
                  </td>
                  <td className="data-table__actions">
                    {!isPast && !slot.bookedBy && (
                      <button type="button" onClick={() => handleBook(slot.id)} disabled={actingSlotId === slot.id}>
                        Записаться
                      </button>
                    )}
                    {!isPast && isMine && (
                      <button
                        type="button"
                        className="btn-danger"
                        onClick={() => handleCancel(slot.id)}
                        disabled={actingSlotId === slot.id}
                      >
                        Отменить
                      </button>
                    )}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}
    </section>
  )
}
