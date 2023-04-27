import React, { useEffect, useState } from 'react';
import { useParams } from "react-router-dom";
import eatTogetherApi from "../../api/eatTogether";
import WriteEatTogetherReply from "./WriteEatTogetherReply";

const GetEatTogetherPost = () => {
    const { postId } = useParams();
    const [post, setPost] = useState({});

    useEffect(() => {
        const getPost = async () => {
            try {
                const {
                    id,
                    userId,
                    nickname,
                    regionCode,
                    regionName,
                    subject,
                    content
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
    }, []);

    return (
        <div>
            <p>{post.id}</p>
            <p>{post.userId}</p>
            <p>{post.nickname}</p>
            <p>{post.regionCode}</p>
            <p>{post.regionName}</p>
            <p>{post.subject}</p>
            <p>{post.content}</p>
            <div>
                <WriteEatTogetherReply postId={postId} />
            </div>
        </div>
    );
};

export default GetEatTogetherPost;