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

const Main = () => {
    const { codes } = useRecoilValue(positionState);

    const { data, isSuccess } = useQuery(
        [API_PATH.STORE.GET_REAL_TIME_STORE_RANKING, { regionCode: codes[codes.length - 1] }],
        async () => {
            const { stores } = await storeApi.getRealTimeStoreRanking(codes[codes.length - 1]);
            return stores;
        }, {
            enabled: !!codes.length,
            refetchOnMount: false,
        });

    return (
        <>
            <NavContent storeRanking={data} />
            <main className={style.main}>
                <div className={style.top_box}>
                    <section className={style.top_box_section}>
                        <h1 className={style.section_title}>실시간 추천 맛집</h1>
                        <ul className={style.section_ul}>
                            {isSuccess && [...Array(10).keys()].map(v => (
                                <li key={'ranking' + v} className={style.section_li}>
                                    {!data[v] ? (
                                        <span className={style.region_ranking}>{v + 1}.</span>
                                    ) : (
                                        <Link to={urlUtils.setPath(BROWSER_PATH.STORE.GET_STORE_REVIEWS, { storeId: data[v].storeId })} className={style.section_li_a}>
                                            <span className={style.region_ranking}>{v + 1}.</span>
                                            <span className={style.region}>[{data[v].regionName}]</span>
                                            {data[v].storeName}
                                        </Link>
                                    )}
                                </li>
                            ))}
                        </ul>
                    </section>
                    <section className={style.top_box_section}>
                        <h1 className={style.section_title}>혼밥 탈출</h1>
                        <ul className={style.section_ul}>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                            <li className={style.section_li}><span>[청운동]</span>가나다</li>
                        </ul>
                    </section>
                </div>
            </main>
        </>
    );
};

export default Main;