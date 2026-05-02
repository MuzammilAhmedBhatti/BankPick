import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, Bell } from "lucide-react";
import { BottomNav } from "./BottomNav";
import { LineChart, Line, XAxis, ResponsiveContainer } from "recharts";

export function Statistics() {
  const navigate = useNavigate();
  const [selectedMonth, setSelectedMonth] = useState("Jan");

  const months = ["Oct", "Nov", "Dec", "Jan", "Feb", "Mar"];

  const balanceData = [
    { month: "Oct", balance: 6000 },
    { month: "Nov", balance: 7200 },
    { month: "Dec", balance: 5800 },
    { month: "Jan", balance: 8500 },
    { month: "Feb", balance: 7800 },
    { month: "Mar", balance: 9200 },
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
          <h1 className="text-xl font-semibold">Statistics</h1>
          <button
            onClick={() => navigate("/notifications")}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <Bell className="w-5 h-5" />
          </button>
        </div>

        <div className="mb-6">
          <p className="text-sm text-gray-400 text-center mb-2">Current Balance</p>
          <p className="text-4xl font-bold text-center">$8,545.00</p>
        </div>

        <div className="mb-6 h-48">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={balanceData}>
              <XAxis
                dataKey="month"
                axisLine={false}
                tickLine={false}
                tick={{ fill: "#9CA3AF", fontSize: 12 }}
              />
              <Line
                type="monotone"
                dataKey="balance"
                stroke="#3B82F6"
                strokeWidth={3}
                dot={{ fill: "#3B82F6", r: 6 }}
                activeDot={{ r: 8 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
          {months.map((month) => (
            <button
              key={month}
              onClick={() => setSelectedMonth(month)}
              className={`px-6 py-2 rounded-full text-sm transition-all active:scale-95 ${
                selectedMonth === month
                  ? "bg-blue-600 text-white"
                  : "bg-gray-100 text-gray-600 hover:bg-gray-200"
              }`}
            >
              {month}
            </button>
          ))}
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

        <div className="space-y-3">
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
    </div>
  );
}
