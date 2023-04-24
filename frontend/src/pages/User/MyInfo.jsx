import React, { useEffect, useState } from 'react';
import userApi from "../../api/user";
import { useRecoilState } from "recoil";
import { userInfoState } from "../../recoils/user";
import imageApi from "../../api/image";

const MyInfo = () => {
    const [userInfo, setUserInfo] = useRecoilState(userInfoState);
    const [myInfo, setMyInfo] = useState({
        provider: '',
        nickname: '',
        email: '',
        profile: ''
    });
    const [inputs, setInputs] = useState({
        nickname: '',
        profile: ''
    });

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

    const handlerChangeInputs = (e) => {
        const { name, value } = e.target;
        setInputs({
            ...inputs,
            [name]: value
        });
    }

    const handlerClickModifyNickname = async () => {
        try {
            const { nickname } = inputs;
            await userApi.modifyNickname(nickname);
            setUserInfo({
                ...userInfo,
                nickname
            });
            setMyInfo({
                ...myInfo,
                nickname
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerChangeTempProfile = async (e) => {
        try {
            const tempProfile = e.target.files[0];
            if (tempProfile) {
                const tempUrl = await imageApi.upload(tempProfile);
                setInputs({
                    ...inputs,
                    profile: tempUrl
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickModifyProfile = async () => {
        try {
            const url = await userApi.modifyProfile(inputs.profile);
            setUserInfo({
                ...userInfo,
                profile: url
            });
            setMyInfo({
                ...myInfo,
                profile: url
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <p>{myInfo.provider}</p>
            <p>{myInfo.nickname}</p>
            <p>{myInfo.email}</p>
            <p>{myInfo.profile}</p>
            <div>
                <input type="text" name="nickname" onChange={handlerChangeInputs} value={inputs.nickname} />
                <button onClick={handlerClickModifyNickname}>닉네임 수정</button>
            </div>
            <div>
                <p>{inputs.profile}</p>
                <input type="file" accept="image/*" onChange={handlerChangeTempProfile} />
                <button onClick={() => setInputs({ ...inputs, profile: '' })}>기본 프로필</button>
                <button onClick={handlerClickModifyProfile}>프로필 변경</button>
            </div>
        </div>
    );
};

export default MyInfo;