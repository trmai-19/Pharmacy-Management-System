import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authService } from '../services/api'

export default function ChangePassword() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ newPassword: '', confirmPassword: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (form.newPassword !== form.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp!')
      return
    }
    if (form.newPassword.length < 6) {
      setError('Mật khẩu phải có ít nhất 6 ký tự!')
      return
    }
    setLoading(true)
    setError('')
    try {
      await authService.changePassword({ newPassword: form.newPassword })
      navigate('/')
    } catch (err) {
      setError(err.response?.data || 'Đổi mật khẩu thất bại!')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-md">
        <div className="text-center mb-8">
          <span className="text-5xl">🔒</span>
          <h1 className="text-2xl font-bold text-blue-700 mt-2">Đặt mật khẩu mới</h1>
          <p className="text-gray-500 text-sm mt-1">
            Bạn đang dùng mật khẩu tạm thời. Vui lòng đặt mật khẩu mới để tiếp tục.
          </p>
        </div>

        <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded-lg mb-6 text-sm">
          ⚠️ Bạn phải đổi mật khẩu trước khi sử dụng hệ thống.
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg mb-4 text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu mới</label>
            <input
              type="password"
              name="newPassword"
              value={form.newPassword}
              onChange={handleChange}
              placeholder="Tối thiểu 6 ký tự"
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Xác nhận mật khẩu mới</label>
            <input
              type="password"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              placeholder="Nhập lại mật khẩu mới"
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50"
          >
            {loading ? 'Đang lưu...' : 'Xác nhận đổi mật khẩu'}
          </button>
        </form>
      </div>
    </div>
  )
}
