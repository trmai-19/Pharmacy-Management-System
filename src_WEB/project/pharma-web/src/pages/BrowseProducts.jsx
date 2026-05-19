import { useState, useEffect, useMemo } from 'react'
import { Link } from 'react-router-dom'
import { sanPhamService } from '../services/api'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default function BrowseProducts() {
  const [products, setProducts] = useState([])
  const [danhMucList, setDanhMucList] = useState([])
  const [selectedDM, setSelectedDM] = useState('')
  const [searchInput, setSearchInput] = useState('')
  const [searchKeyword, setSearchKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [viewMode, setViewMode] = useState('grid') // 'grid' | 'list'

  // Load danh mục 1 lần
  useEffect(() => {
    sanPhamService.getDanhMuc()
      .then(res => setDanhMucList(res.data))
      .catch(err => console.error('Lỗi tải danh mục:', err))
  }, [])

  // Load sản phẩm khi filter thay đổi
  useEffect(() => {
    setLoading(true)
    sanPhamService.browse(searchKeyword || null, selectedDM || null)
      .then(res => setProducts(res.data))
      .catch(err => console.error('Lỗi tải sản phẩm:', err))
      .finally(() => setLoading(false))
  }, [searchKeyword, selectedDM])

  const handleSearch = (e) => {
    e.preventDefault()
    setSearchKeyword(searchInput.trim())
  }

  const handleClearFilters = () => {
    setSelectedDM('')
    setSearchInput('')
    setSearchKeyword('')
  }

  // Thống kê nhanh
  const stats = useMemo(() => ({
    total: products.length,
    categories: new Set(products.map(p => p.maDM).filter(Boolean)).size,
  }), [products])

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <div className="flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 py-6 space-y-6">

        {/* ======== HEADER ======== */}
        <div className="bg-gradient-to-br from-blue-600 via-blue-500 to-indigo-600 rounded-2xl p-6 sm:p-8 text-white shadow-lg">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">
                🏪 Danh Mục Sản Phẩm
              </h1>
              <p className="text-blue-100 mt-1 text-sm sm:text-base">
                Tra cứu toàn bộ thuốc & sản phẩm y tế tại nhà thuốc
              </p>
            </div>
            <div className="flex gap-4">
              <div className="bg-white/15 backdrop-blur rounded-xl px-5 py-3 text-center">
                <p className="text-2xl font-bold">{stats.total}</p>
                <p className="text-blue-100 text-xs mt-0.5">Sản phẩm</p>
              </div>
              <div className="bg-white/15 backdrop-blur rounded-xl px-5 py-3 text-center">
                <p className="text-2xl font-bold">{danhMucList.length}</p>
                <p className="text-blue-100 text-xs mt-0.5">Danh mục</p>
              </div>
            </div>
          </div>
        </div>

        {/* ======== SEARCH + FILTERS ======== */}
        <div className="bg-white rounded-2xl shadow p-4 sm:p-5 space-y-4">
          <div className="flex flex-col sm:flex-row gap-3">
            {/* Search */}
            <form onSubmit={handleSearch} className="flex-1 flex gap-2">
              <div className="relative flex-1">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">🔍</span>
                <input
                  type="text"
                  value={searchInput}
                  onChange={e => setSearchInput(e.target.value)}
                  placeholder="Tìm tên thuốc, công dụng, thành phần..."
                  className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-gray-50 text-sm transition"
                />
              </div>
              <button
                type="submit"
                className="bg-blue-600 text-white px-5 py-3 rounded-xl font-medium hover:bg-blue-700 transition-colors text-sm whitespace-nowrap"
              >
                Tìm kiếm
              </button>
            </form>

            {/* View mode toggle */}
            <div className="flex bg-gray-100 rounded-xl p-1 self-start">
              <button
                onClick={() => setViewMode('grid')}
                className={`px-3 py-2 rounded-lg text-sm transition ${
                  viewMode === 'grid' ? 'bg-white shadow text-blue-600 font-medium' : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                ▦ Grid
              </button>
              <button
                onClick={() => setViewMode('list')}
                className={`px-3 py-2 rounded-lg text-sm transition ${
                  viewMode === 'list' ? 'bg-white shadow text-blue-600 font-medium' : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                ☰ List
              </button>
            </div>
          </div>

          {/* Category pills */}
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => setSelectedDM('')}
              className={`px-4 py-2 rounded-full text-sm font-medium transition-all ${
                selectedDM === ''
                  ? 'bg-blue-600 text-white shadow-md shadow-blue-200'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              Tất cả
            </button>
            {danhMucList.map(dm => (
              <button
                key={dm.maDM}
                onClick={() => setSelectedDM(dm.maDM === selectedDM ? '' : dm.maDM)}
                className={`px-4 py-2 rounded-full text-sm font-medium transition-all ${
                  selectedDM === dm.maDM
                    ? 'bg-blue-600 text-white shadow-md shadow-blue-200'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                {dm.tenDM}
              </button>
            ))}
          </div>

          {/* Active filters info */}
          {(searchKeyword || selectedDM) && (
            <div className="flex items-center gap-2 text-sm text-gray-500 pt-1">
              <span>Đang lọc:</span>
              {searchKeyword && (
                <span className="bg-blue-50 text-blue-700 px-3 py-1 rounded-full text-xs font-medium">
                  🔍 "{searchKeyword}"
                </span>
              )}
              {selectedDM && (
                <span className="bg-indigo-50 text-indigo-700 px-3 py-1 rounded-full text-xs font-medium">
                  📁 {danhMucList.find(d => d.maDM === selectedDM)?.tenDM || selectedDM}
                </span>
              )}
              <button
                onClick={handleClearFilters}
                className="ml-auto text-red-500 hover:text-red-700 text-xs font-medium transition"
              >
                ✕ Xóa bộ lọc
              </button>
            </div>
          )}
        </div>

        {/* ======== PRODUCT LIST ======== */}
        {loading ? (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="w-10 h-10 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin mb-4"></div>
            <p className="text-gray-400 text-sm">Đang tải sản phẩm...</p>
          </div>
        ) : products.length === 0 ? (
          <div className="bg-white rounded-2xl shadow p-12 text-center">
            <p className="text-5xl mb-4">📦</p>
            <h3 className="text-lg font-bold text-gray-700">Không tìm thấy sản phẩm</h3>
            <p className="text-gray-400 text-sm mt-2">Thử thay đổi từ khóa hoặc bộ lọc danh mục.</p>
            <button
              onClick={handleClearFilters}
              className="mt-4 bg-blue-600 text-white px-6 py-2.5 rounded-xl text-sm font-medium hover:bg-blue-700 transition"
            >
              Xóa bộ lọc
            </button>
          </div>
        ) : viewMode === 'grid' ? (
          /* ---- GRID VIEW ---- */
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {products.map(sp => (
              <Link
                key={sp.maSP}
                to={`/san-pham/${sp.maSP}`}
                className="group bg-white rounded-2xl shadow-sm border border-gray-100 p-5 hover:shadow-lg hover:border-blue-200 hover:-translate-y-1 transition-all duration-300 flex flex-col"
              >
                {/* Icon */}
                <div className="w-16 h-16 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl flex items-center justify-center text-3xl mb-4 mx-auto group-hover:scale-110 transition-transform duration-300">
                  💊
                </div>

                {/* Name */}
                <h3 className="font-semibold text-gray-800 text-sm line-clamp-2 text-center leading-snug group-hover:text-blue-600 transition-colors">
                  {sp.tenSanPham}
                </h3>

                {/* DVT badge */}
                {sp.dvt && (
                  <span className="inline-block mx-auto mt-2 bg-gray-100 text-gray-500 text-[11px] px-2.5 py-0.5 rounded-full">
                    {sp.dvt}
                  </span>
                )}

                {/* Công dụng */}
                {sp.congDung && (
                  <p className="text-xs text-gray-400 mt-2 line-clamp-2 text-center leading-relaxed">
                    {sp.congDung}
                  </p>
                )}

                {/* Giá */}
                <div className="mt-auto pt-3">
                  {sp.giaBan ? (
                    <p className="text-blue-600 font-bold text-base text-center">
                      {sp.giaBan.toLocaleString('vi-VN')}đ
                    </p>
                  ) : (
                    <p className="text-gray-300 text-xs text-center">Liên hệ</p>
                  )}
                </div>
              </Link>
            ))}
          </div>
        ) : (
          /* ---- LIST VIEW ---- */
          <div className="bg-white rounded-2xl shadow overflow-hidden divide-y divide-gray-100">
            {products.map(sp => (
              <Link
                key={sp.maSP}
                to={`/san-pham/${sp.maSP}`}
                className="flex items-center gap-4 px-5 py-4 hover:bg-blue-50/50 transition-colors group"
              >
                {/* Icon */}
                <div className="w-12 h-12 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl flex items-center justify-center text-2xl flex-shrink-0 group-hover:scale-110 transition-transform">
                  💊
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold text-gray-800 text-sm group-hover:text-blue-600 transition-colors truncate">
                    {sp.tenSanPham}
                  </h3>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-xs text-gray-400">{sp.maSP}</span>
                    {sp.dvt && (
                      <>
                        <span className="text-gray-200">•</span>
                        <span className="text-xs text-gray-400">{sp.dvt}</span>
                      </>
                    )}
                    {sp.congDung && (
                      <>
                        <span className="text-gray-200">•</span>
                        <span className="text-xs text-gray-400 truncate max-w-xs">{sp.congDung}</span>
                      </>
                    )}
                  </div>
                </div>

                {/* Price */}
                <div className="text-right flex-shrink-0">
                  {sp.giaBan ? (
                    <p className="text-blue-600 font-bold text-sm whitespace-nowrap">
                      {sp.giaBan.toLocaleString('vi-VN')}đ
                    </p>
                  ) : (
                    <p className="text-gray-300 text-xs">Liên hệ</p>
                  )}
                </div>

                {/* Arrow */}
                <span className="text-gray-300 group-hover:text-blue-400 transition-colors text-lg flex-shrink-0">
                  ›
                </span>
              </Link>
            ))}
          </div>
        )}

        {/* Result count */}
        {!loading && products.length > 0 && (
          <p className="text-center text-gray-400 text-sm pb-2">
            Hiển thị {products.length} sản phẩm
            {selectedDM && ` trong danh mục "${danhMucList.find(d => d.maDM === selectedDM)?.tenDM || selectedDM}"`}
            {searchKeyword && ` cho "${searchKeyword}"`}
          </p>
        )}
      </div>

      <Footer />
    </div>
  )
}
