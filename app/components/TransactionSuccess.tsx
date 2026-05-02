import { useNavigate, useLocation } from "react-router";
import { Check, Download, Share2 } from "lucide-react";

export function TransactionSuccess() {
  const navigate = useNavigate();
  const location = useLocation();
  const data = location.state || {
    type: "Send Money",
    recipient: "Yamilet",
    amount: "36.00",
    date: new Date().toLocaleDateString(),
    time: new Date().toLocaleTimeString(),
    transactionId: `TRX${Date.now()}`,
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Transaction Receipt',
          text: `${data.type} - $${data.amount} to ${data.recipient}`,
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
    <div className="h-screen bg-white flex flex-col items-center justify-center p-6">
      <div className="w-full max-w-sm">
        <div className="bg-white rounded-3xl border-2 border-gray-100 p-8 mb-6">
          <div className="flex flex-col items-center mb-8">
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mb-4 animate-in zoom-in duration-500">
              <Check className="w-10 h-10 text-green-600" />
            </div>
            <h2 className="text-2xl font-bold mb-2">Transaction Successful!</h2>
            <p className="text-gray-500 text-center">
              Your {data.type.toLowerCase()} has been completed
            </p>
          </div>

          <div className="bg-blue-50 rounded-2xl p-6 mb-6">
            <div className="text-center mb-4">
              <p className="text-sm text-gray-600 mb-2">Amount Sent</p>
              <p className="text-4xl font-bold text-blue-600">${data.amount}</p>
            </div>
            {data.recipient && (
              <div className="text-center border-t border-blue-200 pt-4">
                <p className="text-sm text-gray-600 mb-1">To</p>
                <p className="text-lg font-semibold">{data.recipient}</p>
              </div>
            )}
          </div>

          <div className="space-y-3 border-t border-gray-100 pt-6">
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Transaction ID</span>
              <span className="font-medium">{data.transactionId}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Date</span>
              <span className="font-medium">{data.date}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Time</span>
              <span className="font-medium">{data.time}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Payment Method</span>
              <span className="font-medium">Mastercard •••• 7852</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Status</span>
              <span className="font-medium text-green-600">Completed</span>
            </div>
          </div>
        </div>

        <div className="flex gap-3 mb-4">
          <button
            onClick={handleDownload}
            className="flex-1 bg-gray-100 text-gray-900 py-4 rounded-2xl flex items-center justify-center gap-2 hover:bg-gray-200 active:scale-95 transition-all"
          >
            <Download className="w-5 h-5" />
            Download
          </button>
          <button
            onClick={handleShare}
            className="flex-1 bg-gray-100 text-gray-900 py-4 rounded-2xl flex items-center justify-center gap-2 hover:bg-gray-200 active:scale-95 transition-all"
          >
            <Share2 className="w-5 h-5" />
            Share
          </button>
        </div>

        <button
          onClick={() => navigate("/home")}
          className="w-full bg-blue-600 text-white py-4 rounded-2xl hover:bg-blue-700 active:scale-95 transition-all"
        >
          Back to Home
        </button>
      </div>
    </div>
  );
}
