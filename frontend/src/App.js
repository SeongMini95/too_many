import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./pages/Layout/Header";
import { BROWSER_PATH } from "./constants/path";
import Login from "./pages/Auth/Login";
import KakaoLogin from "./pages/Auth/KakaoLogin";
import NaverLogin from "./pages/Auth/NaverLogin";
import { useRecoilValue } from "recoil";
import { loginState } from "./recoils/auth";
import SelectRegion from "./pages/Layout/SelectRegion";
import Main from "./pages/Layout/Main";
import PrivateRouter from "./router/PrivateRouter";
import SearchPlaceList from "./pages/Store/SearchPlaceList";
import StoreReviews from "./pages/Store/StoreReviews";
import MyInfo from "./pages/User/MyInfo";
import WriteEatTogetherPost from "./pages/EatTogether/WriteEatTogetherPost";
import GetEatTogetherPostList from "./pages/EatTogether/GetEatTogetherPostList";
import GetEatTogetherPost from "./pages/EatTogether/GetEatTogetherPost";

function App() {
    const isLogin = useRecoilValue(loginState);

    return (
        <BrowserRouter>
            <Header />
            <Routes>
                <Route path={BROWSER_PATH.BASE} element={!isLogin ? <Main /> : <SelectRegion />} />

                <Route path={BROWSER_PATH.AUTH.LOGIN} element={<Login />} />
                <Route path={BROWSER_PATH.AUTH.KAKAO_LOGIN} element={<KakaoLogin />} />
                <Route path={BROWSER_PATH.AUTH.NAVER_LOGIN} element={<NaverLogin />} />

                <Route element={<PrivateRouter />}>
                    {/* user */}
                    <Route path={BROWSER_PATH.USER.GET_MY_INFO} element={<MyInfo />} />

                    {/* store */}
                    <Route path={BROWSER_PATH.STORE.SEARCH_PLACE_LIST} element={<SearchPlaceList />} />
                    <Route path={BROWSER_PATH.STORE.GET_STORE_REVIEWS} element={<StoreReviews />} />

                    {/* eat together */}
                    <Route path={BROWSER_PATH.EAT_TOGETHER.WRITE} element={<WriteEatTogetherPost />} />
                    <Route path={BROWSER_PATH.EAT_TOGETHER.LIST} element={<GetEatTogetherPostList />} />
                    <Route path={BROWSER_PATH.EAT_TOGETHER.GET} element={<GetEatTogetherPost />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
