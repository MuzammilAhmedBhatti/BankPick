import { useState } from "react";
import { useNavigate } from "react-router";
import { ChevronLeft, Search, Check } from "lucide-react";

export function Language() {
  const navigate = useNavigate();
  const [selectedLanguage, setSelectedLanguage] = useState("English");
  const [searchQuery, setSearchQuery] = useState("");

  const languages = [
    { name: "English", flag: "🇺🇸" },
    { name: "Australia", flag: "🇦🇺" },
    { name: "Franch", flag: "🇫🇷" },
    { name: "Spanish", flag: "🇪🇸" },
    { name: "America", flag: "🇦🇲" },
    { name: "Vietnam", flag: "🇻🇳" },
  ];

  const filteredLanguages = languages.filter((lang) =>
    lang.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

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
          <h1 className="text-xl font-semibold">Language</h1>
          <div className="w-10"></div>
        </div>

        <div className="relative mb-6">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="Search Language"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-12 pr-4 py-4 bg-gray-50 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
          />
        </div>

        <div className="space-y-2">
          {filteredLanguages.map((language) => (
            <button
              key={language.name}
              onClick={() => setSelectedLanguage(language.name)}
              className="w-full flex items-center justify-between p-4 hover:bg-gray-50 active:scale-[0.99] transition-all rounded-xl"
            >
              <div className="flex items-center gap-3">
                <span className="text-3xl">{language.flag}</span>
                <span className="text-base">{language.name}</span>
              </div>
              {selectedLanguage === language.name && (
                <div className="w-6 h-6 bg-blue-600 rounded-full flex items-center justify-center">
                  <Check className="w-4 h-4 text-white" />
                </div>
              )}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
