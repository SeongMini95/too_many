import React from 'react';
import NavContent from "./NavContent";
import style from '../../css/Main/Main.module.css';
import storeApi from "../../api/store";
import { useQuery } from "@tanstack/react-query";
import { API_PATH, BROWSER_PATH } from "../../constants/path";
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import { Link } from "react-router-dom";
import { urlUtils } from "../../utils/urlUtils";
import eatTogetherApi from "../../api/eatTogether";

const Main = () => {
    const { codes } = useRecoilValue(positionState);

    const { data: storeRanking, isSuccess: isStoreRankingSuccess } = useQuery(
        [API_PATH.STORE.GET_REAL_TIME_STORE_RANKING, { regionCode: codes[codes.length - 1] }],
        async () => {
            const { stores } = await storeApi.getRealTimeStoreRanking(codes[codes.length - 1]);
            return stores;
        }, {
            enabled: !!codes.length,
            refetchOnMount: false,
        }
    );

    const { data: recentPostList, isSuccess: isRecentPostListSuccess } = useQuery(
        [API_PATH.EAT_TOGETHER.GET_RECENT_POST_LIST, { regionCode: codes[codes.length - 1] }],
        async () => {
            const { posts } = await eatTogetherApi.getRecentEatTogetherPostList(codes[codes.length - 1]);
            return posts;
        }, {
            enabled: !!codes.length,
            refetchOnMount: false
        }
    );

    return (
        <>
            <NavContent storeRanking={storeRanking} />
            <main className={style.main}>
                <div className={style.top_box}>
                    <section className={style.top_box_section}>
                        <h1 className={style.section_title}>실시간 추천 맛집</h1>
                        <ul className={style.section_ul}>
                            {isStoreRankingSuccess && [...Array(10).keys()].map(v => (
                                <li key={'ranking' + v} className={style.section_li}>
                                    {!storeRanking[v] ? (
                                        <div className={style.ranking}>{v + 1}.</div>
                                    ) : (
                                        <Link to={urlUtils.setPath(BROWSER_PATH.STORE.GET_STORE_REVIEWS, { storeId: storeRanking[v].storeId })} className={style.section_li_a}>
                                            <div className={style.ranking}>{v + 1}.</div>
                                            <div className={style.region}>[{storeRanking[v].regionName}]</div>
                                            <div className={style.section_content}>{storeRanking[v].storeName}</div>
                                        </Link>
                                    )}
                                </li>
                            ))}
                        </ul>
                    </section>
                    <section className={style.top_box_section}>
                        <h1 className={style.section_title}>혼밥 탈출</h1>
                        <ul className={style.section_ul}>
                            {isRecentPostListSuccess && (
                                recentPostList.map(v => (
                                    <li key={'post' + v.postId} className={style.section_li}>
                                        <Link to={urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.GET_POST, { postId: v.postId })} className={style.section_li_a}>
                                            <div className={style.region}>[{v.regionName}]</div>
                                            <div className={style.section_content}>{v.subject}</div>
                                            <div className={style.section_createDatetime}>{v.createDatetime}</div>
                                        </Link>
                                    </li>
                                ))
                            )}
                        </ul>
                    </section>
                </div>
            </main>
        </>
    );
};

export default Main;