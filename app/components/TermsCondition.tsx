import { useNavigate } from "react-router";
import { ChevronLeft } from "lucide-react";

export function TermsCondition() {
  const navigate = useNavigate();

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
          <h1 className="text-xl font-semibold">Terms & Condition</h1>
          <div className="w-10"></div>
        </div>

        <div className="overflow-y-auto pr-2" style={{ maxHeight: "calc(100vh - 140px)" }}>
          <div className="space-y-4 text-sm text-gray-700 leading-relaxed">
            <p>
              L15.1 Thank you for visiting our Application Doctor 24×7 and enrolling as a member.
            </p>

            <p>
              15.2 Your privacy is important to us. To better protect your privacy, we are providing this
              notice explaining our policy with regards to the information you share with us. This privacy
              policy relates to the information we collect, online from Application, received through the
              email, by fax or telephone, or in person or in any other way and retain and use for the
              purpose of providing you services. If you do not agree to the terms in this Policy, we kindly
              ask you not to use these portals and/or sign the contract document.
            </p>

            <p>
              15.3 In order to use the services of this Application, You are required to register
              yourself by verifying the authorised device. This Privacy Policy applies to your information
              that we collect and receive on and through Doctor 24×7; it does not apply to practices of
              businesses that we do not own or control or people we do not employ.
            </p>

            <p>
              15.4 By using this Application, you agree to the terms of this Privacy Policy.
            </p>

            <p className="font-semibold mt-6">
              Additional Terms and Conditions
            </p>

            <p>
              By accessing and using this application, you acknowledge that you have read, understood,
              and agree to be bound by these terms and conditions. We reserve the right to modify these
              terms at any time without prior notice.
            </p>

            <p>
              Your continued use of the application following any changes indicates your acceptance of
              the new terms. If you do not agree with any part of these terms, please discontinue use
              of the application immediately.
            </p>

            <p className="font-semibold mt-6">
              Data Collection and Usage
            </p>

            <p>
              We collect information to provide better services to our users. The information we collect
              includes personal identification information, usage data, and device information. This
              data is used to improve our services, customize user experience, and ensure security.
            </p>

            <p>
              We are committed to protecting your personal information and will not share it with third
              parties without your explicit consent, except as required by law.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
