import React, { useEffect, useState } from 'react';
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import style from '../../css/EatTogether/GetEatTogetherPostList.module.css';
import { Link } from "react-router-dom";
import { BROWSER_PATH } from "../../constants/path";
import eatTogetherApi from "../../api/eatTogether";
import { urlUtils } from "../../utils/urlUtils";
import { useInView } from "react-intersection-observer";

const GetEatTogetherPostList = () => {
    const { lastCode, address } = useRecoilValue(positionState);
    const { ref, inView } = useInView();

    const [postList, setPostList] = useState([]);
    const [meta, setMeta] = useState({
        isEnd: true,
        moreId: 0
    });

    useEffect(() => {
        const getPostList = async () => {
            try {
                const { posts } = await eatTogetherApi.getEatTogetherPostList(lastCode, null);
                setPostList(posts);
                setMeta({
                    ...meta,
                    isEnd: posts.length < 30,
                    moreId: posts[posts.length - 1].postId
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        if (lastCode) {
            getPostList();
        }
    }, [lastCode]);

    useEffect(() => {
        const getPostList = async () => {
            try {
                const { posts } = await eatTogetherApi.getEatTogetherPostList(lastCode, meta.moreId);
                setPostList(postList.concat(posts));
                setMeta({
                    ...meta,
                    isEnd: posts.length < 30,
                    moreId: posts[posts.length - 1].postId
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        if (inView && lastCode && meta.moreId !== 0) {
            getPostList();
        }
    }, [inView]);

    const handlerScrollTop = () => {
        window.scrollTo(0, 0);
    }

    return (
        <div className={style.post_wrap}>
            <div className={style.post_inner_wrap}>
                <h1 className={style.location_title}>[{address}]</h1>
                <ul className={style.post_ul}>
                    {!postList.length && (
                        <li>
                            <span className={style.post_link} style={{ justifyContent: 'center' }}>
                                게시글이 없습니다.
                            </span>
                        </li>
                    )}

                    {postList.map(v => (
                        <li key={'post_' + v.postId} className={style.post_li}>
                            <Link to={urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.GET_POST, { postId: v.postId })} className={style.post_link}>
                                <span className={style.post_region_name}>[{v.regionName}]</span>
                                <div className={style.post_title_wrap}>
                                    <span className={style.post_title}>{v.subject}</span>
                                    <span className={style.reply_count}>{v.replyCnt}</span>
                                </div>
                                <span className={style.post_nickname}>{v.nickname}</span>
                                <span className={style.post_create}>{v.createDatetime}</span>
                            </Link>
                        </li>
                    ))}
                </ul>
                {!meta.isEnd && (
                    <div ref={ref}></div>
                )}
            </div>
            <Link to={BROWSER_PATH.EAT_TOGETHER.WRITE_POST}>
                <span className={style.write}></span>
            </Link>
            <span onClick={handlerScrollTop} className={style.up}></span>
        </div>
    );
};

export default GetEatTogetherPostList;