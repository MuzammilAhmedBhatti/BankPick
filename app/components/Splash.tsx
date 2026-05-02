import { useEffect } from "react";
import { useNavigate } from "react-router";

export function Splash() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/onboarding");
    }, 2500);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="h-screen bg-white flex flex-col items-center justify-center">
      <div className="flex flex-col items-center gap-6 animate-in fade-in duration-1000">
        <div className="relative">
          <svg width="120" height="120" viewBox="0 0 120 120" fill="none">
            <circle cx="40" cy="60" r="30" fill="#0EA5E9" opacity="0.8" />
            <circle cx="80" cy="60" r="30" fill="#06B6D4" opacity="0.8" />
          </svg>
        </div>
        <h1 className="text-4xl font-bold tracking-tight">BANKPICK</h1>
      </div>
    </div>
  );
}
