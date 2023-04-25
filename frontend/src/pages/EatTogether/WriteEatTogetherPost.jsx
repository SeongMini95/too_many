import React, { useState } from 'react';
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import eatTogetherApi from "../../api/eatTogether";

const WriteEatTogetherPost = () => {
    const { code } = useRecoilValue(positionState);
    const [inputs, setInputs] = useState({
        subject: '',
        content: ''
    });

    const handlerChangeInputs = (e) => {
        const { name, value } = e.target;
        setInputs({
            ...inputs,
            [name]: value
        });
    }

    const handlerClickWrite = async () => {
        try {
            const param = {
                ...inputs,
                regionCode: code
            };
            const postId = await eatTogetherApi.writeEatTogetherPost(param);
            console.log(postId);
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <div>
                <input type="text" name="subject" onChange={handlerChangeInputs} value={inputs.subject} />
            </div>
            <div>
                <textarea name="content" onChange={handlerChangeInputs} value={inputs.content}></textarea>
            </div>
            <div>
                <button onClick={handlerClickWrite}>작성</button>
            </div>
        </div>
    );
};

export default WriteEatTogetherPost;