import { useNavigate } from "react-router";
import { ChevronLeft, Mail, Phone, MapPin, Globe } from "lucide-react";

export function ContactUs() {
  const navigate = useNavigate();

  const contactInfo = [
    {
      icon: Phone,
      title: "Customer Service",
      value: "+1 (800) 123-4567",
      subtext: "Available 24/7",
    },
    {
      icon: Mail,
      title: "Email Support",
      value: "support@bankpick.com",
      subtext: "Response within 24 hours",
    },
    {
      icon: MapPin,
      title: "Head Office",
      value: "123 Financial District, New York, NY 10004",
      subtext: "Monday - Friday, 9AM - 5PM EST",
    },
    {
      icon: Globe,
      title: "Website",
      value: "www.bankpick.com",
      subtext: "Online banking portal",
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
          <h1 className="text-xl font-semibold">Contact Us</h1>
          <div className="w-10"></div>
        </div>

        <div className="mb-6 text-center">
          <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Phone className="w-10 h-10 text-blue-600" />
          </div>
          <h2 className="text-2xl font-bold mb-2">We're Here to Help</h2>
          <p className="text-gray-500">
            Get in touch with our support team for any questions or assistance
          </p>
        </div>

        <div className="space-y-4 mb-6">
          {contactInfo.map((item, index) => {
            const Icon = item.icon;
            return (
              <div
                key={index}
                className="bg-gray-50 rounded-2xl p-4 hover:bg-gray-100 active:scale-[0.99] transition-all"
              >
                <div className="flex gap-4">
                  <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0">
                    <Icon className="w-6 h-6 text-blue-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-gray-500 mb-1">{item.title}</p>
                    <p className="font-semibold mb-1 break-words">{item.value}</p>
                    <p className="text-xs text-gray-400">{item.subtext}</p>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        <div className="bg-blue-50 rounded-2xl p-6 text-center">
          <h3 className="font-semibold mb-2">Need Immediate Assistance?</h3>
          <p className="text-sm text-gray-600 mb-4">
            For urgent matters, please call our 24/7 hotline
          </p>
          <a
            href="tel:+18001234567"
            className="inline-block bg-blue-600 text-white px-8 py-3 rounded-xl hover:bg-blue-700 active:scale-95 transition-all"
          >
            Call Now
          </a>
        </div>
      </div>
    </div>
  );
}
