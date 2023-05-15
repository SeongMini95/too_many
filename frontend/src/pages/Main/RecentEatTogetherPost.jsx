import React, { useEffect } from 'react';
import style from "../../css/Main/Main.module.css";
import { Link } from "react-router-dom";
import { urlUtils } from "../../utils/urlUtils";
import { API_PATH, BROWSER_PATH } from "../../constants/path";
import { useQuery } from "@tanstack/react-query";
import eatTogetherApi from "../../api/eatTogether";
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";

const RecentEatTogetherPost = () => {
    const { codes, lastCode } = useRecoilValue(positionState);

    const { data: recentPostList, isSuccess: isRecentPostListSuccess } = useQuery(
        [API_PATH.EAT_TOGETHER.GET_RECENT_POST_LIST, { regionCode: lastCode }],
        async () => {
            const { posts } = await eatTogetherApi.getRecentEatTogetherPostList(lastCode);
            return posts;
        }, {
            enabled: !!lastCode,
            refetchOnMount: false
        }
    );

    useEffect(() => {
    }, [codes.region1, codes.region2, codes.region3]);

    return (
        <section className={style.top_box_section_second}>
            <h1 className={style.section_title}>
                <span>혼밥 탈출</span>
                <div className={style.section_more_post_wrap}>
                    <Link to={BROWSER_PATH.EAT_TOGETHER.LIST_POST} className={style.section_more_post}>더 보기...</Link>
                </div>
            </h1>
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
    );
};

export default RecentEatTogetherPost;