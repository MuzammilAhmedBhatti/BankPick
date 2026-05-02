import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, X, Search } from "lucide-react";

export function SearchScreen() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");

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
    {
      id: 5,
      name: "Apple Store",
      category: "Entertainment",
      amount: -5.99,
      icon: "📱",
      color: "bg-red-100",
    },
    {
      id: 6,
      name: "Money Transfer",
      category: "Transaction",
      amount: 300,
      icon: "💸",
      color: "bg-gray-100",
    },
    {
      id: 7,
      name: "Apple Store",
      category: "Entertainment",
      amount: -5.99,
      icon: "🍎",
      color: "bg-gray-100",
    },
    {
      id: 8,
      name: "Spotify",
      category: "Music",
      amount: -12.99,
      icon: "🎵",
      color: "bg-green-100",
    },
  ];

  const filteredTransactions = transactions.filter((t) =>
    t.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="h-screen bg-white flex flex-col">
      <div className="p-6 pt-12">
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => navigate(-1)}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          <h1 className="text-xl font-semibold">Search</h1>
          <button
            onClick={() => navigate(-1)}
            className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="relative mb-6">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="Search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-12 pr-12 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery("")}
              className="absolute right-3 top-1/2 -translate-y-1/2"
            >
              <X className="w-5 h-5 text-gray-400" />
            </button>
          )}
        </div>

        <div className="space-y-3 overflow-y-auto" style={{ maxHeight: "calc(100vh - 220px)" }}>
          {filteredTransactions.map((transaction) => (
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
    </div>
  );
}
