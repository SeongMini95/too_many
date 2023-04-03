import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./pages/Layout/Header";
import { BROWSER_PATH } from "./constants/path";
import Main from "./pages/Layout/Main";
import Login from "./pages/Auth/Login";
import KakaoLogin from "./pages/Auth/KakaoLogin";
import NaverLogin from "./pages/Auth/NaverLogin";

function App() {
    return (
        <BrowserRouter>
            <Header />
            <Routes>
                <Route path={BROWSER_PATH.BASE} element={< Main />} />

                <Route path={BROWSER_PATH.AUTH.LOGIN} element={<Login />} />
                <Route path={BROWSER_PATH.AUTH.KAKAO_LOGIN} element={<KakaoLogin />} />
                <Route path={BROWSER_PATH.AUTH.NAVER_LOGIN} element={<NaverLogin />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
