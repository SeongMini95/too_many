import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from "react-router-dom";
import eatTogetherApi from "../../api/eatTogether";
import { urlUtils } from "../../utils/urlUtils";
import { BROWSER_PATH } from "../../constants/path";
import Editor from "ckeditor5-custom-build";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import style from '../../css/EatTogether/WriteEatTegetherPost.module.css';
import { useRecoilValue } from "recoil";
import { accessTokenState } from "../../recoils/auth";

const ModifyEatTogetherPost = () => {
    const navigate = useNavigate();
    const { postId } = useParams();
    const accessToken = useRecoilValue(accessTokenState)

    const [regionName, setRegionName] = useState('');
    const [subject, setSubject] = useState('');
    const [content, setContent] = useState('');

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

    useEffect(() => {
        const getPost = async () => {
            try {
                const { regionName, subject, content } = await eatTogetherApi.getEatTogetherPost(postId);
                setSubject(subject);
                setContent(content);
                setRegionName(regionName);
            } catch (e) {
                alert(e.response.data);
            }
        }

        getPost();
    }, []);

    const handlerChangeSubject = (e) => {
        setSubject(e.target.value);
    }

    const handlerClickModify = async () => {
        try {
            const param = {
                subject,
                content
            };

            await eatTogetherApi.modifyEatTogetherPost(postId, param);

            const url = urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.GET_POST, { postId });
            navigate(url, { replace: true });
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div className={style.post_wrap}>
            <div className={style.post_inner_wrap}>
                <h1 className={style.location_title}>[{regionName}]</h1>
                <div>
                    <input type="text" name="subject" onChange={handlerChangeSubject} value={subject} className={style.subject} placeholder={'제목을 입력하세요.'} />
                </div>
                <div>
                    <CKEditor
                        editor={Editor}
                        data={content}
                        config={ckConfig}
                        onChange={(event, editor) => setContent(editor.getData())}
                    />
                </div>
                <div className={style.btn_wrap}>
                    <button onClick={handlerClickModify} className={style.btn}>수정</button>
                    <Link to={urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.GET_POST, { postId })}>
                        <button className={style.btn}>취소</button>
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default ModifyEatTogetherPost;