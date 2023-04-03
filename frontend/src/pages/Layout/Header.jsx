import React, { useEffect } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import { useRecoilState, useResetRecoilState, useSetRecoilState } from "recoil";
import { accessTokenState, loginState, refreshTokenState } from "../../recoils/auth";
import { userInfoState } from "../../recoils/user";
import authApi from "../../api/auth";
import { BROWSER_PATH } from "../../constants/path";

const Header = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const [isLogin, setIsLogin] = useRecoilState(loginState);
    const [{ nickname, profile }, setUserInfo] = useRecoilState(userInfoState);
    const [accessToken, setAccessToken] = useRecoilState(accessTokenState);
    const setRefreshToken = useSetRecoilState(refreshTokenState);
    const resetUserInfo = useResetRecoilState(userInfoState);

    useEffect(() => {
        const check = async () => {
            try {
                if (!accessToken) {
                    return;
                }

                const { result, nickname, profile } = await authApi.check();
                if (result) {
                    setIsLogin(true);
                    setUserInfo({
                        nickname: nickname,
                        profile: profile
                    });
                } else {
                    handlerLogout();
                }
            } catch (e) {
                handlerLogout();
            }
        }

        check();
    }, []);

    const handlerLogout = () => {
        setIsLogin(false);
        setAccessToken('');
        setRefreshToken('');
        resetUserInfo();

        navigate(BROWSER_PATH.BASE);
    };

    const handlerMoveLoginPage = () => {
        const rtnUri = location.pathname;
        if (rtnUri !== BROWSER_PATH.AUTH.LOGIN) {
            sessionStorage.setItem('rtnUri', rtnUri);
        }

        navigate(BROWSER_PATH.AUTH.LOGIN);
    }

    return (
        <div>
            {!isLogin ? (
                <>
                    <button type="button" onClick={handlerMoveLoginPage}>로그인</button>
                </>
            ) : (
                <>
                    <p>{nickname}</p>
                    <p>{profile}</p>
                    <button type="button" onClick={handlerLogout}>로그아웃</button>
                </>
            )}
        </div>
    );
};

export default Header;