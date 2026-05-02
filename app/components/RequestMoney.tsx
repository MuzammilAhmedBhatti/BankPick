import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, User, Mail } from "lucide-react";

export function RequestMoney() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    payerName: "Tanya Myroniuk",
    email: "tanya myroniuk@gmail.com",
    description: "Tanya Myroniuk",
    day: "28",
    month: "09",
    year: "2000",
    amount: "26.00.00",
  });

  const handleRequestMoney = () => {
    navigate("/transaction-success", {
      state: {
        type: "Request Money",
        recipient: formData.payerName,
        amount: formData.amount.replace(".00", ""),
        date: new Date().toLocaleDateString("en-US", {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        }),
        time: new Date().toLocaleTimeString("en-US", {
          hour: '2-digit',
          minute: '2-digit'
        }),
        transactionId: `TRX${Date.now()}`,
      },
    });
  };

  return (
    <div className="h-screen bg-white flex flex-col overflow-y-auto">
      <div className="p-6 pt-12">
        <div className="flex items-center justify-between mb-8">
          <button
            onClick={() => navigate(-1)}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          <h1 className="text-xl font-semibold">Request Money</h1>
          <div className="w-10"></div>
        </div>

        <div className="space-y-5 mb-6">
          <div>
            <label className="text-sm text-gray-400 block mb-2">Payer Name</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={formData.payerName}
                onChange={(e) => setFormData({ ...formData, payerName: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Email Address</label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Description</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Monthly Due By</label>
            <div className="flex gap-3">
              <input
                type="text"
                value={formData.day}
                onChange={(e) => setFormData({ ...formData, day: e.target.value })}
                className="w-20 px-4 py-4 bg-gray-50 rounded-xl text-center focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
              <input
                type="text"
                value={formData.month}
                onChange={(e) => setFormData({ ...formData, month: e.target.value })}
                className="w-20 px-4 py-4 bg-gray-50 rounded-xl text-center focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
              <input
                type="text"
                value={formData.year}
                onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                className="flex-1 px-4 py-4 bg-gray-50 rounded-xl text-center focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>
        </div>

        <div className="bg-gray-50 rounded-2xl p-6 mb-6">
          <div className="flex items-baseline justify-between mb-2">
            <label className="text-sm text-gray-500">Enter Your Amount</label>
            <button className="text-sm text-red-500 hover:underline">Change Currency?</button>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl text-gray-400">USD</span>
            <input
              type="text"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              className="text-4xl font-semibold bg-transparent border-none outline-none w-full"
            />
          </div>
        </div>

        <button
          onClick={handleRequestMoney}
          className="w-full bg-blue-600 text-white py-4 rounded-2xl hover:bg-blue-700 active:scale-95 transition-all"
        >
          Request Money
        </button>
      </div>
    </div>
  );
}
