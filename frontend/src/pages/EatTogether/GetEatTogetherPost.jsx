import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from "react-router-dom";
import eatTogetherApi from "../../api/eatTogether";
import WriteEatTogetherReply from "./WriteEatTogetherReply";
import { BROWSER_PATH } from "../../constants/path";
import { urlUtils } from "../../utils/urlUtils";
import { useRecoilValue } from "recoil";
import { userInfoState } from "../../recoils/user";

const GetEatTogetherPost = () => {
    const navigate = useNavigate();
    const { postId } = useParams();
    const userInfo = useRecoilValue(userInfoState);
    const [post, setPost] = useState({
        id: '',
        userId: '',
        nickname: '',
        regionCode: '',
        regionName: '',
        subject: '',
        content: ''
    });
    const [replyList, setReplyList] = useState([]);
    const [upReplyId, setUpReplyId] = useState('');
    const [modifyReplyId, setModifyReplyId] = useState(null);
    const [replyContent, setReplyContent] = useState('');

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

    const handlerClickDelete = async () => {
        try {
            await eatTogetherApi.deleteEatTogetherPost(postId);
            navigate(BROWSER_PATH.EAT_TOGETHER.LIST_POST, { replace: true });
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickModifyReply = async () => {
        try {
            await eatTogetherApi.modifyEatTogetherReply(postId, modifyReplyId, { content: replyContent });
            setModifyReplyId(null);
            await getReplyList();
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickDeleteReply = async (replyId) => {
        try {
            await eatTogetherApi.deleteEatTogetherReply(postId, replyId);
            await getReplyList();
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <button onClick={() => navigate(urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.MODIFY_POST, { postId }))}>수정</button>
            <button onClick={handlerClickDelete}>삭제</button>
            <p>{post.id}</p>
            <p>{post.userId}</p>
            <p>{post.nickname}</p>
            <p>{post.regionCode}</p>
            <p>{post.regionName}</p>
            <p>{post.subject}</p>
            <p>{post.content}</p>
            <div style={{ border: '1px solid black' }}>
                {replyList.map(v => v.replyId !== modifyReplyId ? (
                    <div key={'reply' + v.replyId}>
                        <p>{v.nickname}</p>
                        <p>{v.upNickname}</p>
                        <p>{v.content}</p>
                        <p>{v.image}</p>
                        <p>{v.createDatetime}</p>
                        {v.userId === userInfo.id && (
                            <>
                                <button onClick={() => setModifyReplyId(v.replyId)}>수정</button>
                                <button onClick={() => handlerClickDeleteReply(v.replyId)}>삭제</button>
                            </>
                        )}
                        <button onClick={() => setUpReplyId(v.replyId)}>답글쓰기</button>
                    </div>
                ) : (
                    <div key={'reply' + v.replyId}>
                        <button onClick={() => setModifyReplyId(null)}>취소</button>
                        <textarea onChange={(e) => setReplyContent(e.target.value)} value={replyContent}></textarea>
                        <button onClick={handlerClickModifyReply}>작성</button>
                    </div>
                ))}
            </div>
            <div>
                <WriteEatTogetherReply postId={postId}
                                       getReplyList={getReplyList}
                                       upReplyId={upReplyId} />
            </div>
        </div>
    );
};

export default GetEatTogetherPost;