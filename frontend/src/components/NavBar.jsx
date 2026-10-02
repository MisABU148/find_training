import { NavLink } from 'react-router-dom'

const links = [
  { to: '/gyms', label: 'Залы' },
  { to: '/coaches', label: 'Тренеры' },
  { to: '/sports', label: 'Виды спорта' },
  { to: '/users', label: 'Пользователи' },
  { to: '/booking', label: 'Запись на тренировку' },
  { to: '/my-bookings', label: 'Мои записи' },
  { to: '/register', label: 'Регистрация' },
]

export default function NavBar() {
  return (
    <nav className="navbar">
      <span className="navbar__brand">Find Training</span>
      <div className="navbar__links">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => 'navbar__link' + (isActive ? ' navbar__link--active' : '')}
          >
            {link.label}
          </NavLink>
        ))}
      </div>
    </nav>
  )
}
