import { useNavigate } from "react-router";
import { ChevronLeft, Bell } from "lucide-react";

export function Notifications() {
  const navigate = useNavigate();

  const notifications = [
    {
      id: 1,
      title: "Payment Received",
      message: "You received $300 from John Doe",
      time: "2 hours ago",
      unread: true,
      icon: "💰",
      color: "bg-green-100",
    },
    {
      id: 2,
      title: "Card Payment Successful",
      message: "Your payment of $5.99 to Apple Store was successful",
      time: "5 hours ago",
      unread: true,
      icon: "✅",
      color: "bg-blue-100",
    },
    {
      id: 3,
      title: "Security Alert",
      message: "New login detected from Chrome on Windows",
      time: "1 day ago",
      unread: false,
      icon: "🔒",
      color: "bg-orange-100",
    },
    {
      id: 4,
      title: "Monthly Statement Ready",
      message: "Your April statement is now available",
      time: "2 days ago",
      unread: false,
      icon: "📄",
      color: "bg-purple-100",
    },
    {
      id: 5,
      title: "Spending Limit Alert",
      message: "You've reached 80% of your monthly spending limit",
      time: "3 days ago",
      unread: false,
      icon: "⚠️",
      color: "bg-yellow-100",
    },
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
          <h1 className="text-xl font-semibold">Notifications</h1>
          <button className="text-blue-600 text-sm hover:underline">
            Mark all read
          </button>
        </div>

        <div className="space-y-3 overflow-y-auto" style={{ maxHeight: "calc(100vh - 140px)" }}>
          {notifications.map((notification) => (
            <div
              key={notification.id}
              className={`p-4 rounded-2xl hover:bg-gray-50 active:scale-[0.99] transition-all cursor-pointer ${
                notification.unread ? "bg-blue-50" : "bg-white border border-gray-100"
              }`}
            >
              <div className="flex gap-3">
                <div className={`w-12 h-12 ${notification.color} rounded-full flex items-center justify-center text-xl flex-shrink-0`}>
                  {notification.icon}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between mb-1">
                    <h3 className={`font-semibold ${notification.unread ? "text-gray-900" : "text-gray-700"}`}>
                      {notification.title}
                    </h3>
                    {notification.unread && (
                      <div className="w-2 h-2 bg-blue-600 rounded-full flex-shrink-0 mt-2"></div>
                    )}
                  </div>
                  <p className="text-sm text-gray-600 mb-1">{notification.message}</p>
                  <p className="text-xs text-gray-400">{notification.time}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
