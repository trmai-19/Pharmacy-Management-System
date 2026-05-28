import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { khachHangService } from '../services/api'
import Footer from '../components/Footer'

export default function Home() {
  const [thongTin, setThongTin] = useState(null)
  const [diem, setDiem] = useState(null)
  const [lichSu, setLichSu] = useState([])
  const [loading, setLoading] = useState(true)
  const [selectedInvoice, setSelectedInvoice] = useState(null)
  const [invoiceDetails, setInvoiceDetails] = useState([])
  const [loadingDetails, setLoadingDetails] = useState(false)

  const handleViewInvoiceDetails = async (hd) => {
    setSelectedInvoice(hd)
    setLoadingDetails(true)
    try {
      const res = await khachHangService.getChiTietHoaDon(hd.maHD)
      setInvoiceDetails(res.data)
    } catch (err) {
      console.error("Lỗi khi tải chi tiết hóa đơn:", err)
    } finally {
      setLoadingDetails(false)
    }
  }

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
                    onClick={() => handleViewInvoiceDetails(hd)}
                    className="flex justify-between items-center border-b pb-3 last:border-0 cursor-pointer hover:bg-gray-50 px-3 py-2 rounded-xl transition duration-200">
                    <div>
                      <p className="font-semibold text-gray-800">{hd.maHD}</p>
                      <p className="text-sm text-gray-400">{hd.ngayBan}</p>
                    </div>
                    <div className="text-right flex items-center gap-4">
                      <div>
                        <p className="font-bold text-blue-600">
                          {hd.tongTien?.toLocaleString('vi-VN')}đ
                        </p>
                        <p className="text-xs text-gray-400">
                          -{hd.diemSuDung ?? 0} điểm
                        </p>
                      </div>
                      <span className="text-gray-400 text-sm font-medium">➔</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>
      </div>

      {/* Modal Chi tiết Hóa đơn */}
      {selectedInvoice && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-2xl w-full shadow-2xl overflow-hidden animate-in fade-in zoom-in-95 duration-200">
            {/* Header */}
            <div className="bg-gradient-to-r from-blue-600 to-blue-500 px-6 py-4 flex items-center justify-between text-white">
              <div>
                <h3 className="font-bold text-lg">Chi tiết đơn hàng</h3>
                <p className="text-xs text-blue-100 mt-0.5">Mã HD: {selectedInvoice.maHD}</p>
              </div>
              <button 
                onClick={() => setSelectedInvoice(null)}
                className="text-white hover:text-blue-100 bg-white/10 hover:bg-white/20 p-2 rounded-full transition w-8 h-8 flex items-center justify-center font-bold text-lg"
              >
                ✕
              </button>
            </div>

            {/* Content */}
            <div className="p-6 max-h-[70vh] overflow-y-auto space-y-6">
              {/* Info grid */}
              <div className="grid grid-cols-2 gap-4 text-sm bg-gray-50 p-4 rounded-xl">
                <div>
                  <span className="text-gray-400 block">Ngày mua:</span>
                  <span className="font-medium text-gray-700">{selectedInvoice.ngayBan}</span>
                </div>
                <div>
                  <span className="text-gray-400 block">Trạng thái:</span>
                  <span className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold mt-1 ${
                    selectedInvoice.trangThai === 'Đã thanh toán' || selectedInvoice.trangThai === 'PAID'
                      ? 'bg-green-100 text-green-700' 
                      : 'bg-yellow-100 text-yellow-700'
                  }`}>
                    {selectedInvoice.trangThai || 'Đã thanh toán'}
                  </span>
                </div>
              </div>

              {/* Items List */}
              <div>
                <h4 className="font-semibold text-gray-700 mb-3 flex items-center gap-2">
                  <span>📦</span> Danh sách sản phẩm
                </h4>
                {loadingDetails ? (
                  <div className="py-8 text-center text-gray-400">
                    <p className="animate-pulse">Đang tải chi tiết sản phẩm...</p>
                  </div>
                ) : invoiceDetails.length === 0 ? (
                  <p className="text-gray-400 text-sm text-center py-4">Không tìm thấy chi tiết đơn hàng.</p>
                ) : (
                  <div className="border border-gray-100 rounded-xl overflow-hidden">
                    <table className="w-full text-sm text-left">
                      <thead className="bg-gray-50 text-gray-600 font-medium border-b border-gray-100">
                        <tr>
                          <th className="px-4 py-3">Sản phẩm</th>
                          <th className="px-4 py-3 text-center">SL</th>
                          <th className="px-4 py-3 text-right">Đơn giá</th>
                          <th className="px-4 py-3 text-right">Thành tiền</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-100 text-gray-700">
                        {invoiceDetails.map((item, idx) => (
                          <tr key={idx} className="hover:bg-gray-50/50">
                            <td className="px-4 py-3">
                              <p className="font-medium">{item.tenSanPham}</p>
                              <span className="text-xs text-gray-400">ĐVT: {item.dvt} | Lô: {item.maLo}</span>
                              {item.ghiChu && <p className="text-xs text-yellow-600 mt-0.5">*{item.ghiChu}</p>}
                            </td>
                            <td className="px-4 py-3 text-center font-medium">{item.sl}</td>
                            <td className="px-4 py-3 text-right">{item.donGia?.toLocaleString('vi-VN')}đ</td>
                            <td className="px-4 py-3 text-right font-semibold text-gray-900">{item.thanhTien?.toLocaleString('vi-VN')}đ</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

              {/* Price summary */}
              <div className="border-t pt-4 space-y-2 text-sm text-gray-600">
                <div className="flex justify-between">
                  <span>Điểm sử dụng:</span>
                  <span className="text-red-500 font-medium">-{selectedInvoice.diemSuDung ?? 0} điểm</span>
                </div>
                <div className="flex justify-between text-base font-bold text-gray-900 border-t pt-3 mt-2">
                  <span>Tổng tiền thanh toán:</span>
                  <span className="text-blue-600 text-lg">{selectedInvoice.tongTien?.toLocaleString('vi-VN')}đ</span>
                </div>
              </div>
            </div>

            {/* Footer */}
            <div className="bg-gray-50 px-6 py-4 flex justify-end">
              <button 
                onClick={() => setSelectedInvoice(null)}
                className="bg-blue-600 hover:bg-blue-500 text-white font-medium px-5 py-2 rounded-xl transition text-sm shadow"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}

      <Footer />
    </div>
  )
}