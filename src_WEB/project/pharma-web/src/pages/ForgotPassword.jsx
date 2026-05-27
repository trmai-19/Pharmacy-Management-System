import { useState } from 'react'
import { Link } from 'react-router-dom'
import { authService } from '../services/api'

export default function ForgotPassword() {
  const [form, setForm] = useState({ sdt: '', email: '' })
  const [status, setStatus] = useState(null) // null | 'success' | 'error'
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setStatus(null)
    try {
      await authService.forgotPassword(form)
      setStatus('success')
      setMessage('Mật khẩu tạm thời đã được gửi vào email của bạn. Vui lòng kiểm tra hộp thư.')
    } catch (err) {
      setStatus('error')
      setMessage(err.response?.data || 'Có lỗi xảy ra. Vui lòng thử lại!')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-md">
        <div className="text-center mb-8">
          <span className="text-5xl">🔑</span>
          <h1 className="text-2xl font-bold text-blue-700 mt-2">Quên mật khẩu</h1>
          <p className="text-gray-500 text-sm mt-1">
            Nhập số điện thoại và email đã đăng ký để nhận mật khẩu tạm thời
          </p>
        </div>

        {status === 'success' && (
          <div className="bg-green-50 text-green-700 px-4 py-3 rounded-lg mb-6 text-sm">
            ✅ {message}
          </div>
        )}
        {status === 'error' && (
          <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg mb-6 text-sm">
            ❌ {message}
          </div>
        )}

        {status !== 'success' && (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Số điện thoại đã đăng ký
              </label>
              <input
                type="text"
                name="sdt"
                value={form.sdt}
                onChange={handleChange}
                placeholder="0901234567"
                className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email đã đăng ký
              </label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="example@email.com"
                className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50"
            >
              {loading ? 'Đang gửi...' : 'Gửi mật khẩu tạm thời'}
            </button>
          </form>
        )}

        <p className="text-center text-sm text-gray-500 mt-6">
          <Link to="/login" className="text-blue-600 font-medium hover:underline">
            ← Quay lại đăng nhập
          </Link>
        </p>
      </div>
    </div>
  )
}
