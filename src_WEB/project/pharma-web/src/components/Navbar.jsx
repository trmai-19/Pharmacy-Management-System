import { Link, useNavigate } from 'react-router-dom'

export default function Navbar() {
  const navigate = useNavigate()
  const tenKH = localStorage.getItem('tenKH')

  const handleLogout = () => {
    localStorage.clear()
    navigate('/login')
  }

  return (
    <nav className="bg-blue-600 text-white px-6 py-4 flex justify-between items-center shadow-md">
      <div className="flex items-center gap-2">
        <span className="text-xl font-bold">💊 Pharma</span>
      </div>
      <div className="flex items-center gap-6">
        <Link to="/" className="hover:text-blue-200 transition">Trang chủ</Link>
        <Link to="/diem" className="hover:text-blue-200 transition">Điểm tích lũy</Link>
        <span className="text-blue-200">Xin chào, {tenKH}</span>
        <button
          onClick={handleLogout}
          className="bg-white text-blue-600 px-3 py-1 rounded-lg text-sm font-medium hover:bg-blue-50 transition"
        >
          Đăng xuất
        </button>
      </div>
    </nav>
  )
}