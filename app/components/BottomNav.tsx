import { useNavigate, useLocation } from "react-router";
import { Home, CreditCard, PieChart, Settings } from "lucide-react";

export function BottomNav() {
  const navigate = useNavigate();
  const location = useLocation();

  const navItems = [
    { path: "/home", icon: Home, label: "Home" },
    { path: "/my-cards", icon: CreditCard, label: "My Cards" },
    { path: "/statistics", icon: PieChart, label: "Statistics" },
    { path: "/settings", icon: Settings, label: "Settings" },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100">
      <div className="max-w-md mx-auto px-4 py-3">
        <div className="flex items-center justify-around">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            const Icon = item.icon;
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className="flex flex-col items-center gap-1 py-2 px-4 rounded-xl transition-all active:scale-95"
              >
                <Icon
                  className={`w-6 h-6 transition-colors ${
                    isActive ? "text-blue-600" : "text-gray-400"
                  }`}
                />
                <span
                  className={`text-xs transition-colors ${
                    isActive ? "text-blue-600" : "text-gray-400"
                  }`}
                >
                  {item.label}
                </span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
