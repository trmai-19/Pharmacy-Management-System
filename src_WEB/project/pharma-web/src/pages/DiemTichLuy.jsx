import { useState, useEffect } from 'react'
import { khachHangService } from '../services/api'

export default function DiemTichLuy() {
  const [diem, setDiem] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchDiem = async () => {
      try {
        const res = await khachHangService.getDiem()
        setDiem(res.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchDiem()
  }, [])

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-gray-500">Đang tải...</p>
    </div>
  )

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-2xl mx-auto space-y-6">

        {/* Tổng điểm */}
        <div className="bg-gradient-to-r from-yellow-400 to-yellow-500 rounded-2xl p-6 text-white shadow">
          <p className="text-yellow-100 text-sm">Tổng điểm tích lũy</p>
          <div className="flex items-end gap-2 mt-1">
            <span className="text-5xl font-bold">{diem?.tongDiem ?? 0}</span>
            <span className="text-yellow-100 mb-1">điểm</span>
          </div>
          <p className="text-yellow-100 text-sm mt-2">{diem?.tenKH}</p>
        </div>

        {/* Bảng quy đổi */}
        <div className="bg-white rounded-2xl p-6 shadow">
          <h3 className="font-bold text-gray-700 mb-4">Bảng quy đổi điểm</h3>
          <div className="space-y-3">
            {[
              { diem: 100, quidoi: '10.000đ', icon: '🎁' },
              { diem: 500, quidoi: '55.000đ', icon: '🎀' },
              { diem: 1000, quidoi: '120.000đ', icon: '🏅' },
            ].map((item) => (
              <div key={item.diem}
                className="flex items-center justify-between bg-gray-50 rounded-xl px-4 py-3">
                <div className="flex items-center gap-3">
                  <span className="text-2xl">{item.icon}</span>
                  <span className="font-medium text-gray-700">{item.diem} điểm</span>
                </div>
                <span className="text-blue-600 font-bold">{item.quidoi}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Lịch sử điểm */}
        <div className="bg-white rounded-2xl p-6 shadow">
          <h3 className="font-bold text-gray-700 mb-4">Lịch sử điểm</h3>
          {diem?.lichSuDiem?.length === 0 ? (
            <p className="text-gray-400 text-sm text-center py-4">
              Chưa có giao dịch điểm nào
            </p>
          ) : (
            <div className="space-y-3">
              {diem?.lichSuDiem?.map((item) => (
                <div key={item.maDTL}
                  className="flex justify-between items-center border-b pb-3 last:border-0">
                  <div>
                    <p className="font-medium text-gray-700">{item.loaiGD}</p>
                    <p className="text-sm text-gray-400">{item.maDTL}</p>
                  </div>
                  <span className={`font-bold text-lg ${
                    item.sl >= 0 ? 'text-green-500' : 'text-red-500'
                  }`}>
                    {item.sl >= 0 ? '+' : ''}{item.sl}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  )
}