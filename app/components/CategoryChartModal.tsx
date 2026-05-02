import { ChevronLeft } from "lucide-react";

interface CategoryChartModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export function CategoryChartModal({ isOpen, onClose }: CategoryChartModalProps) {
  if (!isOpen) return null;

  const categories = [
    { name: "Transaction", color: "bg-orange-400", percentage: 55 },
    { name: "Transfer", color: "bg-pink-300", percentage: 25 },
    { name: "Travel", color: "bg-cyan-400", percentage: 15 },
    { name: "Food", color: "bg-purple-400", percentage: 20 },
    { name: "Shopping", color: "bg-orange-500", percentage: 10 },
    { name: "Car", color: "bg-teal-400", percentage: 5 },
  ];

  const transactions = [
    {
      id: 1,
      name: "Apple Store",
      category: "Entertainment",
      amount: -5.99,
      icon: "🍎",
      color: "bg-gray-100",
      time: "Today",
    },
    {
      id: 2,
      name: "Spotify",
      category: "Music",
      amount: -12.99,
      icon: "🎵",
      color: "bg-green-100",
      time: "Today",
    },
    {
      id: 3,
      name: "Spotify",
      category: "Music",
      amount: -12.99,
      icon: "🎵",
      color: "bg-green-100",
      time: "Last 7 Day",
    },
  ];

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-end justify-center animate-in fade-in duration-200">
      <div className="bg-white rounded-t-3xl w-full max-w-md max-h-[85vh] overflow-y-auto animate-in slide-in-from-bottom duration-300">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <button
              onClick={onClose}
              className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
            <h2 className="text-xl font-semibold">Category Chart</h2>
            <div className="w-10"></div>
          </div>

          <div className="flex items-center justify-center mb-8 relative">
            <svg width="200" height="200" viewBox="0 0 200 200" className="transform -rotate-90">
              <circle
                cx="100"
                cy="100"
                r="80"
                fill="none"
                stroke="#E5E7EB"
                strokeWidth="30"
              />
              <circle
                cx="100"
                cy="100"
                r="80"
                fill="none"
                stroke="url(#gradient1)"
                strokeWidth="30"
                strokeDasharray={`${(55 / 100) * 502} 502`}
                strokeLinecap="round"
              />
              <circle
                cx="100"
                cy="100"
                r="80"
                fill="none"
                stroke="#EC4899"
                strokeWidth="30"
                strokeDasharray={`${(25 / 100) * 502} 502`}
                strokeDashoffset={`-${(55 / 100) * 502}`}
                strokeLinecap="round"
              />
              <circle
                cx="100"
                cy="100"
                r="80"
                fill="none"
                stroke="#06B6D4"
                strokeWidth="30"
                strokeDasharray={`${(20 / 100) * 502} 502`}
                strokeDashoffset={`-${(80 / 100) * 502}`}
                strokeLinecap="round"
              />
              <defs>
                <linearGradient id="gradient1" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" stopColor="#A78BFA" />
                  <stop offset="100%" stopColor="#8B5CF6" />
                </linearGradient>
              </defs>
            </svg>
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <p className="text-4xl font-bold">55%</p>
              <p className="text-sm text-gray-500">Transaction</p>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3 mb-6">
            {categories.map((category, index) => (
              <div key={index} className="flex items-center gap-2">
                <div className={`w-3 h-3 rounded-full ${category.color}`}></div>
                <span className="text-sm text-gray-600">{category.name}</span>
              </div>
            ))}
          </div>

          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold">Transaction History</h3>
            <button className="text-blue-600 text-sm hover:underline">See All</button>
          </div>

          <div className="mb-4">
            <p className="text-sm text-gray-500 mb-2">Today</p>
            <div className="space-y-2">
              {transactions.slice(0, 2).map((transaction) => (
                <div
                  key={transaction.id}
                  className="flex items-center justify-between p-3 bg-gray-50 rounded-2xl"
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
          </div>

          <div>
            <p className="text-sm text-gray-500 mb-2">Last 7 Day</p>
            <div className="space-y-2">
              {transactions.slice(2).map((transaction) => (
                <div
                  key={transaction.id}
                  className="flex items-center justify-between p-3 bg-gray-50 rounded-2xl"
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
          </div>
        </div>
      </div>
    </div>
  );
}
