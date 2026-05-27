import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { khachHangService } from '../services/api'
import Footer from '../components/Footer'

export default function Home() {
  const [thongTin, setThongTin] = useState(null)
  const [diem, setDiem] = useState(null)
  const [lichSu, setLichSu] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [ttRes, diemRes, lichSuRes] = await Promise.all([
          khachHangService.getThongTin(),
          khachHangService.getDiem(),
          khachHangService.getLichSu(),
        ])
        setThongTin(ttRes.data)
        setDiem(diemRes.data)
        setLichSu(lichSuRes.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-gray-500">Đang tải...</p>
    </div>
  )

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <div className="flex-1 p-6">
        <div className="max-w-4xl mx-auto space-y-6">

          {/* Chào mừng */}
          <div className="bg-gradient-to-r from-blue-600 to-blue-500 rounded-2xl p-6 text-white shadow">
            <p className="text-blue-100 text-sm">Xin chào,</p>
            <h2 className="text-2xl font-bold mt-1">{thongTin?.tenKH}</h2>
            <p className="text-blue-100 text-sm mt-1">{thongTin?.sdt}</p>
          </div>

          {/* Điểm tích lũy */}
          <div className="bg-white rounded-2xl p-6 shadow flex items-center justify-between">
            <div>
              <p className="text-gray-500 text-sm">Tổng điểm tích lũy</p>
              <p className="text-4xl font-bold text-blue-600 mt-1">
                {diem?.tongDiem ?? 0}
              </p>
              <p className="text-gray-400 text-xs mt-1">điểm</p>
            </div>
            <div className="text-6xl">🏆</div>
          </div>

          {/* 3 nút nhanh */}
          <div className="grid grid-cols-3 gap-4">
            <Link to="/diem"
              className="bg-white rounded-2xl p-5 shadow text-center hover:shadow-md transition">
              <div className="text-3xl mb-2">⭐</div>
              <p className="font-medium text-gray-700 text-sm">Điểm tích lũy</p>
              <p className="text-xs text-gray-400 mt-1">Xem chi tiết</p>
            </Link>
            <Link to="/san-pham"
              className="bg-white rounded-2xl p-5 shadow text-center hover:shadow-md transition">
              <div className="text-3xl mb-2">🏪</div>
              <p className="font-medium text-gray-700 text-sm">Sản phẩm</p>
              <p className="text-xs text-gray-400 mt-1">Xem tất cả</p>
            </Link>
            <Link to="/chinh-sach-doi-tra"
              className="bg-white rounded-2xl p-5 shadow text-center hover:shadow-md transition">
              <div className="text-3xl mb-2">🔄</div>
              <p className="font-medium text-gray-700 text-sm">Đổi trả</p>
              <p className="text-xs text-gray-400 mt-1">Chính sách</p>
            </Link>
          </div>

          {/* Lịch sử mua hàng gần đây */}
          <div className="bg-white rounded-2xl p-6 shadow">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-gray-700">Lịch sử mua hàng gần đây</h3>
              <span className="text-sm text-gray-400">🛒 {lichSu.length} đơn</span>
            </div>
            {lichSu.length === 0 ? (
              <p className="text-gray-400 text-sm text-center py-4">
                Chưa có đơn hàng nào
              </p>
            ) : (
              <div className="space-y-3">
                {lichSu.slice(0, 5).map((hd) => (
                  <div key={hd.maHD}
                    className="flex justify-between items-center border-b pb-3 last:border-0">
                    <div>
                      <p className="font-medium text-gray-700">{hd.maHD}</p>
                      <p className="text-sm text-gray-400">{hd.ngayBan}</p>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-blue-600">
                        {hd.tongTien?.toLocaleString('vi-VN')}đ
                      </p>
                      <p className="text-xs text-gray-400">
                        -{hd.diemSuDung ?? 0} điểm
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>
      </div>
      <Footer />
    </div>
  )
}