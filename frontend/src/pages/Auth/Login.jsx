import React from 'react';
import authApi from "../../api/auth";

const Login = () => {
    const handlerClickNaverLogin = async () => {
        const uri = await authApi.getNaverLoginUri();
        window.location.assign(uri);
    };

    const handlerClickKakaoLogin = async () => {
        const uri = await authApi.getKakaoLoginUri()
        window.location.assign(uri);
    };

    return (
        <div>
            <button onClick={handlerClickNaverLogin}>네이버 로그인</button>
            <button onClick={handlerClickKakaoLogin}>카카오 로그인</button>
        </div>
    );
};

export default Login;