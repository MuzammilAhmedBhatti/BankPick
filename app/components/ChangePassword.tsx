import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, Lock, Eye } from "lucide-react";

export function ChangePassword() {
  const navigate = useNavigate();
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [formData, setFormData] = useState({
    currentPassword: "........",
    newPassword: "........",
    confirmPassword: "........",
  });

  const handleSubmit = () => {
    if (formData.newPassword === formData.confirmPassword) {
      navigate(-1);
    }
  };

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
          <h1 className="text-xl font-semibold">Change Password</h1>
          <div className="w-10"></div>
        </div>

        <div className="space-y-6">
          <div>
            <label className="text-sm text-gray-400 block mb-2">Current Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="password"
                value={formData.currentPassword}
                onChange={(e) => setFormData({ ...formData, currentPassword: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">New Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type={showNewPassword ? "text" : "password"}
                value={formData.newPassword}
                onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
                className="w-full pl-12 pr-12 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
              <button
                onClick={() => setShowNewPassword(!showNewPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2"
              >
                <Eye className="w-5 h-5 text-gray-400" />
              </button>
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Confirm New Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="password"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
            <p className="text-sm text-gray-400 mt-2">Both Passwords Must Match</p>
          </div>

          <button
            onClick={handleSubmit}
            className="w-full bg-blue-600 text-white py-4 rounded-2xl mt-8 hover:bg-blue-700 active:scale-95 transition-all"
          >
            Change Password
          </button>
        </div>
      </div>
    </div>
  );
}
