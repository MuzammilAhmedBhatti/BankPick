import { useNavigate, useLocation } from "react-router";
import { ChevronLeft, Download, Share2, Check } from "lucide-react";

export function TransactionDetail() {
  const navigate = useNavigate();
  const location = useLocation();
  const transaction = location.state?.transaction || {
    name: "Apple Store",
    category: "Entertainment",
    amount: -5.99,
    icon: "🍎",
    date: "May 2, 2026",
    time: "2:30 PM",
    transactionId: "TRX123456789",
    status: "Completed",
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Transaction Receipt',
          text: `${transaction.name} - $${Math.abs(transaction.amount)}`,
        });
      } catch (err) {
        console.log('Share cancelled');
      }
    }
  };

  const handleDownload = () => {
    alert('Receipt downloaded to your device');
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
          <h1 className="text-xl font-semibold">Transaction Details</h1>
          <div className="w-10"></div>
        </div>

        <div className="bg-white rounded-3xl border-2 border-gray-100 p-8 mb-6">
          <div className="flex flex-col items-center mb-8">
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mb-4">
              <Check className="w-10 h-10 text-green-600" />
            </div>
            <p className="text-sm text-gray-500 mb-2">{transaction.status}</p>
            <p className={`text-4xl font-bold ${transaction.amount > 0 ? 'text-green-600' : 'text-gray-900'}`}>
              {transaction.amount > 0 ? '+' : '-'}${Math.abs(transaction.amount)}
            </p>
          </div>

          <div className="space-y-4 border-t border-gray-100 pt-6">
            <div className="flex justify-between">
              <span className="text-gray-500">Merchant</span>
              <span className="font-semibold">{transaction.name}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Category</span>
              <span className="font-semibold">{transaction.category}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Date</span>
              <span className="font-semibold">{transaction.date}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Time</span>
              <span className="font-semibold">{transaction.time}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Transaction ID</span>
              <span className="font-semibold text-sm">{transaction.transactionId}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Payment Method</span>
              <span className="font-semibold">Mastercard •••• 7852</span>
            </div>
          </div>
        </div>

        <div className="flex gap-3">
          <button
            onClick={handleDownload}
            className="flex-1 bg-gray-100 text-gray-900 py-4 rounded-2xl flex items-center justify-center gap-2 hover:bg-gray-200 active:scale-95 transition-all"
          >
            <Download className="w-5 h-5" />
            Download
          </button>
          <button
            onClick={handleShare}
            className="flex-1 bg-blue-600 text-white py-4 rounded-2xl flex items-center justify-center gap-2 hover:bg-blue-700 active:scale-95 transition-all"
          >
            <Share2 className="w-5 h-5" />
            Share
          </button>
        </div>
      </div>
    </div>
  );
}
