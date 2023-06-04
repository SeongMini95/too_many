import React, { useEffect, useState } from 'react';
import style from "../../css/Main/Main.module.css";
import { Link } from "react-router-dom";
import { urlUtils } from "../../utils/urlUtils";
import { BROWSER_PATH } from "../../constants/path";
import storeApi from "../../api/store";
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";

const RealTimeStoreRanking = ({ getStoreRanking }) => {
    const { lastCode } = useRecoilValue(positionState);

    const [storeList, setStoreList] = useState([]);

    useEffect(() => {
        const getStore = async () => {
            try {
                if (lastCode) {
                    const { stores } = await storeApi.getRealTimeStoreRanking(lastCode);
                    setStoreList(stores);
                    getStoreRanking(stores);
                }
            } catch (e) {
                alert(e.response.data);
            }
        }

        getStore();
    }, [lastCode]);

    return (
        <section className={style.top_box_section_first}>
            <h1 className={style.section_title}>실시간 추천 맛집</h1>
            <ul className={style.section_ul}>
                {[...Array(10).keys()].map(v => (
                    <li key={'ranking' + v} className={style.section_li}>
                        {!storeList[v] ? (
                            <div className={style.ranking}>{v + 1}.</div>
                        ) : (
                            <Link to={urlUtils.setPath(BROWSER_PATH.STORE.GET_STORE_REVIEWS, { storeId: storeList[v].storeId })} className={style.section_li_a}>
                                <div className={style.ranking}>{v + 1}.</div>
                                <div className={style.region}>[{storeList[v].regionName}]</div>
                                <div className={style.section_content}>{storeList[v].storeName}</div>
                            </Link>
                        )}
                    </li>
                ))}
            </ul>
        </section>
    );
};

export default RealTimeStoreRanking;