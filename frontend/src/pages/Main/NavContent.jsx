import React, { useEffect, useState } from 'react';
import style from '../../css/Main/NavContent.module.css';
import { Link } from "react-router-dom";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { loginState } from "../../recoils/auth";
import { userInfoState } from "../../recoils/user";
import SelectRegion from "./SelectRegion";
import { positionState } from "../../recoils/position";
import regionApi from "../../api/region";
import { urlUtils } from "../../utils/urlUtils";
import { BROWSER_PATH } from "../../constants/path";

const NavContent = ({ storeRanking }) => {
    const isLogin = useRecoilValue(loginState);
    const { nickname } = useRecoilValue(userInfoState);
    const { address } = useRecoilValue(positionState);
    const setPosition = useSetRecoilState(positionState);

    const [otherRegion, setOtherRegion] = useState(false);
    const [visualStore, setVisualStore] = useState({
        imgUrl: '',
        storeId: '',
        storeName: ''
    });

    const handlerCurrentRegion = () => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(async position => {
                const { longitude, latitude } = position.coords;
                const { codes, address } = await regionApi.getRegionCodeOfCoord(longitude, latitude);
                setPosition({
                    codes: {
                        region1: codes[0] ?? '',
                        region2: codes[1] ?? '',
                        region3: codes[2] ?? '',
                    },
                    lastCode: codes[2] ?? codes[1] ?? codes[0],
                    address,
                    x: longitude,
                    y: latitude
                });
            });
        }
    }

    useEffect(() => {
        let index = 1;
        let maxIndex = storeRanking ? storeRanking.length - 1 : 1;

        setVisualStore(!!storeRanking.length ? {
            ...visualStore,
            imgUrl: storeRanking[0].image,
            storeId: storeRanking[0].storeId,
            storeName: storeRanking[0].storeName
        } : {
            ...visualStore,
            imgUrl: '',
            storeId: '',
            storeName: ''
        });

        let slideStoreRankingImg = null;

        if (!!storeRanking.length) {
            slideStoreRankingImg = setInterval(() => {
                setVisualStore(storeRanking[index] ? {
                    ...visualStore,
                    imgUrl: storeRanking[index].image,
                    storeId: storeRanking[index].storeId,
                    storeName: storeRanking[index].storeName
                } : {
                    ...visualStore,
                    imgUrl: '',
                    storeId: '',
                    storeName: ''
                });

                if (index === maxIndex) {
                    index = 0;
                }

                ++index;
            }, 5000);
        }

        return () => {
            clearInterval(slideStoreRankingImg);
        };
    }, [storeRanking]);

    return (
        <>
            <nav className={style.empty_nav} />
            <div className={style.nav_content}>
                <div className={style.nav_visual}>
                    {visualStore.imgUrl && (
                        <img className={style.visual_img} src={visualStore.imgUrl} alt="" />
                    )}
                    <div className={style.nav_visual_wrap}>
                        <div className={style.info_box}>
                            {!isLogin ? (
                                <p className={style.info_box_first}>당신을 위한</p>
                            ) : (
                                <p className={style.info_box_first}>
                                    <span className={visualStore.imgUrl ? style.info_box_username : style.info_box_username_white}>{nickname}</span>님을 위한
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
                    <Link className={style.link_wrap} to={urlUtils.setPath(BROWSER_PATH.STORE.GET_STORE_REVIEWS, { storeId: visualStore.storeId })}>
                        <span className={style.link_content}>
                            <img className={style.link_icon} src={`${process.env.PUBLIC_URL}/assets/image/top_store_link.png`} alt="" />
                            {visualStore.storeName}
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