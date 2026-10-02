import { Navigate, Route, Routes } from 'react-router-dom'
import NavBar from './components/NavBar'
import GymsPage from './pages/GymsPage'
import CoachesPage from './pages/CoachesPage'
import SportsPage from './pages/SportsPage'
import UsersPage from './pages/UsersPage'
import RegisterPage from './pages/RegisterPage'
import BookingPage from './pages/BookingPage'
import MyBookingsPage from './pages/MyBookingsPage'
import './App.css'

function App() {
  return (
    <div className="app">
      <NavBar />
      <main className="app__content">
        <Routes>
          <Route path="/" element={<Navigate to="/gyms" replace />} />
          <Route path="/gyms" element={<GymsPage />} />
          <Route path="/coaches" element={<CoachesPage />} />
          <Route path="/sports" element={<SportsPage />} />
          <Route path="/users" element={<UsersPage />} />
          <Route path="/booking" element={<BookingPage />} />
          <Route path="/my-bookings" element={<MyBookingsPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
