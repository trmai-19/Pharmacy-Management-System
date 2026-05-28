import { useState, useEffect } from 'react'
import { khachHangService } from '../services/api'
import Footer from '../components/Footer'

export default function Profile() {
  const [profile, setProfile] = useState({
    tenKH: '',
    sdt: '',
    email: '',
    gioiTinh: 'Nam',
    ngaySinh: '',
    tongDoanhThu: 0,
    diemTichLuy: 0,
    hangTV: 'Thành Viên'
  })
  
  const [formData, setFormData] = useState({
    tenKH: '',
    sdt: '',
    email: '',
    gioiTinh: 'Nam',
    ngaySinh: ''
  })

  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [message, setMessage] = useState({ type: '', text: '' })

  useEffect(() => {
    fetchProfile()
  }, [])

  const fetchProfile = async () => {
    setLoading(true)
    try {
      const res = await khachHangService.getThongTin()
      const data = res.data
      setProfile(data)
      setFormData({
        tenKH: data.tenKH || '',
        sdt: data.sdt || '',
        email: data.email || '',
        gioiTinh: data.gioiTinh || 'Nam',
        ngaySinh: data.ngaySinh || ''
      })
    } catch (err) {
      console.error("Lỗi tải thông tin cá nhân:", err)
      setMessage({ type: 'error', text: 'Không thể tải thông tin cá nhân.' })
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMessage({ type: '', text: '' })
    
    // Validations
    if (!formData.tenKH.trim()) {
      setMessage({ type: 'error', text: 'Họ tên không được để trống.' })
      return
    }
    if (!formData.sdt.trim() || !/^\d{10}$/.test(formData.sdt.trim())) {
      setMessage({ type: 'error', text: 'Số điện thoại phải chứa đúng 10 chữ số.' })
      return
    }
    if (formData.email.trim() && !/\S+@\S+\.\S+/.test(formData.email.trim())) {
      setMessage({ type: 'error', text: 'Email không hợp lệ.' })
      return
    }

    setSubmitting(true)
    try {
      const res = await khachHangService.updateThongTin(formData)
      const updatedProfile = res.data
      setProfile(updatedProfile)
      
      // Update LocalStorage to keep Navbar and application in sync
      localStorage.setItem('tenKH', updatedProfile.tenKH)
      localStorage.setItem('sdt', updatedProfile.sdt)
      
      setMessage({ type: 'success', text: 'Cập nhật thông tin cá nhân thành công!' })
      // Auto-clear success message after 3s
      setTimeout(() => setMessage({ type: '', text: '' }), 4000)
    } catch (err) {
      console.error(err)
      const errMsg = err.response?.data?.message || err.message || 'Lỗi khi cập nhật thông tin.'
      setMessage({ type: 'error', text: errMsg })
    } finally {
      setSubmitting(false)
    }
  }

  const getTierBadgeClass = (tier) => {
    const t = tier?.toLowerCase() || ''
    if (t.includes('kim cương') || t.includes('diamond')) {
      return 'bg-purple-100 text-purple-700 border border-purple-200'
    }
    if (t.includes('vàng') || t.includes('gold')) {
      return 'bg-amber-100 text-amber-700 border border-amber-200'
    }
    if (t.includes('bạc') || t.includes('silver')) {
      return 'bg-slate-100 text-slate-700 border border-slate-200'
    }
    return 'bg-blue-100 text-blue-700 border border-blue-200'
  }

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <p className="text-gray-500 animate-pulse font-medium">Đang tải thông tin cá nhân...</p>
    </div>
  )

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <div className="flex-1 p-6">
        <div className="max-w-3xl mx-auto space-y-6">

          {/* Banner chào mừng & Thẻ thành viên */}
          <div className="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-3xl p-6 text-white shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-6 relative overflow-hidden">
            {/* Background elements */}
            <div className="absolute right-0 top-0 text-white/5 text-9xl font-bold select-none pointer-events-none transform translate-x-10 -translate-y-10">
              PHARMA
            </div>
            
            <div className="space-y-2">
              <span className="text-blue-100 text-xs font-semibold tracking-wider uppercase bg-white/10 px-3 py-1 rounded-full">
                Thành viên tích điểm
              </span>
              <h2 className="text-3xl font-extrabold tracking-tight mt-1">{profile.tenKH}</h2>
              <p className="text-blue-100 text-sm flex items-center gap-1.5">
                <span>📞</span> {profile.sdt}
              </p>
            </div>

            <div className="bg-white/10 backdrop-blur-md rounded-2xl p-4 border border-white/20 flex items-center gap-4">
              <div className="text-right">
                <p className="text-blue-100 text-xs">Hạng thành viên</p>
                <p className="text-xl font-bold mt-0.5">{profile.hangTV}</p>
              </div>
              <div className="text-4xl">🏅</div>
            </div>
          </div>

          {/* Thống kê doanh thu & điểm số */}
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 flex items-center justify-between">
              <div>
                <p className="text-gray-400 text-xs font-medium uppercase tracking-wider">Tổng tích điểm</p>
                <p className="text-3xl font-bold text-blue-600 mt-1">{profile.diemTichLuy ?? 0}</p>
                <span className="text-gray-400 text-xs">điểm</span>
              </div>
              <div className="bg-blue-50 text-blue-500 w-12 h-12 rounded-xl flex items-center justify-center text-2xl">
                ⭐
              </div>
            </div>
            
            <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 flex items-center justify-between">
              <div>
                <p className="text-gray-400 text-xs font-medium uppercase tracking-wider">Doanh thu mua sắm</p>
                <p className="text-2xl font-bold text-green-600 mt-1">
                  {profile.tongDoanhThu?.toLocaleString('vi-VN')}đ
                </p>
                <span className="text-gray-400 text-xs">Đã tích lũy</span>
              </div>
              <div className="bg-green-50 text-green-500 w-12 h-12 rounded-xl flex items-center justify-center text-2xl">
                💳
              </div>
            </div>
          </div>

          {/* Form chỉnh sửa thông tin */}
          <div className="bg-white rounded-3xl p-6 shadow-sm border border-gray-100">
            <div className="flex items-center gap-2 mb-6">
              <span className="text-2xl">📝</span>
              <h3 className="font-bold text-gray-800 text-lg">Chỉnh sửa thông tin cá nhân</h3>
            </div>

            {message.text && (
              <div className={`p-4 rounded-xl text-sm mb-6 ${
                message.type === 'success' 
                  ? 'bg-green-50 text-green-700 border border-green-200' 
                  : 'bg-red-50 text-red-700 border border-red-200'
              }`}>
                {message.text}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                
                {/* Họ tên */}
                <div>
                  <label className="block text-gray-500 text-xs font-semibold mb-1.5 uppercase tracking-wider">Họ và tên</label>
                  <input
                    type="text"
                    name="tenKH"
                    value={formData.tenKH}
                    onChange={handleChange}
                    className="w-full bg-gray-50 border border-gray-200 hover:border-gray-300 focus:border-blue-500 focus:bg-white rounded-xl px-4 py-3 text-sm transition duration-150 focus:outline-none text-gray-700 font-medium"
                    placeholder="Nguyễn Văn A"
                  />
                </div>

                {/* Số điện thoại */}
                <div>
                  <label className="block text-gray-500 text-xs font-semibold mb-1.5 uppercase tracking-wider">Số điện thoại</label>
                  <input
                    type="tel"
                    name="sdt"
                    value={formData.sdt}
                    onChange={handleChange}
                    className="w-full bg-gray-50 border border-gray-200 hover:border-gray-300 focus:border-blue-500 focus:bg-white rounded-xl px-4 py-3 text-sm transition duration-150 focus:outline-none text-gray-700 font-medium"
                    placeholder="09XXXXXXXX"
                  />
                </div>

                {/* Email */}
                <div>
                  <label className="block text-gray-500 text-xs font-semibold mb-1.5 uppercase tracking-wider">Email liên hệ</label>
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    className="w-full bg-gray-50 border border-gray-200 hover:border-gray-300 focus:border-blue-500 focus:bg-white rounded-xl px-4 py-3 text-sm transition duration-150 focus:outline-none text-gray-700 font-medium"
                    placeholder="name@example.com"
                  />
                </div>

                {/* Ngày sinh */}
                <div>
                  <label className="block text-gray-500 text-xs font-semibold mb-1.5 uppercase tracking-wider">Ngày sinh</label>
                  <input
                    type="date"
                    name="ngaySinh"
                    value={formData.ngaySinh}
                    onChange={handleChange}
                    className="w-full bg-gray-50 border border-gray-200 hover:border-gray-300 focus:border-blue-500 focus:bg-white rounded-xl px-4 py-3 text-sm transition duration-150 focus:outline-none text-gray-700 font-medium"
                  />
                </div>

                {/* Giới tính */}
                <div>
                  <label className="block text-gray-500 text-xs font-semibold mb-1.5 uppercase tracking-wider">Giới tính</label>
                  <div className="flex gap-4 mt-2">
                    {['Nam', 'Nữ', 'Khác'].map((gender) => (
                      <label key={gender} className="inline-flex items-center gap-2 cursor-pointer text-sm text-gray-700 font-medium">
                        <input
                          type="radio"
                          name="gioiTinh"
                          value={gender}
                          checked={formData.gioiTinh === gender}
                          onChange={handleChange}
                          className="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500 cursor-pointer"
                        />
                        {gender}
                      </label>
                    ))}
                  </div>
                </div>

              </div>

              {/* Submit button */}
              <div className="border-t pt-5 mt-6 flex justify-end">
                <button
                  type="submit"
                  disabled={submitting}
                  className={`bg-blue-600 hover:bg-blue-500 text-white font-bold px-6 py-3 rounded-xl transition duration-150 shadow-md ${
                    submitting ? 'opacity-70 cursor-not-allowed' : ''
                  }`}
                >
                  {submitting ? 'Đang lưu...' : 'Lưu thay đổi'}
                </button>
              </div>
            </form>
          </div>

        </div>
      </div>
      <Footer />
    </div>
  )
}
