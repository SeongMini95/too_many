import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from "react-router-dom";
import eatTogetherApi from "../../api/eatTogether";
import { urlUtils } from "../../utils/urlUtils";
import { BROWSER_PATH } from "../../constants/path";

const ModifyEatTogetherPost = () => {
    const navigate = useNavigate();
    const { postId } = useParams();
    const [inputs, setInputs] = useState({
        subject: '',
        content: ''
    });

    useEffect(() => {
        const getPost = async () => {
            const { subject, content } = await eatTogetherApi.getEatTogetherPost(postId);
            setInputs({
                ...inputs,
                subject,
                content
            });
        }

        getPost();
    }, [postId]);

    const handlerChangeInputs = (e) => {
        const { name, value } = e.target;
        setInputs({
            ...inputs,
            [name]: value
        });
    }

    const handlerClickModify = async () => {
        try {
            await eatTogetherApi.modifyEatTogetherPost(postId, inputs);

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
                <button onClick={handlerClickModify}>수정</button>
            </div>
        </div>
    );
};

export default ModifyEatTogetherPost;