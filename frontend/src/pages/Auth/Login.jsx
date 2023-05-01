import React from 'react';
import authApi from "../../api/auth";
import style from '../../css/Auth/Login.module.css';

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
        <div className={style.container}>
            <div className={style.row}>
                <div className={style.login}>
                    <div className={style.title}>로그인 및 회원가입</div>
                    <div className={style.explain}>
                        로그인을 통해 오점메의 다양한 혜택을 누리세요.
                    </div>
                    <button className={[style.btn, style.naver_login].join(' ')} onClick={handlerClickNaverLogin}>
                        <img src={`${process.env.PUBLIC_URL}/assets/image/naver_login_icon.jpg`} alt="" />
                        네이버 로그인
                    </button>
                    <button className={[style.btn, style.kakao_login].join(' ')} onClick={handlerClickKakaoLogin}>
                        <img src={`${process.env.PUBLIC_URL}/assets/image/kakao_login_icon.jpg`} alt="" />
                        카카오 로그인
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Login;