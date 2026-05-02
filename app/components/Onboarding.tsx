import { useState } from "react";
import { useNavigate } from "react-router";

export function Onboarding() {
  const navigate = useNavigate();
  const [currentSlide, setCurrentSlide] = useState(0);

  const slides = [
    {
      title: "Fastest Payment in the world",
      description: "Integrate multiple payment methods to help you up the process quickly",
      illustration: (
        <div className="w-full h-64 flex items-center justify-center mb-8">
          <div className="relative">
            <div className="w-72 h-48 border-4 border-blue-400 rounded-3xl bg-blue-50 flex items-center justify-center relative">
              <div className="absolute -left-12 top-1/2 -translate-y-1/2">
                <div className="w-24 h-32 relative">
                  <div className="absolute inset-0 bg-blue-600 rounded-lg"></div>
                  <div className="absolute top-2 left-2 w-10 h-10 bg-blue-800 rounded-full"></div>
                  <div className="absolute bottom-8 left-1/2 -translate-x-1/2 w-16 h-16 bg-blue-400 rounded-lg"></div>
                </div>
              </div>
              <div className="flex gap-8 items-center justify-center">
                <div className="space-y-2">
                  {[40, 60, 80, 50, 70].map((height, i) => (
                    <div key={i} className={`w-8 bg-blue-600 rounded`} style={{ height: `${height}px` }}></div>
                  ))}
                </div>
                <div className="flex gap-4">
                  <div className="px-4 py-2 bg-blue-200 rounded-lg text-sm font-semibold">55%</div>
                  <div className="px-4 py-2 bg-blue-200 rounded-lg text-sm font-semibold">60%</div>
                </div>
              </div>
              <div className="absolute -right-16 bottom-8">
                <div className="w-16 h-16 bg-yellow-400 rounded-full flex items-center justify-center">
                  <span className="text-2xl">$</span>
                </div>
                <div className="w-20 h-24 bg-blue-600 rounded-full -mt-4 ml-8"></div>
              </div>
            </div>
          </div>
        </div>
      ),
    },
    {
      title: "The most Secure Platform for Customer",
      description: "Built-in Fingerprint, face recognition and more, keeping you completely safe",
      illustration: (
        <div className="w-full h-64 flex items-center justify-center mb-8">
          <div className="relative">
            <div className="absolute left-0 top-8 text-4xl">👤</div>
            <div className="absolute left-4 bottom-12 text-3xl">%</div>
            <div className="absolute top-0 right-20 text-2xl">$</div>

            <div className="w-72 h-48 border-4 border-blue-400 rounded-3xl bg-blue-50 flex items-center justify-center relative">
              <div className="absolute left-8 top-8 w-20 h-24 bg-blue-600 rounded-xl p-2">
                <div className="w-full h-2 bg-white rounded mb-1"></div>
                <div className="w-full h-2 bg-white rounded mb-1"></div>
                <div className="w-full h-2 bg-white rounded mb-2"></div>
                <div className="flex gap-1 mt-2">
                  {[20, 30, 15, 25, 18].map((h, i) => (
                    <div key={i} className="flex-1 bg-white rounded" style={{ height: `${h}px` }}></div>
                  ))}
                </div>
              </div>

              <div className="absolute right-8 top-8 w-32 h-24 bg-gray-900 rounded-xl p-3 flex items-center justify-center">
                <div className="w-16 h-16 border-4 border-blue-400 border-t-transparent rounded-full"></div>
              </div>

              <div className="absolute right-0 -bottom-2">
                <div className="w-24 h-32 relative">
                  <div className="w-full h-full bg-blue-600 rounded-lg"></div>
                  <div className="absolute top-4 left-1/2 -translate-x-1/2 w-12 h-12 bg-blue-400 rounded-full"></div>
                  <div className="absolute bottom-8 left-4 w-16 h-16 bg-blue-800 rounded-lg"></div>
                </div>
              </div>
            </div>

            <div className="absolute -bottom-8 left-1/2 -translate-x-1/2 w-48 h-20 bg-gray-900 rounded-full flex items-center justify-center gap-2">
              <div className="w-8 h-8 border-2 border-white rounded"></div>
              <div className="w-8 h-8 border-2 border-white rounded"></div>
              <div className="absolute -right-8 -top-4 w-12 h-12 bg-yellow-400 rounded-full flex items-center justify-center">
                <span className="text-xl">$</span>
              </div>
            </div>
          </div>
        </div>
      ),
    },
    {
      title: "Paying for Everything is Easy and Convenient",
      description: "Built-in Fingerprint, face recognition and more, keeping you completely safe",
      illustration: (
        <div className="w-full h-64 flex items-center justify-center mb-8">
          <div className="relative">
            <div className="flex flex-col items-center">
              <div className="relative mb-8">
                <div className="flex gap-8 items-end">
                  <div className="w-20 h-32 bg-gray-800 rounded-lg relative">
                    <div className="absolute top-4 left-1/2 -translate-x-1/2 w-12 h-12 bg-gray-600 rounded-full"></div>
                    <div className="absolute bottom-4 left-1/2 -translate-x-1/2 text-white text-lg">$</div>
                  </div>
                  <div className="w-20 h-32 bg-gray-800 rounded-lg relative">
                    <div className="absolute top-4 left-1/2 -translate-x-1/2 w-12 h-12 bg-gray-600 rounded-full"></div>
                    <div className="absolute bottom-4 left-1/2 -translate-x-1/2 text-white text-lg">$</div>
                  </div>
                </div>

                <div className="absolute -top-12 left-1/2 -translate-x-1/2 w-16 h-16 bg-yellow-400 rounded-full flex items-center justify-center">
                  <span className="text-2xl">🏆</span>
                </div>

                <div className="absolute -right-24 top-8">
                  <div className="w-24 h-32 relative">
                    <div className="w-full h-full bg-blue-600 rounded-lg"></div>
                    <div className="absolute top-4 left-1/2 -translate-x-1/2 w-12 h-12 bg-blue-400 rounded-full"></div>
                    <div className="absolute bottom-8 left-4 w-16 h-16 bg-blue-800 rounded-lg"></div>
                  </div>
                </div>
              </div>

              <div className="flex gap-2 items-end">
                {[60, 80, 50, 90, 70, 85, 65].map((height, i) => (
                  <div
                    key={i}
                    className="w-6 bg-yellow-400 rounded-t-lg"
                    style={{ height: `${height}px` }}
                  ></div>
                ))}
              </div>

              <div className="mt-4 flex gap-2">
                <div className="w-12 h-12 bg-yellow-500 rounded-full flex items-center justify-center">
                  <span className="text-lg font-bold">$</span>
                </div>
                <div className="w-12 h-12 bg-yellow-500 rounded-full flex items-center justify-center">
                  <span className="text-lg font-bold">$</span>
                </div>
              </div>

              <div className="absolute -bottom-12 left-0 w-32 h-24">
                
              </div>
              <div className="absolute -bottom-12 right-0 w-32 h-24">
                
              </div>
            </div>
          </div>
        </div>
      ),
    },
  ];

  const handleNext = () => {
    if (currentSlide < slides.length - 1) {
      setCurrentSlide(currentSlide + 1);
    } else {
      navigate("/signin");
    }
  };

  return (
    <div className="h-screen bg-white flex flex-col">
      <div className="flex-1 flex flex-col justify-between p-6 pt-20">
        <button
          onClick={() => navigate("/signin")}
          className="self-start text-2xl hover:opacity-70 transition-opacity"
        >
          •••
        </button>

        <div className="flex-1 flex flex-col items-center justify-center">
          <div className="w-full max-w-sm">
            {slides[currentSlide].illustration}

            <div className="flex justify-center gap-2 mb-8">
              {slides.map((_, index) => (
                <div
                  key={index}
                  className={`h-2 rounded-full transition-all ${
                    index === currentSlide
                      ? "w-8 bg-blue-600"
                      : "w-2 bg-gray-300"
                  }`}
                />
              ))}
            </div>

            <h2 className="text-2xl font-bold text-center mb-4 px-4">
              {slides[currentSlide].title}
            </h2>
            <p className="text-gray-500 text-center px-6 leading-relaxed">
              {slides[currentSlide].description}
            </p>
          </div>
        </div>

        <button
          onClick={handleNext}
          className="w-full bg-blue-600 text-white py-4 rounded-2xl hover:bg-blue-700 active:scale-95 transition-all"
        >
          Next
        </button>
      </div>
    </div>
  );
}
