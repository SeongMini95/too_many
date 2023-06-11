import React, { useState } from 'react';
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import eatTogetherApi from "../../api/eatTogether";
import { Link, useNavigate } from "react-router-dom";
import { BROWSER_PATH } from "../../constants/path";
import { urlUtils } from "../../utils/urlUtils";
import Editor from "ckeditor5-custom-build/build/ckeditor";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import style from '../../css/EatTogether/WriteEatTegetherPost.module.css';
import { accessTokenState } from "../../recoils/auth";

const WriteEatTogetherPost = () => {
    const navigate = useNavigate();
    const accessToken = useRecoilValue(accessTokenState)
    const { lastCode, address } = useRecoilValue(positionState);

    const [inputs, setInputs] = useState({
        subject: '',
        content: ''
    });

    const ckConfig = {
        simpleUpload: {
            uploadUrl: `${process.env.REACT_APP_BASE_URL}/api/image/upload/editor`,
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        },
        fontSize: {
            options: [
                12,
                13,
                14,
                15,
                'default',
                17,
                18,
                19,
                20
            ],
            supportAllValues: true
        }
    };

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
                regionCode: lastCode
            };
            const postId = await eatTogetherApi.writeEatTogetherPost(param);

            const url = urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.GET_POST, { postId });
            navigate(url, { replace: true });
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div className={style.post_wrap}>
            <div className={style.post_inner_wrap}>
                <h1 className={style.location_title}>[{address}]</h1>
                <div>
                    <input type="text" name="subject" onChange={handlerChangeInputs} value={inputs.subject} className={style.subject} placeholder={'제목을 입력하세요.'} />
                </div>
                <div>
                    <CKEditor
                        editor={Editor}
                        config={ckConfig}
                        onChange={(event, editor) => setInputs({
                            ...inputs,
                            content: editor.getData()
                        })}
                    />
                </div>
                <div className={style.btn_wrap}>
                    <button onClick={handlerClickWrite} className={style.btn}>작성</button>
                    <Link to={BROWSER_PATH.EAT_TOGETHER.POST_LIST}>
                        <button className={style.btn}>취소</button>
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default WriteEatTogetherPost;