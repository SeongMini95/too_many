import React from 'react';
import NavContent from "./NavContent";
import style from '../../css/Main/Main.module.css';
import storeApi from "../../api/store";
import { useQuery } from "@tanstack/react-query";
import { API_PATH } from "../../constants/path";
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";

const Main = () => {
    const { codes } = useRecoilValue(positionState);

    const { data, isSuccess } = useQuery(
        [API_PATH.STORE.GET_TODAY_STORE_RANKING, { regionCode: codes[codes.length - 1] }],
        async () => {
            const { stores } = await storeApi.getTodayStoreRanking(codes[codes.length - 1]);
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
                        <h1 className={style.section_title}>오늘의 추천 맛집</h1>
                        <ul className={style.section_ul}>
                            {/*{isSuccess && data.map((v, i) => (
                                <li key={'ranking' + i} className={style.section_li}>
                                    <span className={style.region_ranking}>{i + 1}.</span> {v && (
                                    <>
                                        <span className={style.region}>[{v.regionName}]</span>
                                        {v.storeName}
                                    </>
                                )}
                                </li>
                            ))}*/}
                            {isSuccess && [...Array(10).keys()].map(v => (
                                <li key={'ranking' + v} className={style.section_li}>
                                    <span className={style.region_ranking}>{v + 1}.</span> {data[v] && (
                                    <>
                                        <span className={style.region}>[{data[v].regionName}]</span>
                                        {data[v].storeName}
                                    </>
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