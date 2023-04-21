import React, { useEffect } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import { useRecoilState, useResetRecoilState, useSetRecoilState } from "recoil";
import { accessTokenState, loginState, refreshTokenState } from "../../recoils/auth";
import { userInfoState } from "../../recoils/user";
import authApi from "../../api/auth";
import { BROWSER_PATH } from "../../constants/path";
import { positionState } from "../../recoils/position";
import { positionProvider } from "../../utils/positionUtils";

const Header = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const [isLogin, setIsLogin] = useRecoilState(loginState);
    const [{ nickname, profile }, setUserInfo] = useRecoilState(userInfoState);
    const [accessToken, setAccessToken] = useRecoilState(accessTokenState);
    const setRefreshToken = useSetRecoilState(refreshTokenState);
    const resetUserInfo = useResetRecoilState(userInfoState);
    const setPosition = useSetRecoilState(positionState);

    useEffect(() => {
        const authCheck = async () => {
            try {
                if (!accessToken) {
                    return;
                }

                const { result, id, nickname, profile } = await authApi.check();
                if (result) {
                    setIsLogin(true);
                    setUserInfo({
                        id: id,
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

        authCheck();
    }, []);

    useEffect(() => {
        const position = positionProvider.getPosition();
        if (position) {
            setPosition(JSON.parse(position));
        } else {
            setPosition('');
        }
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
        const loginPaths = [BROWSER_PATH.AUTH.LOGIN, BROWSER_PATH.AUTH.NAVER_LOGIN, BROWSER_PATH.AUTH.KAKAO_LOGIN];
        if (!loginPaths.includes(rtnUri)) {
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