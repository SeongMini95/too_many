import React, { useEffect, useState } from 'react';
import eatTogetherApi from "../../api/eatTogether";

const WriteEatTogetherReply = ({ postId, getReplyList, upReplyId }) => {
    const [inputs, setInputs] = useState({
        upReplyId: upReplyId,
        content: '',
        image: ''
    });

    useEffect(() => {
        setInputs({
            ...inputs,
            upReplyId: upReplyId
        })
    }, [upReplyId]);

    const handlerClickWrite = async () => {
        try {
            await eatTogetherApi.writeEatTogetherReply(postId, inputs);
            getReplyList();
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <textarea name="content"
                      onChange={(e) => setInputs({ ...inputs, content: e.target.value })}
                      value={inputs.content}></textarea>
            <button onClick={handlerClickWrite}>작성</button>
        </div>
    );
};

export default WriteEatTogetherReply;