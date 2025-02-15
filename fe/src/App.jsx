import { createBrowserRouter, RouterProvider, Outlet } from "react-router-dom";
import { Provider } from "react-redux";

import Home from "./pages/Home";
import SignIn from "./pages/SignIn";
import SignUp from "./pages/SignUp";
import OAuth2Join from "./pages/OAuth2Join";
import OAuth2CallBack from "./pages/OAuth2CallBack";
import MyInfo from "./pages/MyInfo";

import store from "./redux/store";

const Layout = () => (
  <>
    <Outlet />
  </>
);

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      { path: "/", element: <Home /> },
      { path: "/signin", element: <SignIn /> },
      { path: "/signup", element: <SignUp /> },
      { path: "/oauth/callback", element: <OAuth2CallBack /> },
      { path: "/oauth/join", element: <OAuth2Join /> },
      { path: "/my-info", element: <MyInfo />}
    ]
  }
])

export default function App() {
  return (
    <Provider store={store}>
      <RouterProvider router={router} />
    </Provider>
  )
}