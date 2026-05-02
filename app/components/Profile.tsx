import { useNavigate } from "react-router";
import { ChevronLeft, ChevronRight, UserPlus } from "lucide-react";

export function Profile() {
  const navigate = useNavigate();

  const menuItems = [
    { label: "Personal Information", icon: "👤", path: "/edit-profile" },
    { label: "Banks and Cards", icon: "🏦", path: "/all-cards" },
    { label: "Notifications", icon: "🔔", path: "/notifications", badge: 2 },
    { label: "Settings", icon: "⚙️", path: "/settings" },
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
          <h1 className="text-xl font-semibold">Profile</h1>
          <button className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all">
            <UserPlus className="w-5 h-5" />
          </button>
        </div>

        <div className="flex items-center gap-4 mb-8 pb-8 border-b border-gray-100">
          <div className="w-16 h-16 rounded-full bg-gray-200 overflow-hidden">
            <img
              src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop"
              alt="Profile"
              className="w-full h-full object-cover"
            />
          </div>
          <div>
            <p className="font-semibold text-lg">Tanya Myroniuk</p>
            <p className="text-sm text-gray-500">Senior Designer</p>
          </div>
        </div>

        <div className="space-y-2">
          {menuItems.map((item, index) => (
            <button
              key={index}
              onClick={() => item.path && navigate(item.path)}
              className="w-full flex items-center justify-between p-4 hover:bg-gray-50 active:scale-[0.99] transition-all rounded-xl"
            >
              <div className="flex items-center gap-3">
                <span className="text-2xl">{item.icon}</span>
                <span className="text-base">{item.label}</span>
              </div>
              <div className="flex items-center gap-2">
                {item.badge && (
                  <span className="w-6 h-6 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                    {item.badge}
                  </span>
                )}
                <ChevronRight className="w-5 h-5 text-gray-400" />
              </div>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
