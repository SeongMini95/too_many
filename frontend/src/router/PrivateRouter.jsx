import React, { useEffect } from 'react';
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useRecoilValue } from "recoil";
import { BROWSER_PATH } from "../constants/path";
import { accessTokenState, loginState } from "../recoils/auth";

const PrivateRouter = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const isLogin = useRecoilValue(loginState);
    const accessToken = useRecoilValue(accessTokenState)

    useEffect(() => {
        if (!isLogin && !accessToken) {
            const rtnUri = location.pathname;
            const loginPaths = [BROWSER_PATH.AUTH.LOGIN, BROWSER_PATH.AUTH.NAVER_LOGIN, BROWSER_PATH.AUTH.KAKAO_LOGIN];
            if (!loginPaths.includes(rtnUri)) {
                sessionStorage.setItem('rtnUri', rtnUri);
            }
            
            navigate(BROWSER_PATH.AUTH.LOGIN, { replace: true });
        }
    }, [isLogin, accessToken]);

    return (
        <Outlet />
    );
};

export default PrivateRouter;