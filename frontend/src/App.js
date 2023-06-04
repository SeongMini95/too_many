import { BrowserRouter, Route, Routes } from "react-router-dom";
import { BROWSER_PATH } from "./constants/path";
import Login from "./pages/Auth/Login";
import KakaoLogin from "./pages/Auth/KakaoLogin";
import NaverLogin from "./pages/Auth/NaverLogin";
import Main from "./pages/Main/Main";
import PrivateRouter from "./router/PrivateRouter";
import SearchPlaceList from "./pages/Store/SearchPlaceList";
import GetStore from "./pages/Store/GetStore";
import MyInfo from "./pages/User/MyInfo";
import WriteEatTogetherPost from "./pages/EatTogether/WriteEatTogetherPost";
import GetEatTogetherPostList from "./pages/EatTogether/GetEatTogetherPostList";
import GetEatTogetherPost from "./pages/EatTogether/GetEatTogetherPost";
import ModifyEatTogetherPost from "./pages/EatTogether/ModifyEatTogetherPost";
import DefaultLayout from "./pages/Layout/Default/DefaultLayout";
import SimpleLayout from "./pages/Layout/Simple/SimpleLayout";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<SimpleLayout />}>
                    <Route path={BROWSER_PATH.AUTH.LOGIN} element={<Login />} />
                    <Route path={BROWSER_PATH.AUTH.KAKAO_LOGIN} element={<KakaoLogin />} />
                    <Route path={BROWSER_PATH.AUTH.NAVER_LOGIN} element={<NaverLogin />} />
                </Route>
                <Route element={<DefaultLayout />}>
                    <Route path={BROWSER_PATH.BASE} element={<Main />} />

                    <Route element={<PrivateRouter />}>
                        {/* user */}
                        <Route path={BROWSER_PATH.USER.GET_MY_INFO} element={<MyInfo />} />

                        {/* store */}
                        <Route path={BROWSER_PATH.STORE.SEARCH_PLACE_LIST} element={<SearchPlaceList />} />
                        <Route path={BROWSER_PATH.STORE.GET_STORE_REVIEWS} element={<GetStore />} />

                        {/* eat together */}
                        <Route path={BROWSER_PATH.EAT_TOGETHER.WRITE_POST} element={<WriteEatTogetherPost />} />
                        <Route path={BROWSER_PATH.EAT_TOGETHER.POST_LIST} element={<GetEatTogetherPostList />} />
                        <Route path={BROWSER_PATH.EAT_TOGETHER.GET_POST} element={<GetEatTogetherPost />} />
                        <Route path={BROWSER_PATH.EAT_TOGETHER.MODIFY_POST} element={<ModifyEatTogetherPost />} />
                    </Route>
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
