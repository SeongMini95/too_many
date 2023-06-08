import React, { useEffect, useRef, useState } from 'react';
import userApi from "../../api/user";
import { useRecoilState } from "recoil";
import { userInfoState } from "../../recoils/user";
import style from '../../css/User/MyInfo.module.css';
import imageApi from "../../api/image";
import { useNavigate } from "react-router-dom";
import { BROWSER_PATH } from "../../constants/path";

const MyInfo = () => {
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useRecoilState(userInfoState);

    const [myInfo, setMyInfo] = useState({
        provider: '',
        nickname: '',
        email: '',
        profile: ''
    });

    const refProfile = useRef(null);

    useEffect(() => {
        const getMyInfo = async () => {
            try {
                const { provider, nickname, email, profile } = await userApi.getMyInfo();
                setMyInfo({
                    ...myInfo,
                    provider,
                    nickname,
                    email,
                    profile
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        getMyInfo();
    }, []);

    const handlerChangeNickname = (e) => {
        setMyInfo({
            ...myInfo,
            nickname: e.target.value
        });
    }

    const handlerClickDefaultProfile = () => {
        setMyInfo({
            ...myInfo,
            profile: ''
        });
    }

    const handlerChangeProfile = async (e) => {
        try {
            const profile = e.target.files[0];
            if (profile) {
                const url = await imageApi.upload(profile);
                setMyInfo({
                    ...myInfo,
                    profile: url
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickModifyMyInfo = async () => {
        try {
            const param = {
                nickname: myInfo.nickname,
                profile: myInfo.profile
            };

            const { nickname, profile } = await userApi.modifyMyInfo(param);
            setUserInfo({
                ...userInfo,
                nickname,
                profile
            });

            navigate(BROWSER_PATH.BASE);
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div className={style.my_info_wrap}>
            <h3 className={style.title}>프로필 수정</h3>
            <div>
                <table className={style.table_user}>
                    <tbody>
                    <tr>
                        <th>프로필 사진</th>
                        <td>
                            <input type="file" onChange={handlerChangeProfile} ref={refProfile} className="hide" accept="image/*" />
                            <img src={myInfo.profile !== '' ? myInfo.profile : `${process.env.PUBLIC_URL}/assets/image/default_profile.png`} className={style.profile} alt="" />
                            <div className={style.btn_profile_wrap}>
                                <button onClick={() => refProfile.current.click()} className={style.btn_profile}>프로필 변경</button>
                                <button onClick={handlerClickDefaultProfile} className={style.btn_profile}>기본 프로필</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <th>닉네임</th>
                        <td>
                            <input type="text" onChange={(e) => handlerChangeNickname(e)} value={myInfo.nickname} className={style.input} />
                        </td>
                    </tr>
                    <tr>
                        <th>이메일</th>
                        <td>{myInfo.email}</td>
                    </tr>
                    <tr>
                        <th>소셜 로그인</th>
                        <td>
                            <div className={myInfo.provider === 1 ? style.social_login_img : [style.social_login_img, style.kakao].join(' ')}>
                                {myInfo.provider === 1 ? '네이버' : '카카오'} 연동
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div className={style.btn_confirm_wrap}>
                    <button onClick={handlerClickModifyMyInfo} className={style.btn_confirm}>수정</button>
                    <button onClick={() => navigate(BROWSER_PATH.BASE)} className={style.btn_confirm}>취소</button>
                </div>
            </div>
        </div>
    );
};

export default MyInfo;