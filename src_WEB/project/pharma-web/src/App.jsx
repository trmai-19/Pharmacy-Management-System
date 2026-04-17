import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Home from './pages/Home'
import DiemTichLuy from './pages/DiemTichLuy'
import Navbar from './components/Navbar'

// Bảo vệ route cần đăng nhập
const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token')
  return token ? children : <Navigate to="/login" />
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={
          <PrivateRoute>
            <Navbar />
            <Home />
          </PrivateRoute>
        } />
        <Route path="/diem" element={
          <PrivateRoute>
            <Navbar />
            <DiemTichLuy />
          </PrivateRoute>
        } />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App