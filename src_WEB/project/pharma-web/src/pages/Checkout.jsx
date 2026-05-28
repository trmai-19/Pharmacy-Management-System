import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { khachHangService, orderService } from '../services/api'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default function Checkout() {
  const navigate = useNavigate()
  const [cart, setCart] = useState([])
  const [profile, setProfile] = useState(null)
  const [diemSuDung, setDiemSuDung] = useState(0)
  const [ghiChu, setGhiChu] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    // 1. Kiểm tra giỏ hàng
    const cartStr = localStorage.getItem('cart')
    const parsedCart = cartStr ? JSON.parse(cartStr) : []
    if (parsedCart.length === 0) {
      navigate('/cart')
      return
    }
    setCart(parsedCart)

    // 2. Lấy thông tin khách hàng để xem điểm tích lũy
    khachHangService.getThongTin()
      .then(res => setProfile(res.data))
      .catch(err => {
        console.error('Lỗi lấy profile:', err)
        alert('Phiên làm việc hết hạn, vui lòng đăng nhập lại!')
        navigate('/login')
      })
      .finally(() => setLoading(false))
  }, [navigate])

  const tongTien = cart.reduce((sum, item) => sum + (item.giaBan * item.sl), 0)
  const diemHienCo = profile?.diemTichLuy || 0
  const maxDiemChoPhep = Math.floor(tongTien * 0.5)

  const handleDiemChange = (e) => {
    const val = parseInt(e.target.value) || 0
    if (val < 0) {
      setDiemSuDung(0)
      return
    }
    if (val > diemHienCo) {
      alert(`Bạn chỉ có tối đa ${diemHienCo} điểm tích lũy!`)
      setDiemSuDung(diemHienCo)
      return
    }
    if (val > maxDiemChoPhep) {
      alert(`Số điểm sử dụng tối đa là 50% giá trị đơn hàng (${maxDiemChoPhep} điểm)!`)
      setDiemSuDung(maxDiemChoPhep)
      return
    }
    setDiemSuDung(val)
  }

  const handleOrder = async () => {
    setSubmitting(true)
    try {
      const orderItems = cart.map(item => ({
        masp: item.maSP,
        sl: item.sl
      }))

      const body = {
        items: orderItems,
        diemSuDung: diemSuDung,
        ghiChu: ghiChu.trim()
      }

      const res = await orderService.createOrder(body)
      
      // Xóa giỏ hàng
      localStorage.removeItem('cart')
      window.dispatchEvent(new Event('cartUpdated'))

      alert(`Đặt hàng thành công! Mã đơn hàng: ${res.data.maHD}`)
      navigate('/orders')
    } catch (err) {
      console.error(err)
      alert(err.response?.data?.message || err.message || 'Có lỗi xảy ra trong quá trình đặt hàng!')
    } finally {
      setSubmitting(false)
    }
  }

  const tienThanhToan = tongTien - diemSuDung

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-gray-400">Đang tải thông tin thanh toán...</p>
    </div>
  )

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <div className="flex-1 max-w-4xl mx-auto w-full px-4 sm:px-6 py-8">
        <h1 className="text-2xl font-bold text-gray-800 mb-6 flex items-center gap-2">
          📝 Xác Nhận Đặt Trước Đơn Hàng
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Thông tin khách hàng & Giao nhận */}
          <div className="md:col-span-2 space-y-6">
            {/* Thông tin cá nhân */}
            <div className="bg-white rounded-2xl shadow p-6 border border-gray-100 space-y-4">
              <h3 className="text-base font-bold text-gray-800">👤 Thông tin người đặt hàng</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
                <div>
                  <label className="block text-xs text-gray-400 font-medium">Họ và tên</label>
                  <p className="mt-1 font-semibold text-gray-700">{profile?.tenKH}</p>
                </div>
                <div>
                  <label className="block text-xs text-gray-400 font-medium">Số điện thoại</label>
                  <p className="mt-1 font-semibold text-gray-700">{profile?.sdt}</p>
                </div>
              </div>
            </div>

            {/* Điểm tích lũy */}
            <div className="bg-white rounded-2xl shadow p-6 border border-gray-100 space-y-4">
              <div className="flex justify-between items-center">
                <h3 className="text-base font-bold text-gray-800">🪙 Sử dụng điểm tích lũy</h3>
                <span className="bg-amber-50 text-amber-700 text-xs font-semibold px-2.5 py-1 rounded-full">
                  Hiện có: {diemHienCo.toLocaleString('vi-VN')} điểm
                </span>
              </div>
              
              <p className="text-xs text-gray-400 leading-normal">
                Quy đổi: **1 điểm = 1 VNĐ** trừ trực tiếp vào đơn hàng. Bạn chỉ được dùng tối đa 50% giá trị đơn hàng (tối đa {maxDiemChoPhep.toLocaleString('vi-VN')} điểm).
              </p>

              <div className="flex items-center gap-3">
                <input
                  type="number"
                  value={diemSuDung || ''}
                  onChange={handleDiemChange}
                  placeholder="Nhập số điểm muốn dùng..."
                  disabled={diemHienCo === 0}
                  className="flex-1 max-w-xs px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm"
                />
                <button
                  type="button"
                  onClick={() => setDiemSuDung(Math.min(diemHienCo, maxDiemChoPhep))}
                  disabled={diemHienCo === 0}
                  className="bg-blue-50 hover:bg-blue-100 text-blue-600 px-4 py-2.5 rounded-xl text-xs font-semibold transition"
                >
                  Dùng tối đa
                </button>
              </div>
            </div>

            {/* Ghi chú nhận hàng */}
            <div className="bg-white rounded-2xl shadow p-6 border border-gray-100 space-y-4">
              <h3 className="text-base font-bold text-gray-800">📝 Ghi chú đơn hàng</h3>
              <textarea
                value={ghiChu}
                onChange={e => setGhiChu(e.target.value)}
                placeholder="Nhập ghi chú giao nhận, địa chỉ nhận hàng hoặc thời gian bạn muốn qua lấy thuốc..."
                rows="3"
                className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition"
              />
            </div>
          </div>

          {/* Tóm tắt & Nút đặt hàng */}
          <div className="bg-white rounded-2xl shadow border border-gray-100 p-6 self-start space-y-6">
            <h3 className="text-base font-bold text-gray-800">Chi tiết thanh toán</h3>

            <div className="space-y-4 text-sm divide-y divide-gray-100">
              <div className="space-y-2 pb-3">
                {cart.map(item => (
                  <div key={item.maSP} className="flex justify-between text-xs text-gray-500">
                    <span className="truncate max-w-[150px]">{item.tenSanPham}</span>
                    <span>x{item.sl}</span>
                  </div>
                ))}
              </div>

              <div className="space-y-2 pt-3">
                <div className="flex justify-between text-gray-500">
                  <span>Tổng tiền hàng:</span>
                  <span className="font-semibold text-gray-700">{tongTien.toLocaleString('vi-VN')}đ</span>
                </div>
                {diemSuDung > 0 && (
                  <div className="flex justify-between text-amber-600 font-medium">
                    <span>Điểm tích lũy sử dụng:</span>
                    <span>-{diemSuDung.toLocaleString('vi-VN')}đ</span>
                  </div>
                )}
                <hr className="border-gray-100" />
                <div className="flex justify-between text-base font-bold text-gray-800">
                  <span>Cần thanh toán:</span>
                  <span className="text-blue-600">{tienThanhToan.toLocaleString('vi-VN')}đ</span>
                </div>
              </div>
            </div>

            <button
              onClick={handleOrder}
              disabled={submitting}
              className={`w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-3 rounded-xl font-semibold shadow-md transition duration-300 text-sm text-center ${
                submitting ? 'opacity-70 cursor-not-allowed' : 'hover:from-blue-700 hover:to-indigo-700 shadow-blue-100'
              }`}
            >
              {submitting ? 'Đang xử lý đơn...' : 'Xác nhận đặt đơn trước'}
            </button>
            
            <p className="text-[11px] text-gray-400 text-center leading-normal">
              Bằng việc nhấn đặt trước, bạn đồng ý với chính sách giữ chỗ và bảo quản thuốc của Pharma.
            </p>
          </div>
        </div>
      </div>

      <Footer />
    </div>
  )
}
