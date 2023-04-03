import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from "react-router-dom";
import { useResetRecoilState, useSetRecoilState } from "recoil";
import authApi from "../../api/auth";
import { BROWSER_PATH } from "../../constants/path";
import { accessTokenState, loginState, refreshTokenState } from "../../recoils/auth";
import { userInfoState } from "../../recoils/user";

const KakaoLogin = () => {
    const navigate = useNavigate();

    const [searchParams] = useSearchParams();
    const code = searchParams.get('code');

    const setAccessToken = useSetRecoilState(accessTokenState);
    const setRefreshToken = useSetRecoilState(refreshTokenState);

    const setIsLogin = useSetRecoilState(loginState);
    const setUserInfo = useSetRecoilState(userInfoState);
    const resetUserInfo = useResetRecoilState(userInfoState);

    useEffect(() => {
        const login = async () => {
            try {
                if (code) {
                    const { accessToken, refreshToken } = await authApi.loginKakao(code);
                    setAccessToken(accessToken);
                    setRefreshToken(refreshToken);

                    const { result, nickname, profile } = await authApi.check();
                    if (result) {
                        setIsLogin(true);
                        setUserInfo({
                            nickname: nickname,
                            profile: profile
                        });

                        const rtnUri = sessionStorage.getItem('rtnUri') ?? BROWSER_PATH.BASE;
                        sessionStorage.removeItem('rtnUri');

                        navigate(rtnUri, { replace: true });
                    } else {
                        handlerLoginError();
                    }
                }
            } catch (e) {
                handlerLoginError();
            }
        }

        login();
    }, []);

    const handlerLoginError = () => {
        setIsLogin(false);
        setAccessToken('');
        setRefreshToken('');
        resetUserInfo();

        navigate(BROWSER_PATH.AUTH.LOGIN, { replace: true });
    }

    return (
        <></>
    );
};

export default KakaoLogin;