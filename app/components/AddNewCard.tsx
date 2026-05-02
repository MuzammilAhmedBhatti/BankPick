import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, CreditCard, User } from "lucide-react";

export function AddNewCard() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    cardholderName: "Tanya Myroniuk",
    expiryDate: "09/06/2024",
    cvv: "6986",
    cardNumber: "4562 1122 4595 7852",
  });

  return (
    <div className="h-screen bg-white flex flex-col">
      <div className="p-6 pt-12">
        <div className="flex items-center justify-between mb-8">
          <button
            onClick={() => navigate(-1)}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          <h1 className="text-xl font-semibold">Add New Card</h1>
          <div className="w-10"></div>
        </div>

        <div className="mb-8">
          <div className="bg-gradient-to-br from-slate-800 via-slate-700 to-slate-800 rounded-3xl p-6 text-white relative overflow-hidden">
            <div className="absolute top-4 right-4 w-12 h-8 opacity-30">
              <div className="flex gap-1">
                <div className="w-6 h-6 rounded-full bg-white"></div>
                <div className="w-6 h-6 rounded-full bg-white"></div>
              </div>
            </div>

            <div className="w-10 h-10 rounded-lg border-2 border-white/30 flex items-center justify-center mb-6">
              <div className="w-6 h-4 border-2 border-white rounded"></div>
            </div>

            <p className="text-2xl tracking-wider mb-4 font-mono">4562 1122 4595 7852</p>

            <div className="flex justify-between items-end">
              <div>
                <p className="text-xs opacity-70 mb-1">AR Jonson</p>
                <div className="flex gap-4 text-xs">
                  <div>
                    <p className="opacity-70">Expiry Date</p>
                    <p>24/2000</p>
                  </div>
                  <div>
                    <p className="opacity-70">CVV</p>
                    <p>6986</p>
                  </div>
                </div>
              </div>
              <div className="flex flex-col items-end">
                <div className="flex gap-1 mb-1">
                  <div className="w-6 h-6 rounded-full bg-red-500"></div>
                  <div className="w-6 h-6 rounded-full bg-orange-500 -ml-2"></div>
                </div>
                <p className="text-xs">Mastercard</p>
              </div>
            </div>
          </div>
        </div>

        <div className="space-y-5">
          <div>
            <label className="text-sm text-gray-400 block mb-2">Cardholder Name</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={formData.cardholderName}
                onChange={(e) => setFormData({ ...formData, cardholderName: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div className="flex gap-3">
            <div className="flex-1">
              <label className="text-sm text-gray-400 block mb-2">Expiry Date</label>
              <input
                type="text"
                value={formData.expiryDate}
                onChange={(e) => setFormData({ ...formData, expiryDate: e.target.value })}
                className="w-full px-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
            <div className="flex-1">
              <label className="text-sm text-gray-400 block mb-2">4-digit CVV</label>
              <input
                type="text"
                value={formData.cvv}
                onChange={(e) => setFormData({ ...formData, cvv: e.target.value })}
                className="w-full px-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Card Number</label>
            <div className="relative">
              <CreditCard className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={formData.cardNumber}
                onChange={(e) => setFormData({ ...formData, cardNumber: e.target.value })}
                className="w-full pl-12 pr-12 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
              <div className="absolute right-3 top-1/2 -translate-y-1/2 flex gap-1">
                <div className="w-6 h-6 rounded-full bg-red-500"></div>
                <div className="w-6 h-6 rounded-full bg-orange-500 -ml-2"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
