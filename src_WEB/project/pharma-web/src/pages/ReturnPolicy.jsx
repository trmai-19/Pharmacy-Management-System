import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default function ReturnPolicy() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <div className="flex-1 max-w-3xl mx-auto w-full p-6">

        {/* Hero */}
        <div className="bg-gradient-to-r from-blue-600 to-blue-500 rounded-2xl p-8 text-white shadow mb-6">
          <div className="text-4xl mb-3">🔄</div>
          <h1 className="text-2xl font-bold">Chính Sách Đổi Trả</h1>
          <p className="text-blue-100 text-sm mt-2">
            Nhà thuốc Pharma cam kết hỗ trợ đổi trả minh bạch, bảo vệ quyền lợi khách hàng.
          </p>
        </div>

        <div className="space-y-5">

          {/* 1. Điều kiện được đổi trả */}
          <Section title="1. Điều kiện được đổi trả" icon="✅">
            <p className="text-gray-600 text-sm mb-3">
              Sản phẩm được chấp nhận đổi trả khi đáp ứng <strong>đồng thời</strong> các điều kiện sau:
            </p>
            <ul className="space-y-2 text-sm text-gray-600">
              <li className="flex items-start gap-2"><span className="text-green-500 mt-0.5">✓</span> Còn nguyên vẹn, chưa qua sử dụng, chưa mở seal / bao bì.</li>
              <li className="flex items-start gap-2"><span className="text-green-500 mt-0.5">✓</span> Còn trong thời hạn đổi trả (xem mục 2).</li>
              <li className="flex items-start gap-2"><span className="text-green-500 mt-0.5">✓</span> Có hóa đơn mua hàng hợp lệ từ nhà thuốc Pharma.</li>
              <li className="flex items-start gap-2"><span className="text-green-500 mt-0.5">✓</span> Không thuộc danh mục không được đổi trả (xem mục 3).</li>
            </ul>
          </Section>

          {/* 2. Thời gian áp dụng */}
          <Section title="2. Thời gian áp dụng" icon="📅">
            <div className="bg-blue-50 rounded-xl px-4 py-3 text-sm text-blue-800">
              <strong>Thời hạn đổi trả: 7 ngày</strong> kể từ ngày mua hàng ghi trên hóa đơn.
            </div>
            <p className="text-gray-500 text-xs mt-2">
              * Trường hợp sản phẩm lỗi do nhà sản xuất: thời hạn theo bảo hành của nhà sản xuất.
            </p>
          </Section>

          {/* 3. Được / Không được đổi trả */}
          <Section title="3. Các trường hợp được và không được đổi trả" icon="📋">
            <div className="overflow-x-auto">
              <table className="w-full text-sm border-collapse">
                <thead>
                  <tr className="bg-gray-50">
                    <th className="text-left px-4 py-2 border border-gray-200 text-gray-600">Trường hợp</th>
                    <th className="text-center px-4 py-2 border border-gray-200 w-24 text-gray-600">Đổi trả</th>
                  </tr>
                </thead>
                <tbody>
                  {[
                    ['Sản phẩm lỗi do nhà sản xuất', true],
                    ['Giao nhầm sản phẩm (khác với hóa đơn)', true],
                    ['Sản phẩm hết hạn sử dụng trước khi bán', true],
                    ['Sản phẩm đã mở bao bì / đã sử dụng', false],
                    ['Thuốc kê đơn (theo chỉ định bác sĩ)', false],
                    ['Quá thời hạn đổi trả (sau 7 ngày)', false],
                    ['Không có hóa đơn mua hàng', false],
                    ['Sản phẩm khuyến mãi / dùng thử', false],
                  ].map(([text, ok]) => (
                    <tr key={text} className="border-b border-gray-100 hover:bg-gray-50">
                      <td className="px-4 py-2.5 text-gray-700">{text}</td>
                      <td className="px-4 py-2.5 text-center">
                        {ok
                          ? <span className="text-green-600 font-bold">✅ Được</span>
                          : <span className="text-red-500 font-bold">❌ Không</span>
                        }
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Section>

          {/* 4. Quy trình đổi trả */}
          <Section title="4. Quy trình đổi trả" icon="🔄">
            <div className="space-y-3">
              {[
                { step: 1, title: 'Liên hệ nhà thuốc', desc: 'Đến trực tiếp quầy, hoặc liên hệ qua hotline / fanpage.' },
                { step: 2, title: 'Xuất trình hóa đơn & sản phẩm', desc: 'Mang theo hóa đơn mua hàng và sản phẩm cần đổi trả.' },
                { step: 3, title: 'Nhà thuốc kiểm tra', desc: 'Dược sĩ xác nhận điều kiện đổi trả (hạn sử dụng, bao bì, v.v.).' },
                { step: 4, title: 'Xử lý đổi trả', desc: 'Đổi sản phẩm tương đương hoặc hoàn tiền theo phương thức thanh toán ban đầu.' },
                { step: 5, title: 'Hoàn tất', desc: 'Cập nhật hóa đơn / phiếu đổi trả và lưu hồ sơ giao dịch.' },
              ].map(item => (
                <div key={item.step} className="flex items-start gap-4">
                  <div className="w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center text-sm font-bold flex-shrink-0">
                    {item.step}
                  </div>
                  <div>
                    <p className="font-semibold text-gray-700 text-sm">{item.title}</p>
                    <p className="text-gray-500 text-sm">{item.desc}</p>
                  </div>
                </div>
              ))}
            </div>
          </Section>

          {/* Liên hệ */}
          <div className="bg-blue-50 rounded-2xl p-5 text-center">
            <p className="text-sm text-blue-800 font-medium">Cần hỗ trợ về đổi trả?</p>
            <p className="text-gray-500 text-sm mt-1">Liên hệ nhân viên tại quầy hoặc nhắn tin qua fanpage nhà thuốc.</p>
          </div>

          <p className="text-gray-400 text-xs text-center pb-2">
            Cập nhật lần cuối: 19/04/2026
          </p>
        </div>
      </div>
      <Footer />
    </div>
  )
}

function Section({ title, icon, children }) {
  return (
    <div className="bg-white rounded-2xl shadow p-6">
      <h2 className="text-base font-bold text-gray-700 mb-4 flex items-center gap-2">
        <span>{icon}</span> {title}
      </h2>
      {children}
    </div>
  )
}
