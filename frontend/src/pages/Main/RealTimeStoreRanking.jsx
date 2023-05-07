import React, { useEffect } from 'react';
import style from "../../css/Main/Main.module.css";
import { Link } from "react-router-dom";
import { urlUtils } from "../../utils/urlUtils";
import { API_PATH, BROWSER_PATH } from "../../constants/path";
import { useQuery } from "@tanstack/react-query";
import storeApi from "../../api/store";
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";

const RealTimeStoreRanking = ({ getStoreRanking }) => {
    const { lastCode } = useRecoilValue(positionState);

    const { data: storeRanking, isSuccess: isStoreRankingSuccess } = useQuery(
        [API_PATH.STORE.GET_REAL_TIME_STORE_RANKING, { regionCode: lastCode }],
        async () => {
            const { stores } = await storeApi.getRealTimeStoreRanking(lastCode);
            getStoreRanking(stores);

            return stores;
        }, {
            enabled: !!lastCode,
            refetchOnMount: false,
        }
    );

    useEffect(() => {
        if (storeRanking) {
            getStoreRanking(storeRanking);
        }
    }, [storeRanking]);

    return (
        <section className={style.top_box_section_first}>
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
    );
};

export default RealTimeStoreRanking;