import { useNavigate } from "react-router";
import { ChevronLeft, ChevronRight, QrCode } from "lucide-react";
import { BottomNav } from "./BottomNav";

export function Settings() {
  const navigate = useNavigate();

  const generalSettings = [
    { label: "My Profile", value: null, path: "/profile" },
    { label: "Contact Us", value: null, path: "/contact-us" },
  ];

  const securitySettings = [
    { label: "Change Password", value: null, path: "/change-password" },
    { label: "Terms & Conditions", value: null, path: "/terms-condition" },
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
          <h1 className="text-xl font-semibold">Settings</h1>
          <button className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all">
            <QrCode className="w-5 h-5" />
          </button>
        </div>

        <div className="mb-8">
          <p className="text-sm text-gray-400 mb-4 uppercase tracking-wide">General</p>
          <div className="space-y-1">
            {generalSettings.map((setting, index) => (
              <button
                key={index}
                onClick={() => setting.path && navigate(setting.path)}
                className="w-full flex items-center justify-between p-4 bg-white hover:bg-gray-50 active:scale-[0.99] transition-all rounded-xl"
              >
                <span className="text-base">{setting.label}</span>
                <div className="flex items-center gap-2">
                  {setting.value && <span className="text-gray-400">{setting.value}</span>}
                  <ChevronRight className="w-5 h-5 text-gray-400" />
                </div>
              </button>
            ))}
          </div>
        </div>

        <div>
          <p className="text-sm text-gray-400 mb-4 uppercase tracking-wide">Security</p>
          <div className="space-y-1">
            {securitySettings.map((setting, index) => (
              <button
                key={index}
                onClick={() => setting.path && navigate(setting.path)}
                className="w-full flex items-center justify-between p-4 bg-white hover:bg-gray-50 active:scale-[0.99] transition-all rounded-xl"
              >
                <span className="text-base">{setting.label}</span>
                <ChevronRight className="w-5 h-5 text-gray-400" />
              </button>
            ))}
          </div>
        </div>
      </div>

      <BottomNav />
    </div>
  );
}
