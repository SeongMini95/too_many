import React, { useState } from 'react';
import style from '../../../css/Layout/Default/NavContent.module.css';
import { Link } from "react-router-dom";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { loginState } from "../../../recoils/auth";
import { userInfoState } from "../../../recoils/user";
import SelectRegion from "./SelectRegion";
import { positionState } from "../../../recoils/position";
import regionApi from "../../../api/region";

const NavContent = () => {
    const isLogin = useRecoilValue(loginState);
    const { nickname } = useRecoilValue(userInfoState);
    const { address } = useRecoilValue(positionState);
    const setPosition = useSetRecoilState(positionState);

    const [otherRegion, setOtherRegion] = useState(false);

    const handlerCurrentRegion = () => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(async position => {
                const { longitude, latitude } = position.coords;
                const { codes, address } = await regionApi.getRegionCodeOfCoord(longitude, latitude);
                setPosition({
                    codes,
                    address,
                    x: longitude,
                    y: latitude
                });
            });
        }
    }

    return (
        <>
            <nav className={style.empty_nav} />
            <div className={style.nav_content}>
                <div className={style.nav_visual}>
                    <img className={style.visual_img} src="https://d12zq4w4guyljn.cloudfront.net/pre_20220913200230_photo1_5f3a51b4b5f9.jpg" alt="" />
                    <div className={style.nav_visual_wrap}>
                        <div className={style.info_box}>
                            {!isLogin ? (
                                <p className={style.info_box_first}>당신을 위한</p>
                            ) : (
                                <p className={style.info_box_first}>
                                    <span className={style.info_box_username}>{nickname}</span>님을 위한
                                </p>
                            )}
                            <h1 className={style.info_box_second}>
                                <span className={style.info_box_location}>{address ? address : '서울'}</span> 추천 맛집
                            </h1>
                        </div>
                        <div className={style.nav_button_wrap}>
                            <button className={style.button_other_location} onClick={() => setOtherRegion(true)}>다른 지역 선택</button>
                            <button className={style.button_current_location} onClick={handlerCurrentRegion}>
                                <img className={style.button_refresh_img}
                                     src={`${process.env.PUBLIC_URL}/assets/image/my_location.png`} alt="" />
                                현 위치로 설정
                            </button>
                        </div>
                    </div>
                    <Link className={style.link_wrap} to={'asd'}>
                        <span className={style.link_content}>
                            <img className={style.link_icon} src={`${process.env.PUBLIC_URL}/assets/image/top_store_link.png`} alt="" />
                            매장명
                        </span>
                    </Link>
                </div>
            </div>
            {otherRegion && (
                <SelectRegion handlerClickClose={() => setOtherRegion(false)} />
            )}
        </>
    );
};

export default NavContent;