import { createBrowserRouter } from "react-router";
import { RootLayout } from "./components/RootLayout";
import { Splash } from "./components/Splash";
import { Onboarding } from "./components/Onboarding";
import { SignIn } from "./components/SignIn";
import { SignUp } from "./components/SignUp";
import { Home } from "./components/Home";
import { Statistics } from "./components/Statistics";
import { MyCards } from "./components/MyCards";
import { Settings } from "./components/Settings";
import { Profile } from "./components/Profile";
import { EditProfile } from "./components/EditProfile";
import { AddNewCard } from "./components/AddNewCard";
import { AllCards } from "./components/AllCards";
import { TransactionHistory } from "./components/TransactionHistory";
import { SearchScreen } from "./components/SearchScreen";
import { SendMoney } from "./components/SendMoney";
import { RequestMoney } from "./components/RequestMoney";
import { Language } from "./components/Language";
import { ChangePassword } from "./components/ChangePassword";
import { TermsCondition } from "./components/TermsCondition";
import { TransactionDetail } from "./components/TransactionDetail";
import { Topup } from "./components/Topup";
import { Notifications } from "./components/Notifications";
import { ContactUs } from "./components/ContactUs";
import { TransactionSuccess } from "./components/TransactionSuccess";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    children: [
      { index: true, element: <Splash /> },
      { path: "onboarding", element: <Onboarding /> },
      { path: "signin", element: <SignIn /> },
      { path: "signup", element: <SignUp /> },
      { path: "home", element: <Home /> },
      { path: "statistics", element: <Statistics /> },
      { path: "my-cards", element: <MyCards /> },
      { path: "settings", element: <Settings /> },
      { path: "profile", element: <Profile /> },
      { path: "edit-profile", element: <EditProfile /> },
      { path: "add-card", element: <AddNewCard /> },
      { path: "all-cards", element: <AllCards /> },
      { path: "transaction-history", element: <TransactionHistory /> },
      { path: "search", element: <SearchScreen /> },
      { path: "send-money", element: <SendMoney /> },
      { path: "request-money", element: <RequestMoney /> },
      { path: "language", element: <Language /> },
      { path: "change-password", element: <ChangePassword /> },
      { path: "terms-condition", element: <TermsCondition /> },
      { path: "transaction-detail", element: <TransactionDetail /> },
      { path: "topup", element: <Topup /> },
      { path: "notifications", element: <Notifications /> },
      { path: "contact-us", element: <ContactUs /> },
      { path: "transaction-success", element: <TransactionSuccess /> },
    ],
  },
]);
