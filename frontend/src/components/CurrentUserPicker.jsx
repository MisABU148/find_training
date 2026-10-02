import { useEffect, useState } from 'react'
import { usersApi } from '../api/users'

/**
 * Заглушка вместо полноценного входа (JWT-авторизация ещё не подключена):
 * позволяет выбрать, от чьего имени пользователя действовать в UI.
 */
export default function CurrentUserPicker({ value, onChange, label = 'Я (вместо входа)' }) {
  const [users, setUsers] = useState([])

  useEffect(() => {
    usersApi.list().then(setUsers).catch(() => {})
  }, [])

  return (
    <div className="field">
      <label>{label}</label>
      <select
        value={value ?? ''}
        onChange={(e) => onChange(e.target.value ? Number(e.target.value) : null)}
      >
        <option value="">— выбери пользователя —</option>
        {users.map((u) => (
          <option key={u.id} value={u.id}>
            {u.fullName} ({u.email})
          </option>
        ))}
      </select>
    </div>
  )
}
