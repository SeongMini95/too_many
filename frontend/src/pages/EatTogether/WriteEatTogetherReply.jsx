import React, { useState } from 'react';
import eatTogetherApi from "../../api/eatTogether";

const WriteEatTogetherReply = ({ postId }) => {
    const [inputs, setInputs] = useState({
        upReplyId: '',
        content: '',
        image: ''
    });

    const handlerClickWrite = async () => {
        try {
            await eatTogetherApi.writeEatTogetherReply(postId, inputs);
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <textarea name="content"
                      onChange={(e) => setInputs({ ...inputs, content: e.target.value })}
                      value={inputs.reply}></textarea>
            <button onClick={handlerClickWrite}>작성</button>
        </div>
    );
};

export default WriteEatTogetherReply;