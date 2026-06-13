import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'

export default function Navbar() {
  const navigate = useNavigate()
  const tenKH = localStorage.getItem('tenKH')
  const token = localStorage.getItem('token')
  const [searchQuery, setSearchQuery] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)
  const [cartCount, setCartCount] = useState(0)

  const getCartCount = () => {
    const cartStr = localStorage.getItem('cart')
    if (cartStr) {
      const cart = JSON.parse(cartStr)
      return cart.reduce((sum, item) => sum + item.sl, 0)
    }
    return 0
  }

  useEffect(() => {
    setCartCount(getCartCount())

    const handleCartUpdate = () => {
      setCartCount(getCartCount())
    }

    window.addEventListener('cartUpdated', handleCartUpdate)
    return () => {
      window.removeEventListener('cartUpdated', handleCartUpdate)
    }
  }, [])

  const handleLogout = () => {
    localStorage.clear()
    navigate('/login')
  }

  const handleSearch = (e) => {
    e.preventDefault()
    if (searchQuery.trim()) {
      navigate(`/tim-kiem?q=${encodeURIComponent(searchQuery.trim())}`)
      setSearchQuery('')
    }
  }

  return (
    <nav className="bg-blue-600 text-white shadow-md">
      <div className="flex items-center justify-between px-6 py-3 gap-4">
        {/* Logo */}
        <Link to="/" className="text-xl font-bold whitespace-nowrap flex items-center gap-1">
          💊 Pharma
        </Link>

        {/* Search bar */}
        <form onSubmit={handleSearch} className="flex-1 max-w-md">
          <div className="flex items-center bg-white rounded-xl overflow-hidden">
            <input
              type="text"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              placeholder="Tìm thuốc, công dụng..."
              className="flex-1 px-4 py-2 text-gray-700 text-sm focus:outline-none"
            />
            <button
              type="submit"
              className="bg-blue-500 hover:bg-blue-400 px-3 py-2 transition text-white"
            >
              🔍
            </button>
          </div>
        </form>

        {/* Desktop nav links */}
        <div className="hidden lg:flex items-center gap-5 text-sm">
          <Link to="/" className="hover:text-blue-200 transition">Trang chủ</Link>
          <Link to="/san-pham" className="hover:text-blue-200 transition">Sản phẩm</Link>
          {token && <Link to="/diem" className="hover:text-blue-200 transition">Điểm tích lũy</Link>}
          {token && <Link to="/orders" className="hover:text-blue-200 transition">Lịch sử đơn hàng</Link>}
          <Link to="/chinh-sach-doi-tra" className="hover:text-blue-200 transition whitespace-nowrap">
            Chính sách đổi trả
          </Link>
          
          {/* Giỏ hàng */}
          <Link to="/cart" className="relative hover:text-blue-200 transition flex items-center gap-1 font-medium bg-blue-700 px-3 py-1.5 rounded-xl">
            <span>🛒 Giỏ hàng</span>
            {cartCount > 0 && (
              <span className="bg-red-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full min-w-[16px] text-center">
                {cartCount}
              </span>
            )}
          </Link>

          {token ? (
            <>
              <Link to="/profile" className="text-blue-100 hover:text-white transition whitespace-nowrap font-medium flex items-center gap-1">
                👤 {tenKH}
              </Link>
              <button
                onClick={handleLogout}
                className="bg-white text-blue-600 px-3 py-1 rounded-lg text-sm font-medium hover:bg-blue-50 transition"
              >
                Đăng xuất
              </button>
            </>
          ) : (
            <Link
              to="/login"
              className="bg-white text-blue-600 px-3.5 py-1.5 rounded-lg text-sm font-medium hover:bg-blue-50 transition"
            >
              Đăng nhập
            </Link>
          )}
        </div>

        {/* Mobile menu toggle */}
        <button
          className="lg:hidden text-white text-xl"
          onClick={() => setMenuOpen(!menuOpen)}
        >
          ☰
        </button>
      </div>

      {/* Mobile dropdown */}
      {menuOpen && (
        <div className="lg:hidden bg-blue-700 px-6 py-3 space-y-2 text-sm border-t border-blue-500">
          <Link to="/" className="block hover:text-blue-200" onClick={() => setMenuOpen(false)}>Trang chủ</Link>
          <Link to="/san-pham" className="block hover:text-blue-200" onClick={() => setMenuOpen(false)}>Sản phẩm</Link>
          {token && <Link to="/diem" className="block hover:text-blue-200" onClick={() => setMenuOpen(false)}>Điểm tích lũy</Link>}
          {token && <Link to="/orders" className="block hover:text-blue-200" onClick={() => setMenuOpen(false)}>Lịch sử đơn hàng</Link>}
          <Link to="/chinh-sach-doi-tra" className="block hover:text-blue-200" onClick={() => setMenuOpen(false)}>Chính sách đổi trả</Link>
          
          <Link to="/cart" className="block hover:text-blue-200 font-medium" onClick={() => setMenuOpen(false)}>
            🛒 Giỏ hàng ({cartCount})
          </Link>

          {token ? (
            <>
              <Link to="/profile" className="block hover:text-blue-200 font-medium" onClick={() => setMenuOpen(false)}>👤 Thông tin cá nhân ({tenKH})</Link>
              <button onClick={handleLogout} className="block text-left text-red-300 hover:text-red-200 w-full">Đăng xuất</button>
            </>
          ) : (
            <Link to="/login" className="block hover:text-blue-200 font-medium" onClick={() => setMenuOpen(false)}>Đăng nhập</Link>
          )}
        </div>
      )}
    </nav>
  )
}