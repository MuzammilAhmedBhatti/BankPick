import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft } from "lucide-react";

export function Topup() {
  const navigate = useNavigate();
  const [amount, setAmount] = useState("100.00");
  const [selectedMethod, setSelectedMethod] = useState("card");

  const handleTopup = () => {
    const methodName = paymentMethods.find(m => m.id === selectedMethod)?.name || "Card";
    navigate("/transaction-success", {
      state: {
        type: "Top Up",
        recipient: methodName,
        amount: amount,
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

  const quickAmounts = [50, 100, 200, 500];

  const paymentMethods = [
    { id: "card", name: "Credit/Debit Card", icon: "💳" },
    { id: "bank", name: "Bank Transfer", icon: "🏦" },
    { id: "wallet", name: "Digital Wallet", icon: "👛" },
    { id: "paypal", name: "PayPal", icon: "💵" },
  ];

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
          <h1 className="text-xl font-semibold">Top Up</h1>
          <div className="w-10"></div>
        </div>

        <div className="mb-6">
          <div className="bg-gradient-to-br from-slate-800 via-slate-700 to-slate-800 rounded-3xl p-6 text-white">
            <p className="text-sm opacity-70 mb-2">Current Balance</p>
            <p className="text-3xl font-bold">$8,545.00</p>
          </div>
        </div>

        <div className="mb-6">
          <label className="text-sm text-gray-600 block mb-3">Quick Select</label>
          <div className="grid grid-cols-4 gap-3">
            {quickAmounts.map((amt) => (
              <button
                key={amt}
                onClick={() => setAmount(amt.toString() + ".00")}
                className={`py-3 rounded-xl border-2 transition-all hover:scale-105 active:scale-95 ${
                  amount === amt.toString() + ".00"
                    ? "border-blue-600 bg-blue-50 text-blue-600"
                    : "border-gray-200 bg-white text-gray-900"
                }`}
              >
                ${amt}
              </button>
            ))}
          </div>
        </div>

        <div className="bg-gray-50 rounded-2xl p-6 mb-6">
          <label className="text-sm text-gray-500 block mb-2">Enter Amount</label>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl text-gray-400">USD</span>
            <input
              type="text"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="text-4xl font-semibold bg-transparent border-none outline-none w-full"
            />
          </div>
        </div>

        <div className="mb-6">
          <label className="text-sm text-gray-600 block mb-3">Payment Method</label>
          <div className="space-y-2">
            {paymentMethods.map((method) => (
              <button
                key={method.id}
                onClick={() => setSelectedMethod(method.id)}
                className={`w-full flex items-center justify-between p-4 rounded-xl border-2 transition-all hover:bg-gray-50 active:scale-[0.99] ${
                  selectedMethod === method.id
                    ? "border-blue-600 bg-blue-50"
                    : "border-gray-200 bg-white"
                }`}
              >
                <div className="flex items-center gap-3">
                  <span className="text-2xl">{method.icon}</span>
                  <span className="font-medium">{method.name}</span>
                </div>
                {selectedMethod === method.id && (
                  <div className="w-5 h-5 bg-blue-600 rounded-full flex items-center justify-center">
                    <div className="w-2 h-2 bg-white rounded-full"></div>
                  </div>
                )}
              </button>
            ))}
          </div>
        </div>

        <button
          onClick={handleTopup}
          className="w-full bg-blue-600 text-white py-4 rounded-2xl hover:bg-blue-700 active:scale-95 transition-all"
        >
          Continue to Payment
        </button>
      </div>
    </div>
  );
}
