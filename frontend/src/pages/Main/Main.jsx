import React, { useState } from 'react';
import NavContent from "./NavContent";
import style from '../../css/Main/Main.module.css';
import RealTimeStoreRanking from "./RealTimeStoreRanking";
import RecentEatTogetherPost from "./RecentEatTogetherPost";
import GetStoreList from "./GetStoreList";

const Main = () => {
    const [storeRanking, setStoreRaking] = useState([]);

    return (
        <>
            <NavContent storeRanking={storeRanking} />
            <main className={style.main}>
                <div className={style.top_box}>
                    <RealTimeStoreRanking getStoreRanking={setStoreRaking} />
                    <RecentEatTogetherPost />
                </div>
                <GetStoreList />
            </main>
        </>
    );
};

export default Main;