import { useState, useEffect } from 'react'
import { useSearchParams, Link, useNavigate } from 'react-router-dom'
import { sanPhamService } from '../services/api'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default function SearchResult() {
  const [searchParams] = useSearchParams()
  const keyword = searchParams.get('q') || ''
  const navigate = useNavigate()

  const [data, setData] = useState({ ketQua: [], sanPhamTuongTu: [] })
  const [loading, setLoading] = useState(false)
  const [searchInput, setSearchInput] = useState(keyword)

  useEffect(() => {
    if (!keyword) return
    setLoading(true)
    sanPhamService.search(keyword)
      .then(res => setData(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false))
  }, [keyword])

  const handleSearch = (e) => {
    e.preventDefault()
    navigate(`/tim-kiem?q=${encodeURIComponent(searchInput)}`)
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <div className="flex-1 max-w-4xl mx-auto w-full p-6 space-y-6">

        {/* Search bar on page */}
        <form onSubmit={handleSearch} className="flex gap-2">
          <input
            type="text"
            value={searchInput}
            onChange={e => setSearchInput(e.target.value)}
            placeholder="Tìm kiếm thuốc, công dụng..."
            className="flex-1 border border-gray-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white shadow-sm"
          />
          <button
            type="submit"
            className="bg-blue-600 text-white px-6 py-3 rounded-xl font-medium hover:bg-blue-700 transition"
          >
            🔍 Tìm
          </button>
        </form>

        {loading ? (
          <div className="text-center text-gray-400 py-12">Đang tìm kiếm...</div>
        ) : (
          <>
            {/* Kết quả chính */}
            <div className="bg-white rounded-2xl shadow p-6">
              <h2 className="text-lg font-bold text-gray-700 mb-4">
                {keyword
                  ? `Kết quả tìm kiếm cho "${keyword}" — ${data.ketQua.length} sản phẩm`
                  : 'Nhập từ khóa để tìm kiếm'}
              </h2>

              {data.ketQua.length === 0 && keyword ? (
                <div className="text-center py-8">
                  <p className="text-4xl mb-3">🔍</p>
                  <p className="text-gray-500">Không tìm thấy sản phẩm phù hợp.</p>
                  <p className="text-gray-400 text-sm mt-1">Thử từ khóa khác hoặc liên hệ nhà thuốc.</p>
                </div>
              ) : (
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                  {data.ketQua.map(sp => (
                    <Link
                      key={sp.maSP}
                      to={`/san-pham/${sp.maSP}`}
                      className="border border-gray-100 rounded-xl p-4 hover:shadow-md hover:border-blue-200 transition text-center"
                    >
                      <div className="text-5xl mb-3">💊</div>
                      <p className="font-semibold text-gray-700 text-sm line-clamp-2">{sp.tenSanPham}</p>
                      {sp.congDung && (
                        <p className="text-xs text-gray-400 mt-1 line-clamp-2">{sp.congDung}</p>
                      )}
                      {sp.giaBan && (
                        <p className="text-blue-600 font-bold text-sm mt-2">
                          {sp.giaBan.toLocaleString('vi-VN')}đ
                        </p>
                      )}
                    </Link>
                  ))}
                </div>
              )}
            </div>

            {/* Sản phẩm tương tự */}
            {data.sanPhamTuongTu.length > 0 && (
              <div className="bg-white rounded-2xl shadow p-6">
                <h2 className="text-lg font-bold text-gray-700 mb-4">Sản phẩm tương tự</h2>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                  {data.sanPhamTuongTu.map(sp => (
                    <Link
                      key={sp.maSP}
                      to={`/san-pham/${sp.maSP}`}
                      className="border border-gray-100 rounded-xl p-4 hover:shadow-md hover:border-blue-200 transition text-center"
                    >
                      <div className="text-4xl mb-2">💊</div>
                      <p className="font-semibold text-gray-700 text-sm line-clamp-2">{sp.tenSanPham}</p>
                      {sp.giaBan && (
                        <p className="text-blue-600 font-bold text-sm mt-1">
                          {sp.giaBan.toLocaleString('vi-VN')}đ
                        </p>
                      )}
                    </Link>
                  ))}
                </div>
              </div>
            )}
          </>
        )}
      </div>
      <Footer />
    </div>
  )
}
