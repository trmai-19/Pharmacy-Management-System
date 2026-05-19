import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { sanPhamService } from '../services/api'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default function ProductDetail() {
  const { maSP } = useParams()
  const navigate = useNavigate()
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)

  useEffect(() => {
    sanPhamService.getDetail(maSP)
      .then(res => setProduct(res.data))
      .catch(() => setNotFound(true))
      .finally(() => setLoading(false))
  }, [maSP])

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-gray-400">Đang tải thông tin sản phẩm...</p>
    </div>
  )

  if (notFound) return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <div className="flex-1 flex flex-col items-center justify-center text-center p-6">
        <p className="text-6xl mb-4">😔</p>
        <h2 className="text-xl font-bold text-gray-700">Không tìm thấy sản phẩm</h2>
        <p className="text-gray-400 mt-2">Sản phẩm này không tồn tại hoặc đã bị xóa.</p>
        <button
          onClick={() => navigate(-1)}
          className="mt-6 bg-blue-600 text-white px-6 py-2.5 rounded-xl hover:bg-blue-700 transition"
        >
          ← Quay lại
        </button>
      </div>
      <Footer />
    </div>
  )

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <div className="flex-1 max-w-3xl mx-auto w-full p-6">
        <button
          onClick={() => navigate(-1)}
          className="text-blue-600 hover:underline text-sm mb-6 flex items-center gap-1"
        >
          ← Quay lại
        </button>

        <div className="bg-white rounded-2xl shadow p-6 space-y-6">
          {/* Header */}
          <div className="flex items-start gap-5">
            <div className="w-28 h-28 bg-blue-50 rounded-2xl flex items-center justify-center text-5xl flex-shrink-0">
              💊
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-800">{product.tenSanPham}</h1>
              <p className="text-gray-400 text-sm mt-1">Mã SP: {product.maSP}</p>
              {product.dvt && (
                <span className="inline-block mt-2 bg-blue-50 text-blue-600 text-xs px-3 py-1 rounded-full font-medium">
                  Đơn vị: {product.dvt}
                </span>
              )}
              {product.giaBan && (
                <p className="text-blue-700 font-bold text-xl mt-2">
                  {product.giaBan.toLocaleString('vi-VN')}đ
                </p>
              )}
            </div>
          </div>

          <hr className="border-gray-100" />

          {/* Công dụng */}
          {product.congDung && (
            <div>
              <h2 className="text-base font-bold text-gray-700 mb-2 flex items-center gap-2">
                ✅ Công dụng
              </h2>
              <p className="text-gray-600 leading-relaxed bg-green-50 rounded-xl px-4 py-3 text-sm">
                {product.congDung}
              </p>
            </div>
          )}

          {/* Thành phần / Hoạt chất */}
          <div>
            <h2 className="text-base font-bold text-gray-700 mb-2 flex items-center gap-2">
              🧪 Thành phần / Hoạt chất
            </h2>
            <p className="text-gray-500 bg-gray-50 rounded-xl px-4 py-3 text-sm">
              {product.thanhPhan || 'Vui lòng hỏi nhân viên tư vấn để biết thêm chi tiết về thành phần.'}
            </p>
          </div>

          {/* Lưu ý khi sử dụng */}
          <div>
            <h2 className="text-base font-bold text-gray-700 mb-2 flex items-center gap-2">
              ⚠️ Lưu ý khi sử dụng
            </h2>
            <div className="bg-yellow-50 border border-yellow-100 rounded-xl px-4 py-3 text-sm text-yellow-800">
              {product.luuY || (
                <ul className="space-y-1 list-disc list-inside">
                  <li>Đọc kỹ hướng dẫn sử dụng trước khi dùng.</li>
                  <li>Dùng theo đúng liều lượng và chỉ định của bác sĩ / dược sĩ.</li>
                  <li>Để xa tầm tay trẻ em.</li>
                  <li>Bảo quản nơi khô ráo, tránh ánh sáng trực tiếp.</li>
                </ul>
              )}
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  )
}
