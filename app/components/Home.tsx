import { useState } from "react";
import { useNavigate } from "react-router";
import { Search, ArrowUp, ArrowDown, DollarSign, Upload } from "lucide-react";
import { BottomNav } from "./BottomNav";
import { CategoryChartModal } from "./CategoryChartModal";

export function Home() {
  const navigate = useNavigate();
  const [currentCardIndex, setCurrentCardIndex] = useState(0);
  const [showCategoryChart, setShowCategoryChart] = useState(false);

  const cards = [
    {
      number: "4562 1122 4595 7852",
      holder: "AR Jonson",
      expiry: "24/2000",
      cvv: "6986",
      type: "Mastercard",
      gradient: "from-slate-800 via-slate-700 to-slate-800",
    },
  ];

  const transactions = [
    {
      id: 1,
      name: "Apple Store",
      category: "Entertainment",
      amount: -5.99,
      icon: "🍎",
      color: "bg-gray-100",
    },
    {
      id: 2,
      name: "Spotify",
      category: "Music",
      amount: -12.99,
      icon: "🎵",
      color: "bg-green-100",
    },
    {
      id: 3,
      name: "Money Transfer",
      category: "Transaction",
      amount: 300,
      icon: "💸",
      color: "bg-gray-100",
    },
    {
      id: 4,
      name: "Grocery",
      category: "Shopping",
      amount: -88,
      icon: "🛒",
      color: "bg-red-100",
    },
  ];

  return (
    <div className="h-screen bg-white flex flex-col pb-20">
      <div className="p-6 pt-12">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-full bg-gray-200 overflow-hidden">
              <img
                src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop"
                alt="Profile"
                className="w-full h-full object-cover"
              />
            </div>
            <div>
              <p className="text-sm text-gray-500">Welcome back,</p>
              <p className="font-semibold">Tanya Myroniuk</p>
            </div>
          </div>
          <button
            onClick={() => navigate("/search")}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <Search className="w-5 h-5" />
          </button>
        </div>

        <div className="relative mb-6">
          <div
            className={`bg-gradient-to-br ${cards[currentCardIndex].gradient} rounded-3xl p-6 text-white relative overflow-hidden`}
          >
            <div className="absolute top-4 right-4 w-12 h-8 opacity-30">
              <div className="flex gap-1">
                <div className="w-6 h-6 rounded-full bg-white"></div>
                <div className="w-6 h-6 rounded-full bg-white"></div>
              </div>
            </div>

            <div className="w-10 h-10 rounded-lg border-2 border-white/30 flex items-center justify-center mb-6">
              <div className="w-6 h-4 border-2 border-white rounded"></div>
            </div>

            <p className="text-2xl tracking-wider mb-4 font-mono">
              {cards[currentCardIndex].number}
            </p>

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

        <div className="flex gap-3 mb-6">
          <button
            onClick={() => navigate("/send-money")}
            className="flex-1 bg-gray-50 rounded-2xl p-4 flex flex-col items-center gap-2 hover:bg-gray-100 active:scale-95 transition-all"
          >
            <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center">
              <ArrowUp className="w-5 h-5" />
            </div>
            <span className="text-sm">Sent</span>
          </button>
          <button
            onClick={() => navigate("/request-money")}
            className="flex-1 bg-gray-50 rounded-2xl p-4 flex flex-col items-center gap-2 hover:bg-gray-100 active:scale-95 transition-all"
          >
            <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center">
              <ArrowDown className="w-5 h-5" />
            </div>
            <span className="text-sm">Receive</span>
          </button>
          <button
            onClick={() => setShowCategoryChart(true)}
            className="flex-1 bg-gray-50 rounded-2xl p-4 flex flex-col items-center gap-2 hover:bg-gray-100 active:scale-95 transition-all"
          >
            <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center">
              <DollarSign className="w-5 h-5" />
            </div>
            <span className="text-sm">Loan</span>
          </button>
          <button
            onClick={() => navigate("/topup")}
            className="flex-1 bg-gray-50 rounded-2xl p-4 flex flex-col items-center gap-2 hover:bg-gray-100 active:scale-95 transition-all"
          >
            <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center">
              <Upload className="w-5 h-5" />
            </div>
            <span className="text-sm">Topup</span>
          </button>
        </div>

        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold">Transaction</h2>
          <button
            onClick={() => navigate("/transaction-history")}
            className="text-blue-600 text-sm hover:underline"
          >
            See All
          </button>
        </div>

        <div className="space-y-3 overflow-y-auto max-h-64">
          {transactions.map((transaction) => (
            <div
              key={transaction.id}
              onClick={() => navigate("/transaction-detail", { state: { transaction } })}
              className="flex items-center justify-between p-3 bg-gray-50 rounded-2xl hover:bg-gray-100 active:scale-95 transition-all cursor-pointer"
            >
              <div className="flex items-center gap-3">
                <div className={`w-10 h-10 ${transaction.color} rounded-full flex items-center justify-center text-lg`}>
                  {transaction.icon}
                </div>
                <div>
                  <p className="font-medium">{transaction.name}</p>
                  <p className="text-sm text-gray-500">{transaction.category}</p>
                </div>
              </div>
              <p
                className={`font-semibold ${
                  transaction.amount > 0 ? "text-blue-600" : "text-gray-900"
                }`}
              >
                {transaction.amount > 0 ? "+" : ""}${Math.abs(transaction.amount)}
              </p>
            </div>
          ))}
        </div>
      </div>

      <BottomNav />
      <CategoryChartModal isOpen={showCategoryChart} onClose={() => setShowCategoryChart(false)} />
    </div>
  );
}
