import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from "react-router-dom";
import eatTogetherApi from "../../api/eatTogether";
import WriteEatTogetherReply from "./WriteEatTogetherReply";
import { BROWSER_PATH } from "../../constants/path";
import { urlUtils } from "../../utils/urlUtils";

const GetEatTogetherPost = () => {
    const navigate = useNavigate();
    const { postId } = useParams();
    const [post, setPost] = useState({});
    const [replyList, setReplyList] = useState([]);
    const [upReplyId, setUpReplyId] = useState('');

    useEffect(() => {
        const getPost = async () => {
            try {
                const {
                    id, userId, nickname, regionCode, regionName, subject, content
                } = await eatTogetherApi.getEatTogetherPost(postId);
                setPost({
                    ...post,
                    id,
                    userId,
                    nickname,
                    regionCode,
                    regionName,
                    subject,
                    content
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        getPost();
        getReplyList();
    }, []);

    const getReplyList = async () => {
        try {
            const { replies } = await eatTogetherApi.getEatTogetherReplyList(postId);
            setReplyList(replies);
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <button onClick={() => navigate(urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.MODIFY_POST, { postId }))}>수정</button>
            <p>{post.id}</p>
            <p>{post.userId}</p>
            <p>{post.nickname}</p>
            <p>{post.regionCode}</p>
            <p>{post.regionName}</p>
            <p>{post.subject}</p>
            <p>{post.content}</p>
            <div style={{ border: '1px solid black' }}>
                {replyList.map(v => (
                    <div key={'reply' + v.replyId}>
                        <p>{v.nickname}</p>
                        <p>{v.upNickname}</p>
                        <p>{v.content}</p>
                        <p>{v.image}</p>
                        <p>{v.createDatetime}</p>
                        <button onClick={() => setUpReplyId(v.replyId)}>답글쓰기</button>
                    </div>
                ))}
            </div>
            <div>
                <WriteEatTogetherReply postId={postId} getReplyList={getReplyList} upReplyId={upReplyId} />
            </div>
        </div>
    );
};

export default GetEatTogetherPost;