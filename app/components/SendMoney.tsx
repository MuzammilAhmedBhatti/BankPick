import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, Plus } from "lucide-react";

export function SendMoney() {
  const navigate = useNavigate();
  const [amount, setAmount] = useState("36.00");
  const [selectedContact, setSelectedContact] = useState("Yamilet");

  const handleSendMoney = () => {
    navigate("/transaction-success", {
      state: {
        type: "Send Money",
        recipient: selectedContact,
        amount: amount,
        date: new Date().toLocaleDateString("en-US", {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        }),
        time: new Date().toLocaleTimeString("en-US", {
          hour: '2-digit',
          minute: '2-digit'
        }),
        transactionId: `TRX${Date.now()}`,
      },
    });
  };

  const contacts = [
    { name: "Yamilet", avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop" },
    { name: "Alexa", avatar: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop" },
    { name: "Yakub", avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop" },
    { name: "Krishna", avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop" },
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
          <h1 className="text-xl font-semibold">Send Money</h1>
          <div className="w-10"></div>
        </div>

        <div className="mb-6">
          <div className="bg-gradient-to-br from-slate-800 via-slate-700 to-slate-800 rounded-3xl p-6 text-white relative overflow-hidden">
            <div className="absolute top-4 right-4 w-12 h-8 opacity-30">
              <div className="flex gap-1">
                <div className="w-6 h-6 rounded-full bg-white"></div>
                <div className="w-6 h-6 rounded-full bg-white"></div>
              </div>
            </div>

            <div className="w-10 h-10 rounded-lg border-2 border-white/30 flex items-center justify-center mb-6">
              <div className="w-6 h-4 border-2 border-white rounded"></div>
            </div>

            <p className="text-2xl tracking-wider mb-4 font-mono">4562 1122 4595 7852</p>

            <div className="flex justify-between items-end">
              <div>
                <p className="text-xs opacity-70 mb-1">AR Jonson</p>
                <div className="flex gap-4 text-xs">
                  <div>
                    <p className="opacity-70">Expiry Date</p>
                    <p>24/2000</p>
                  </div>
                  <div>
                    <p className="opacity-70">CVV</p>
                    <p>6986</p>
                  </div>
                </div>
              </div>
              <div className="flex flex-col items-end">
                <div className="flex gap-1 mb-1">
                  <div className="w-6 h-6 rounded-full bg-red-500"></div>
                  <div className="w-6 h-6 rounded-full bg-orange-500 -ml-2"></div>
                </div>
                <p className="text-xs">Mastercard</p>
              </div>
            </div>
          </div>
        </div>

        <div className="mb-6">
          <p className="text-sm text-gray-600 mb-3">Send to</p>
          <div className="flex gap-3 overflow-x-auto pb-2">
            <button className="flex flex-col items-center gap-2 min-w-[60px]">
              <div className="w-14 h-14 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 hover:bg-blue-200 active:scale-95 transition-all">
                <Plus className="w-6 h-6" />
              </div>
              <span className="text-xs text-gray-600">Add</span>
            </button>
            {contacts.map((contact) => (
              <button
                key={contact.name}
                onClick={() => setSelectedContact(contact.name)}
                className={`flex flex-col items-center gap-2 min-w-[60px] ${
                  selectedContact === contact.name ? "opacity-100" : "opacity-70"
                }`}
              >
                <div className={`w-14 h-14 rounded-full overflow-hidden border-2 ${
                  selectedContact === contact.name ? "border-blue-600" : "border-transparent"
                } hover:scale-105 active:scale-95 transition-all`}>
                  <img
                    src={contact.avatar}
                    alt={contact.name}
                    className="w-full h-full object-cover"
                  />
                </div>
                <span className="text-xs text-gray-600">{contact.name}</span>
              </button>
            ))}
          </div>
        </div>

        <div className="bg-gray-50 rounded-2xl p-6 mb-6">
          <div className="flex items-baseline justify-between mb-2">
            <label className="text-sm text-gray-500">Enter Your Amount</label>
            <button className="text-sm text-red-500 hover:underline">Change Currency?</button>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl text-gray-400">USD</span>
            <input
              type="text"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="text-4xl font-semibold bg-transparent border-none outline-none w-full"
            />
          </div>
        </div>

        <button
          onClick={handleSendMoney}
          className="w-full bg-blue-600 text-white py-4 rounded-2xl hover:bg-blue-700 active:scale-95 transition-all"
        >
          Send Money
        </button>
      </div>
    </div>
  );
}
