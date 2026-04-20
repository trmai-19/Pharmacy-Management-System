import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <footer className="bg-gray-800 text-gray-300 mt-10">
      <div className="max-w-4xl mx-auto px-6 py-8">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 text-sm">
          {/* Brand */}
          <div>
            <p className="text-white font-bold text-lg mb-2">💊 Pharma</p>
            <p className="text-gray-400 text-xs leading-relaxed">
              Hệ thống nhà thuốc uy tín, cung cấp dược phẩm chất lượng và dịch vụ tư vấn chuyên nghiệp.
            </p>
          </div>

          {/* Links */}
          <div>
            <p className="text-white font-semibold mb-3">Liên kết nhanh</p>
            <ul className="space-y-2">
              <li><Link to="/" className="hover:text-white transition">Trang chủ</Link></li>
              <li><Link to="/diem" className="hover:text-white transition">Điểm tích lũy</Link></li>
              <li>
                <Link to="/chinh-sach-doi-tra" className="hover:text-white transition">
                  Chính sách đổi trả
                </Link>
              </li>
              <li>
                <Link to="/tim-kiem" className="hover:text-white transition">
                  Tìm kiếm sản phẩm
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <p className="text-white font-semibold mb-3">Liên hệ</p>
            <ul className="space-y-2 text-xs text-gray-400">
              <li>📍 Địa chỉ: TP. Hồ Chí Minh</li>
              <li>📞 Hotline: 1800-xxxx</li>
              <li>📧 Email: pharma@example.com</li>
              <li>🕐 Giờ mở cửa: 7:00 - 22:00</li>
            </ul>
          </div>
        </div>

        <hr className="border-gray-700 my-5" />
        <p className="text-center text-xs text-gray-500">
          © 2026 Pharma. All rights reserved.
        </p>
      </div>
    </footer>
  )
}
