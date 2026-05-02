import { useNavigate } from "react-router";
import { ChevronLeft, Plus } from "lucide-react";

export function AllCards() {
  const navigate = useNavigate();

  const cards = [
    {
      number: "4562 1122 4595 7852",
      holder: "AR Jonson",
      expiry: "24/2000",
      cvv: "6986",
      type: "Mastercard",
      gradient: "from-slate-800 via-slate-700 to-slate-800",
    },
    {
      number: "4562 1122 4595 7852",
      holder: "Smith Jonson",
      expiry: "24/2000",
      cvv: "6986",
      type: "VISA",
      gradient: "from-blue-900 via-blue-800 to-blue-900",
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
          <h1 className="text-xl font-semibold">All Cards</h1>
          <div className="w-10"></div>
        </div>

        <div className="space-y-6 mb-8">
          {cards.map((card, index) => (
            <div
              key={index}
              onClick={() => navigate("/my-cards")}
              className="cursor-pointer hover:scale-[1.02] active:scale-[0.98] transition-transform"
            >
              <div className={`bg-gradient-to-br ${card.gradient} rounded-3xl p-6 text-white relative overflow-hidden`}>
                <div className="absolute top-4 right-4 w-12 h-8 opacity-30">
                  <div className="flex gap-1">
                    <div className="w-6 h-6 rounded-full bg-white"></div>
                    <div className="w-6 h-6 rounded-full bg-white"></div>
                  </div>
                </div>

                <div className="w-10 h-10 rounded-lg border-2 border-white/30 flex items-center justify-center mb-6">
                  <div className="w-6 h-4 border-2 border-white rounded"></div>
                </div>

                <p className="text-2xl tracking-wider mb-4 font-mono">{card.number}</p>

                <div className="flex justify-between items-end">
                  <div>
                    <p className="text-xs opacity-70 mb-1">{card.holder}</p>
                    <div className="flex gap-4 text-xs">
                      <div>
                        <p className="opacity-70">Expiry Date</p>
                        <p>{card.expiry}</p>
                      </div>
                    </div>
                  </div>
                  <div className="flex flex-col items-end">
                    {card.type === "Mastercard" ? (
                      <>
                        <div className="flex gap-1 mb-1">
                          <div className="w-6 h-6 rounded-full bg-red-500"></div>
                          <div className="w-6 h-6 rounded-full bg-orange-500 -ml-2"></div>
                        </div>
                        <p className="text-xs">Mastercard</p>
                      </>
                    ) : (
                      <p className="text-xl font-bold">VISA</p>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        <button
          onClick={() => navigate("/add-card")}
          className="w-full bg-blue-600 text-white py-4 rounded-2xl flex items-center justify-center gap-2 hover:bg-blue-700 active:scale-95 transition-all"
        >
          <Plus className="w-5 h-5" />
          Add Card
        </button>
      </div>
    </div>
  );
}
