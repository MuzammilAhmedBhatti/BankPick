import { useState } from "react";
import { useNavigate } from "react-router";
import { Eye, Mail, Lock, ChevronLeft } from "lucide-react";

export function SignIn() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("tanyamyroniuk@gmail.com");
  const [password, setPassword] = useState("........");

  const handleSignIn = () => {
    navigate("/home");
  };

  return (
    <div className="h-screen bg-white flex flex-col">
      <div className="flex items-center justify-between p-4 pt-12">
        <button
          onClick={() => navigate("/onboarding")}
          className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200 active:scale-95 transition-all"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
      </div>

      <div className="flex-1 px-6 pt-8">
        <h1 className="text-4xl mb-12">Sign In</h1>

        <div className="space-y-6">
          <div>
            <label className="text-sm text-gray-400 block mb-2">Email Address</label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400 block mb-2">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full pl-12 pr-12 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              />
              <button
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2"
              >
                <Eye className="w-5 h-5 text-gray-400" />
              </button>
            </div>
          </div>

          <button
            onClick={handleSignIn}
            className="w-full bg-blue-600 text-white py-4 rounded-2xl mt-8 active:scale-95 hover:bg-blue-700 transition-all"
          >
            Sign In
          </button>

          <p className="text-center text-gray-500 mt-4">
            I'm a new user.{" "}
            <button
              onClick={() => navigate("/signup")}
              className="text-blue-600 hover:underline"
            >
              Sign Up
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
