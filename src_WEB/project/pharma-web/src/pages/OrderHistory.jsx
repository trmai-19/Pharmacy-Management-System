import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { orderService } from '../services/api'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default function OrderHistory() {
  const navigate = useNavigate()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [expandedOrder, setExpandedOrder] = useState(null)

  const fetchOrders = () => {
    setLoading(true)
    orderService.getMyOrders()
      .then(res => setOrders(res.data))
      .catch(err => {
        console.error('Lỗi lấy danh sách đơn:', err)
        alert('Có lỗi xảy ra khi lấy danh sách đơn hàng hoặc phiên đăng nhập đã hết hạn!')
        navigate('/login')
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    fetchOrders()
  }, [])

  const handleCancelOrder = async (maHD) => {
    if (window.confirm(`Bạn có chắc chắn muốn hủy đơn hàng ${maHD}? Các thuốc đã giữ chỗ sẽ được hoàn lại kho.`)) {
      try {
        await orderService.cancelOrder(maHD)
        alert('Hủy đơn hàng thành công!')
        fetchOrders() // Tải lại danh sách
      } catch (err) {
        console.error(err)
        alert(err.response?.data?.message || 'Có lỗi xảy ra khi hủy đơn hàng!')
      }
    }
  }

  const getStatusBadge = (status) => {
    switch (status?.toUpperCase()) {
      case 'DAT_TRUOC':
        return <span className="bg-blue-50 text-blue-700 px-3 py-1 rounded-full text-xs font-semibold">Chờ chuẩn bị (Đặt trước)</span>
      case 'DANG_XU_LY':
        return <span className="bg-amber-50 text-amber-700 px-3 py-1 rounded-full text-xs font-semibold">Đang chuẩn bị/giao</span>
      case 'HOANTAT':
        return <span className="bg-green-50 text-green-700 px-3 py-1 rounded-full text-xs font-semibold">Đã hoàn tất</span>
      case 'HUY':
        return <span className="bg-red-50 text-red-700 px-3 py-1 rounded-full text-xs font-semibold">Đã hủy</span>
      default:
        return <span className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full text-xs font-semibold">{status}</span>
    }
  }

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-gray-400">Đang tải lịch sử đơn hàng...</p>
    </div>
  )

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <div className="flex-1 max-w-4xl mx-auto w-full px-4 sm:px-6 py-8">
        <h1 className="text-2xl font-bold text-gray-800 mb-6 flex items-center gap-2">
          📦 Lịch Sử Đặt Hàng Của Tôi
        </h1>

        {orders.length === 0 ? (
          <div className="bg-white rounded-2xl shadow p-12 text-center space-y-4">
            <div className="w-20 h-20 bg-blue-50 text-blue-500 rounded-full flex items-center justify-center text-4xl mx-auto">
              📦
            </div>
            <h3 className="text-lg font-bold text-gray-700">Bạn chưa có đơn đặt trước nào</h3>
            <p className="text-gray-400 text-sm">Hãy tìm thuốc của bạn và tiến hành đặt giữ chỗ ngay trên website.</p>
            <button
              onClick={() => navigate('/san-pham')}
              className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-xl font-medium transition"
            >
              Đặt hàng ngay
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {orders.map(order => (
              <div key={order.maHD} className="bg-white rounded-2xl shadow overflow-hidden border border-gray-100">
                {/* Header đơn hàng */}
                <div className="p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-gray-50/50">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-gray-800 text-sm">{order.maHD}</span>
                      {getStatusBadge(order.trangThai)}
                    </div>
                    <p className="text-xs text-gray-400">
                      Ngày đặt: {order.ngayBan ? new Date(order.ngayBan).toLocaleDateString('vi-VN') : 'Mới đặt'}
                    </p>
                  </div>

                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <p className="text-xs text-gray-400">Cần thanh toán</p>
                      <p className="font-bold text-blue-600 text-base">
                        {order.tienThanhToan ? order.tienThanhToan.toLocaleString('vi-VN') : '0'}đ
                      </p>
                    </div>

                    <button
                      onClick={() => setExpandedOrder(expandedOrder === order.maHD ? null : order.maHD)}
                      className="text-gray-400 hover:text-gray-600 text-sm font-semibold transition"
                    >
                      {expandedOrder === order.maHD ? 'Thu gọn ▲' : 'Chi tiết ▼'}
                    </button>
                  </div>
                </div>

                {/* Chi tiết sản phẩm trong đơn hàng (khi expand) */}
                {expandedOrder === order.maHD && (
                  <div className="p-5 border-t border-gray-100 space-y-4">
                    <div className="divide-y divide-gray-100">
                      {order.items?.map((item, idx) => (
                        <div key={idx} className="py-3 flex justify-between items-center text-sm gap-4">
                          <div>
                            <p className="font-semibold text-gray-800">{item.tenSanPham}</p>
                            <p className="text-xs text-gray-400 mt-0.5">Mã lô: {item.maLo} | Đơn vị: {item.dvt}</p>
                          </div>
                          <div className="text-right flex-shrink-0">
                            <p className="text-gray-600">{item.donGia ? item.donGia.toLocaleString('vi-VN') : '0'}đ x{item.sl}</p>
                            <p className="font-bold text-gray-800 mt-0.5">
                              {item.thanhTien ? item.thanhTien.toLocaleString('vi-VN') : '0'}đ
                            </p>
                          </div>
                        </div>
                      ))}
                    </div>

                    {/* Tổng chi phí */}
                    <div className="pt-4 border-t border-gray-100 flex flex-col items-end gap-2 text-xs text-gray-500">
                      <div className="flex justify-between w-64">
                        <span>Tạm tính tiền hàng:</span>
                        <span className="font-semibold text-gray-800">
                          {order.tongTien ? order.tongTien.toLocaleString('vi-VN') : '0'}đ
                        </span>
                      </div>
                      {order.diemSuDung > 0 && (
                        <div className="flex justify-between w-64 text-amber-600">
                          <span>Điểm tích lũy sử dụng:</span>
                          <span className="font-semibold">
                            -{order.diemSuDung.toLocaleString('vi-VN')}đ
                          </span>
                        </div>
                      )}
                      <div className="flex justify-between w-64 text-sm font-bold text-gray-800 pt-2 border-t border-dashed border-gray-100">
                        <span>Tổng thanh toán:</span>
                        <span className="text-blue-600">
                          {order.tienThanhToan ? order.tienThanhToan.toLocaleString('vi-VN') : '0'}đ
                        </span>
                      </div>
                    </div>

                    {/* Nút hủy đơn hàng */}
                    {order.trangThai === 'DAT_TRUOC' && (
                      <div className="flex justify-end pt-2">
                        <button
                          onClick={() => handleCancelOrder(order.maHD)}
                          className="bg-red-50 hover:bg-red-100 text-red-600 font-semibold px-5 py-2 rounded-xl text-xs transition"
                        >
                          ✕ Hủy đơn đặt trước
                        </button>
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      <Footer />
    </div>
  )
}
