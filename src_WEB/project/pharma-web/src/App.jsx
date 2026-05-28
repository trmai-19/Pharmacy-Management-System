import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Home from './pages/Home'
import DiemTichLuy from './pages/DiemTichLuy'
import ForgotPassword from './pages/ForgotPassword'
import ChangePassword from './pages/ChangePassword'
import SearchResult from './pages/SearchResult'
import ProductDetail from './pages/ProductDetail'
import BrowseProducts from './pages/BrowseProducts'
import ReturnPolicy from './pages/ReturnPolicy'
import Navbar from './components/Navbar'
import Profile from './pages/Profile'
import Cart from './pages/Cart'
import Checkout from './pages/Checkout'
import OrderHistory from './pages/OrderHistory'

// Bảo vệ route cần đăng nhập
const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token')
  return token ? children : <Navigate to="/login" />
}

// Layout có Navbar cho các trang private
const WithNavbar = ({ children }) => (
  <>
    <Navbar />
    {children}
  </>
)

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes – không cần đăng nhập */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/quen-mat-khau" element={<ForgotPassword />} />
        <Route path="/chinh-sach-doi-tra" element={<ReturnPolicy />} />

        {/* Tìm kiếm, duyệt & chi tiết sản phẩm – public */}
        <Route path="/san-pham" element={<BrowseProducts />} />
        <Route path="/tim-kiem" element={<SearchResult />} />
        <Route path="/san-pham/:maSP" element={<ProductDetail />} />
        
        {/* Giỏ hàng – public */}
        <Route path="/cart" element={<Cart />} />

        {/* Route bắt buộc sau khi đăng nhập bằng pass tạm */}
        <Route path="/doi-mat-khau" element={
          <PrivateRoute>
            <ChangePassword />
          </PrivateRoute>
        } />

        {/* Private routes – cần đăng nhập */}
        <Route path="/" element={
          <PrivateRoute>
            <WithNavbar><Home /></WithNavbar>
          </PrivateRoute>
        } />
        <Route path="/diem" element={
          <PrivateRoute>
            <WithNavbar><DiemTichLuy /></WithNavbar>
          </PrivateRoute>
        } />
        <Route path="/profile" element={
          <PrivateRoute>
            <WithNavbar><Profile /></WithNavbar>
          </PrivateRoute>
        } />
        <Route path="/checkout" element={
          <PrivateRoute>
            <Checkout />
          </PrivateRoute>
        } />
        <Route path="/orders" element={
          <PrivateRoute>
            <OrderHistory />
          </PrivateRoute>
        } />

        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App