import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, Plus } from "lucide-react";
import { BottomNav } from "./BottomNav";
import { Slider } from "./ui/slider";

export function MyCards() {
  const navigate = useNavigate();
  const [spendingLimit, setSpendingLimit] = useState([8545]);

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
        <div className="flex items-center justify-between mb-8">
          <button
            onClick={() => navigate(-1)}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          <h1 className="text-xl font-semibold">My Cards</h1>
          <button
            onClick={() => navigate("/all-cards")}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <Plus className="w-5 h-5" />
          </button>
        </div>

        <div className="mb-6">
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

        <div className="space-y-3 mb-6">
          {transactions.map((transaction) => (
            <div
              key={transaction.id}
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
              <p className="font-semibold text-gray-900">
                - ${Math.abs(transaction.amount)}
              </p>
            </div>
          ))}
        </div>

        <div className="bg-gray-50 rounded-3xl p-6">
          <h3 className="text-lg font-semibold mb-4">Monthly spending limit</h3>
          <div className="mb-4">
            <p className="text-sm text-gray-600 mb-2">Amount: ${spendingLimit[0].toLocaleString()}.00</p>
            <Slider
              value={spendingLimit}
              onValueChange={setSpendingLimit}
              max={10000}
              step={100}
              className="mb-2"
            />
            <div className="flex justify-between text-xs text-gray-500">
              <span>$0</span>
              <span>$4,600</span>
              <span>$10,000</span>
            </div>
          </div>
        </div>
      </div>

      <BottomNav />
    </div>
  );
}
