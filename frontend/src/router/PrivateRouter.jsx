import React, { useEffect } from 'react';
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useRecoilValue } from "recoil";
import { BROWSER_PATH } from "../constants/path";
import { loginState } from "../recoils/auth";

const PrivateRouter = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const isLogin = useRecoilValue(loginState);

    useEffect(() => {
        if (!isLogin) {
            sessionStorage.setItem('rtnUri', location.pathname);
            navigate(BROWSER_PATH.AUTH.LOGIN, { replace: true });
        }
    }, [isLogin]);

    return (
        <Outlet />
    );
};

export default PrivateRouter;