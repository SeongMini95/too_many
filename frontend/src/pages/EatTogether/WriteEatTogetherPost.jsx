import React, { useState } from 'react';
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import eatTogetherApi from "../../api/eatTogether";
import { useNavigate } from "react-router-dom";
import { BROWSER_PATH } from "../../constants/path";
import { urlUtils } from "../../utils/urlUtils";

const WriteEatTogetherPost = () => {
    const navigate = useNavigate();
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

            const url = urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.GET_POST, { postId });
            navigate(url, { replace: true });
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