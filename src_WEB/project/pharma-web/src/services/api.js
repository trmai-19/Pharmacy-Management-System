import axios from 'axios'

const api = axios.create({
  // Vite proxy se forward /api -> http://localhost:8081
  // (xem vite.config.js)
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  forgotPassword: (data) => api.post('/auth/forgot-password', data),
  changePassword: (data) => api.post('/auth/change-password', data),
}

export const khachHangService = {
  getThongTin: () => api.get('/khachhang/me'),
  getDiem: () => api.get('/khachhang/diem'),
  getLichSu: () => api.get('/khachhang/lichsu'),
}

export const sanPhamService = {
  search: (keyword) => api.get(`/sanpham/search?q=${encodeURIComponent(keyword)}`),
  getDetail: (maSP) => api.get(`/sanpham/${maSP}`),
}

export default api