import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, User, Mail, Phone } from "lucide-react";

export function EditProfile() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    fullName: "Tanya Myroniuk",
    email: "tanya myroniuk@gmail.com",
    phone: "+8801712663389",
    day: "28",
    month: "September",
    year: "2000",
  });

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
          <h1 className="text-xl font-semibold">Edit Profile</h1>
          <div className="w-10"></div>
        </div>

        <div className="flex flex-col items-center mb-8">
          <div className="w-24 h-24 rounded-full bg-gray-200 overflow-hidden mb-4">
            <img
              src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop"
              alt="Profile"
              className="w-full h-full object-cover"
            />
          </div>
          <p className="font-semibold text-lg">Tanya Myroniuk</p>
          <p className="text-sm text-gray-500">Senior Designer</p>
        </div>

        <div className="space-y-5">
          <div>
            <label className="text-sm text-gray-400 block mb-2">Full Name</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Email Address</label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Phone Number</label>
            <div className="relative">
              <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="tel"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Birth Date</label>
            <div className="flex gap-3">
              <input
                type="text"
                value={formData.day}
                onChange={(e) => setFormData({ ...formData, day: e.target.value })}
                className="w-20 px-4 py-4 bg-gray-50 rounded-xl text-center focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
              <input
                type="text"
                value={formData.month}
                onChange={(e) => setFormData({ ...formData, month: e.target.value })}
                className="flex-1 px-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
              <input
                type="text"
                value={formData.year}
                onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                className="w-24 px-4 py-4 bg-gray-50 rounded-xl text-center focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <p className="text-center text-sm text-gray-400 pt-4">Joined 28 Jan 2021</p>
        </div>
      </div>
    </div>
  );
}
