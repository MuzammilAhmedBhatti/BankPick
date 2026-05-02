import { Outlet } from "react-router";

export function RootLayout() {
  return (
    <div className="min-h-screen bg-white">
      <div className="mx-auto max-w-md h-screen">
        <Outlet />
      </div>
    </div>
  );
}
