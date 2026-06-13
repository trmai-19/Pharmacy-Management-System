import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default function Cart() {
  const navigate = useNavigate()
  const [cart, setCart] = useState([])

  useEffect(() => {
    const cartStr = localStorage.getItem('cart')
    if (cartStr) {
      setCart(JSON.parse(cartStr))
    }
  }, [])

  const saveCart = (newCart) => {
    setCart(newCart)
    localStorage.setItem('cart', JSON.stringify(newCart))
    window.dispatchEvent(new Event('cartUpdated'))
  }

  const updateQuantity = (index, val) => {
    const newCart = [...cart]
    newCart[index].sl = Math.max(1, newCart[index].sl + val)
    saveCart(newCart)
  }

  const removeItem = (index) => {
    const newCart = cart.filter((_, i) => i !== index)
    saveCart(newCart)
  }

  const clearCart = () => {
    if (window.confirm('Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
      saveCart([])
    }
  }

  const handleCheckout = () => {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('Vui lòng đăng nhập tài khoản trước khi thực hiện đặt hàng!')
      navigate('/login', { state: { from: '/checkout' } })
      return
    }
    navigate('/checkout')
  }

  const tongTien = cart.reduce((sum, item) => sum + (item.giaBan * item.sl), 0)

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <div className="flex-1 max-w-4xl mx-auto w-full px-4 sm:px-6 py-8">
        <h1 className="text-2xl font-bold text-gray-800 mb-6 flex items-center gap-2">
          🛒 Giỏ Hàng Của Bạn
        </h1>

        {cart.length === 0 ? (
          <div className="bg-white rounded-2xl shadow p-12 text-center space-y-4">
            <div className="w-20 h-20 bg-blue-50 text-blue-500 rounded-full flex items-center justify-center text-4xl mx-auto">
              🛒
            </div>
            <h3 className="text-lg font-bold text-gray-700">Giỏ hàng của bạn đang trống</h3>
            <p className="text-gray-400 text-sm">Hãy tìm kiếm thuốc và các sản phẩm y tế cần thiết rồi thêm vào giỏ.</p>
            <Link
              to="/san-pham"
              className="inline-block bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-xl font-medium transition"
            >
              Xem danh sách sản phẩm
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* List items */}
            <div className="md:col-span-2 space-y-4">
              <div className="bg-white rounded-2xl shadow overflow-hidden border border-gray-100">
                <div className="divide-y divide-gray-100">
                  {cart.map((item, index) => (
                    <div key={item.maSP} className="p-5 flex items-start gap-4">
                      {/* Icon */}
                      <div className="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center text-2xl flex-shrink-0">
                        💊
                      </div>

                      {/* Info */}
                      <div className="flex-1 min-w-0">
                        <h4 className="font-semibold text-gray-800 text-sm truncate hover:text-blue-600">
                          <Link to={`/san-pham/${item.maSP}`}>{item.tenSanPham}</Link>
                        </h4>
                        <p className="text-xs text-gray-400 mt-1">Đơn vị: {item.dvt || 'Chai/Hộp'}</p>
                        <p className="text-blue-600 font-bold text-sm mt-2">
                          {item.giaBan.toLocaleString('vi-VN')}đ
                        </p>
                      </div>

                      {/* Quantity Selector */}
                      <div className="flex flex-col items-end gap-3 flex-shrink-0">
                        <div className="flex items-center border border-gray-200 rounded-lg overflow-hidden bg-gray-50 text-xs">
                          <button
                            onClick={() => updateQuantity(index, -1)}
                            className="px-2.5 py-1 hover:bg-gray-200 transition font-bold text-gray-500"
                          >
                            -
                          </button>
                          <span className="px-3 py-1 font-semibold text-gray-700">{item.sl}</span>
                          <button
                            onClick={() => updateQuantity(index, 1)}
                            className="px-2.5 py-1 hover:bg-gray-200 transition font-bold text-gray-500"
                          >
                            +
                          </button>
                        </div>

                        <button
                          onClick={() => removeItem(index)}
                          className="text-red-500 hover:text-red-700 text-xs font-medium transition"
                        >
                          ✕ Xóa
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex justify-between items-center px-2">
                <button
                  onClick={clearCart}
                  className="text-gray-400 hover:text-red-500 text-xs font-medium transition"
                >
                  🗑️ Xóa sạch giỏ hàng
                </button>
                <Link
                  to="/san-pham"
                  className="text-blue-600 hover:underline text-xs font-medium"
                >
                  ← Tiếp tục mua thuốc
                </Link>
              </div>
            </div>

            {/* Summary */}
            <div className="bg-white rounded-2xl shadow border border-gray-100 p-6 self-start space-y-6">
              <h3 className="text-base font-bold text-gray-800">Tóm tắt đơn hàng</h3>
              
              <div className="space-y-3 text-sm">
                <div className="flex justify-between text-gray-500">
                  <span>Số lượng thuốc:</span>
                  <span className="font-semibold text-gray-700">
                    {cart.reduce((sum, item) => sum + item.sl, 0)} sản phẩm
                  </span>
                </div>
                <div className="flex justify-between text-gray-500">
                  <span>Tạm tính:</span>
                  <span className="font-semibold text-gray-700">{tongTien.toLocaleString('vi-VN')}đ</span>
                </div>
                <div className="flex justify-between text-gray-500">
                  <span>Phí giữ kho:</span>
                  <span className="text-green-600 font-semibold">Miễn phí</span>
                </div>
                <hr className="border-gray-100" />
                <div className="flex justify-between text-base font-bold text-gray-800">
                  <span>Tổng cộng:</span>
                  <span className="text-blue-600">{tongTien.toLocaleString('vi-VN')}đ</span>
                </div>
              </div>

              <button
                onClick={handleCheckout}
                className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white py-3 rounded-xl font-semibold shadow-md shadow-blue-100 transition duration-300 text-sm text-center"
              >
                Tiến hành đặt trước
              </button>
              
              <p className="text-[11px] text-gray-400 text-center leading-normal">
                (*) Thuốc đặt trước sẽ được giữ trong kho trong vòng 24 giờ. Vui lòng đến cửa hàng hoặc chuẩn bị thanh toán khi nhận thuốc.
              </p>
            </div>
          </div>
        )}
      </div>

      <Footer />
    </div>
  )
}
