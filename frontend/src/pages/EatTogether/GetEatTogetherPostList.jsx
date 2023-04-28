import React, { useEffect, useState } from 'react';
import eatTogetherApi from "../../api/eatTogether";
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";

const GetEatTogetherPostList = () => {
    const { code } = useRecoilValue(positionState);
    const [postList, setPostList] = useState([]);
    const [meta, setMeta] = useState(null);

    useEffect(() => {
        const getPostList = async () => {
            try {
                const { posts } = await eatTogetherApi.getEatTogetherPostList(code);
                setPostList(posts);
                setMeta(posts[posts.length - 1].id);
            } catch (e) {
                console.log(e);
                alert(e.response.data);
            }
        }

        if (code) {
            getPostList();
        }
    }, [code]);

    const handlerClickMorePosts = async () => {
        try {
            const { posts } = await eatTogetherApi.getEatTogetherPostList(code, meta);
            setPostList(postList.concat(posts));
            setMeta(posts[posts.length - 1].id);
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            {postList.map(v => (
                <div key={'post' + v.id} style={{ border: '1px solid black' }}>
                    <p>{v.id}</p>
                    <p>{v.nickname}</p>
                    <p>{v.regionName}</p>
                    <p>{v.subject}</p>
                    <p>{v.createDatetime}</p>
                </div>
            ))}
            <button onClick={handlerClickMorePosts}>더 보기</button>
        </div>
    );
};

export default GetEatTogetherPostList;