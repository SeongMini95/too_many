import React, { useEffect, useRef, useState } from 'react';
import { Link, useNavigate, useParams } from "react-router-dom";
import eatTogetherApi from "../../api/eatTogether";
import { BROWSER_PATH } from "../../constants/path";
import style from '../../css/EatTogether/GetEatTogetherPost.module.css';
import { urlUtils } from "../../utils/urlUtils";
import { useRecoilValue } from "recoil";
import { userInfoState } from "../../recoils/user";
import imageApi from "../../api/image";

const GetEatTogetherPost = () => {
    const navigate = useNavigate();
    const { postId } = useParams();
    const { nickname } = useRecoilValue(userInfoState);

    const [post, setPost] = useState({
        postId: '',
        isWrite: false,
        nickname: '',
        profile: '',
        regionCode: '',
        regionName: '',
        subject: '',
        content: '',
        createDatetime: ''
    });
    const [replyList, setReplyList] = useState({
        replyCnt: 0,
        list: []
    });
    const [upReplyId, setUpReplyId] = useState(null);
    const [modifyReplyId, setModifyReplyId] = useState(null);
    const [replyInputs, setReplyInputs] = useState({
        content: '',
        image: null
    });
    const [upReplyInputs, setUpReplyInputs] = useState({
        content: '',
        image: null
    });
    const [modifyReplyInputs, setModifyReplyInputs] = useState({
        content: '',
        image: null
    });

    const refModifyReply = useRef(null);
    const refReplyList = useRef([]);
    const refWriteReply = useRef(null);
    const refInputImage = useRef({
        write: null,
        up: null,
        modify: null
    });

    useEffect(() => {
        const getPost = async () => {
            try {
                const {
                    id, isWrite, nickname, profile, regionCode, regionName, subject, content, createDatetime
                } = await eatTogetherApi.getEatTogetherPost(postId);
                setPost({
                    ...post,
                    id,
                    isWrite,
                    nickname,
                    profile,
                    regionCode,
                    regionName,
                    subject,
                    content,
                    createDatetime
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        getPost();
        getReplyList();
    }, []);

    useEffect(() => {
        if (refModifyReply.current) {
            refModifyReply.current.style.height = (refModifyReply.current.scrollHeight) + 'px';
        }
    }, [modifyReplyId]);

    useEffect(() => {
        const ref = refReplyList.current.find(v => v.replyId === refWriteReply.current)?.el;
        if (ref) {
            ref.scrollIntoView();
        }
    }, [replyList.list]);

    const getReplyList = async () => {
        try {
            const { replyCnt, replies } = await eatTogetherApi.getEatTogetherReplyList(postId, 1);
            setReplyList({
                ...replyList,
                replyCnt,
                list: replies
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickDeletePost = async () => {
        try {
            if (window.confirm('게시글을 삭제하시겠습니까?')) {
                await eatTogetherApi.deleteEatTogetherPost(postId);
                navigate(BROWSER_PATH.EAT_TOGETHER.POST_LIST, { replace: true });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickWriteReply = async () => {
        try {
            const replyId = await eatTogetherApi.writeEatTogetherReply(postId, replyInputs);
            setReplyInputs({
                ...replyInputs,
                content: '',
                image: null
            });
            refWriteReply.current = replyId;

            getReplyList();
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickWriteUpReply = async () => {
        try {
            const param = {
                ...upReplyInputs,
                upReplyId
            };

            const replyId = await eatTogetherApi.writeEatTogetherReply(postId, param);
            setUpReplyId(null);
            setUpReplyInputs({
                ...upReplyInputs,
                content: '',
                image: null
            });
            refWriteReply.current = replyId;

            getReplyList();
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickModifyReply = async () => {
        try {
            await eatTogetherApi.modifyEatTogetherReply(postId, modifyReplyId, modifyReplyInputs);
            setModifyReplyId(null);
            setModifyReplyInputs({
                ...modifyReplyInputs,
                content: '',
                image: null
            });

            getReplyList();
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickDeleteReply = async (replyId) => {
        try {
            if (window.confirm('댓글을 삭제하시겠습니까?')) {
                await eatTogetherApi.deleteEatTogetherReply(postId, replyId);

                getReplyList();
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerChangeReplyContent = (e) => {
        setReplyInputs({
            ...replyInputs,
            content: e.target.value
        });
        e.target.style.height = '28px';
        e.target.style.height = (e.target.scrollHeight) + 'px';
    }

    const handlerChangeUpReplyContent = (e) => {
        setUpReplyInputs({
            ...upReplyInputs,
            content: e.target.value
        });
        e.target.style.height = '28px';
        e.target.style.height = (e.target.scrollHeight) + 'px';
    }

    const handlerClickReplyImageRemove = (e) => {
        e.preventDefault();
        setReplyInputs({
            ...replyInputs,
            image: null
        });
    }

    const handlerClickUpReplyImageRemove = (e) => {
        e.preventDefault();
        setUpReplyInputs({
            ...upReplyInputs,
            image: null
        });
    }

    const handlerClickSetUpReply = (replyId) => {
        setUpReplyId(replyId);
        setUpReplyInputs({
            ...upReplyInputs,
            content: '',
            image: null
        });
    }

    const handlerClickCancelUpReply = () => {
        setUpReplyId(null);
        setUpReplyInputs({
            ...upReplyInputs,
            content: '',
            image: null
        });
    }

    const handlerClickSetModifyReply = (reply) => {
        setModifyReplyId(reply.replyId);
        setModifyReplyInputs({
            ...modifyReplyInputs,
            content: reply.content,
            image: reply.image
        });
    }

    const handlerClickCancelModifyReply = () => {
        setModifyReplyId(null);
        setModifyReplyInputs({
            ...modifyReplyInputs,
            content: '',
            image: null
        });
        refModifyReply.current = null;
    }

    const handlerClickModifyReplyImageRemove = (e) => {
        e.preventDefault();
        setModifyReplyInputs({
            ...modifyReplyInputs,
            image: null
        });
    }

    const handlerChangeModifyReplyContent = (e) => {
        setModifyReplyInputs({
            ...modifyReplyInputs,
            content: e.target.value
        });
        e.target.style.height = '28px';
        e.target.style.height = (e.target.scrollHeight) + 'px';
    }

    const handlerChangeWriteReplyImage = async (e) => {
        try {
            const image = e.target.files[0];
            if (image) {
                const url = await imageApi.upload(image);
                setReplyInputs({
                    ...replyInputs,
                    image: url
                });
            } else {
                setReplyInputs({
                    ...replyInputs,
                    image: null
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerChangeUpReplyImage = async (e) => {
        try {
            const image = e.target.files[0];
            if (image) {
                const url = await imageApi.upload(image);
                setUpReplyInputs({
                    ...upReplyInputs,
                    image: url
                });
            } else {
                setUpReplyInputs({
                    ...upReplyInputs,
                    image: null
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerChangeModifyReplyImage = async (e) => {
        try {
            const image = e.target.files[0];
            if (image) {
                const url = await imageApi.upload(image);
                setModifyReplyInputs({
                    ...modifyReplyInputs,
                    image: url
                });
            } else {
                setModifyReplyInputs({
                    ...modifyReplyInputs,
                    image: null
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div className={style.post_wrap}>
            <div className={style.post_inner_wrap}>
                <div className={style.wrap_content}>
                    <div>
                        <h2 className={style.post_region}>[{post.regionName}]</h2>
                        <div className={style.post_subject_wrap}>
                            <h1 className={style.post_subject}>{post.subject}</h1>
                            {post.isWrite && (
                                <div className={style.util_btn_wrap}>
                                    <Link to={urlUtils.setPath(BROWSER_PATH.EAT_TOGETHER.MODIFY_POST, { postId: postId })}>
                                        <button className={style.util_btn}>수정</button>
                                    </Link>
                                    <button onClick={handlerClickDeletePost} className={style.util_btn}>삭제</button>
                                </div>
                            )}
                        </div>
                    </div>
                    <div className={style.info_wrap}>
                        <div className={style.info_user_wrap}>
                            <img className={style.profile_img} src={post.profile ? post.profile : `${process.env.PUBLIC_URL}/assets/image/default_profile.png`} alt="" />
                            <span className={style.nickname}>{post.nickname}</span>
                        </div>
                        <div className={style.reply_date}>{post.createDatetime}</div>
                    </div>
                    <div className={['ck-content', style.post_content_wrap].join(' ')} dangerouslySetInnerHTML={{ __html: post.content }}></div>
                </div>
                <div className={style.wrap_reply}>
                    <h3>댓글 {replyList.replyCnt}</h3>
                    <div className={style.write_reply_wrap}>
                        <div className={style.write_reply_header}>
                            <div className={style.write_reply_nickname}>{nickname}</div>
                            <div className={style.write_reply_content_cnt}>{replyInputs.content.length}/3000</div>
                        </div>
                        <textarea onChange={handlerChangeReplyContent}
                                  value={replyInputs.content}
                                  className={style.reply_content_text}
                                  placeholder="댓글을 작성하세요"
                                  style={{ height: '28px' }} />
                        {replyInputs.image && (
                            <div className={style.upload_reply_image_wrap}>
                                <a href={'#none'} onClick={(e) => handlerClickReplyImageRemove(e)} className={style.reply_image_remove}>
                                    <img className={style.upload_reply_image} src={replyInputs.image} alt="" />
                                </a>
                            </div>
                        )}
                        <div className={style.write_util_wrap}>
                            <div>
                                <input type="file"
                                       onChange={(e) => handlerChangeWriteReplyImage(e)}
                                       ref={(el) => refInputImage.current.write = el}
                                       className="hide"
                                       accept="image/*" />
                                <button onClick={() => refInputImage.current.write.click()} className={style.upload_image_ico} />
                            </div>
                            <div>
                                <button onClick={handlerClickWriteReply} className={style.util_btn}>등록</button>
                            </div>
                        </div>
                    </div>
                    <ul className={style.reply_list_wrap}>
                        {replyList.list.map((v, i) => (
                            <li key={'reply_' + v.replyId} ref={(el) => refReplyList.current[i] = { replyId: v.replyId, el: el }} className={style.reply_li}>
                                <div className={v.replyId === v.upReplyId ? style.reply_area : style.re_reply_area}>
                                    {modifyReplyId !== v.replyId ? (
                                        <>
                                            <div className={style.reply_profile_wrap}>
                                                <img className={style.reply_profile} src={v.profile ? v.profile : `${process.env.PUBLIC_URL}/assets/image/default_profile.png`} alt="프로필 이미지" />
                                                {v.isWriter && (
                                                    <div className={style.writer}>작성자</div>
                                                )}
                                            </div>
                                            <div className={style.reply_info_wrap}>
                                                <div className={style.reply_header}>
                                                    <div style={{ fontWeight: 'bold' }}>{v.nickname}</div>
                                                    <div className={style.reply_date}>{v.createDatetime}</div>
                                                </div>
                                                <div className={style.reply_content}>
                                                    {v.upNickname && (
                                                        <p className={style.up_nickname_wrap}>
                                                            <a className={style.up_nickname} href={'#none'} onClick={(e) => e.preventDefault()}>@{v.upNickname}</a>
                                                        </p>
                                                    )}
                                                    {v.content}
                                                </div>
                                                {v.image && (
                                                    <div className={style.reply_image_wrap}>
                                                        <a href={v.image} target="_blank">
                                                            <img className={style.reply_image} src={v.image} alt="" />
                                                        </a>
                                                    </div>
                                                )}
                                                <div className={style.reply_util_wrap}>
                                                    <button onClick={() => handlerClickSetUpReply(v.replyId)} className={style.util_btn}>답글</button>
                                                    {v.isWrite && (
                                                        <>
                                                            <button onClick={() => handlerClickSetModifyReply(v)} className={style.util_btn}>수정</button>
                                                            <button onClick={() => handlerClickDeleteReply(v.replyId)} className={style.util_btn}>삭제</button>
                                                        </>
                                                    )}
                                                </div>
                                                {upReplyId === v.replyId && (
                                                    <div className={style.write_reply_wrap}>
                                                        <div className={style.write_reply_header}>
                                                            <div className={style.write_reply_nickname}>{nickname}</div>
                                                            <div className={style.write_reply_content_cnt}>{upReplyInputs.content.length}/3000</div>
                                                        </div>
                                                        <textarea onChange={handlerChangeUpReplyContent} value={upReplyInputs.content}
                                                                  className={style.reply_content_text}
                                                                  placeholder={`${v.nickname}님에게 답글을 작성하세요`}
                                                                  style={{ height: '28px' }} />
                                                        {upReplyInputs.image && (
                                                            <div className={style.upload_reply_image_wrap}>
                                                                <a href={'#none'} onClick={(e) => handlerClickUpReplyImageRemove(e)} className={style.reply_image_remove}>
                                                                    <img className={style.upload_reply_image} src={replyInputs.image} alt="" />
                                                                </a>
                                                            </div>
                                                        )}
                                                        <div className={style.write_util_wrap}>
                                                            <div>
                                                                <input type="file"
                                                                       onChange={(e) => handlerChangeUpReplyImage(e)}
                                                                       ref={(el) => refInputImage.current.up = el}
                                                                       className="hide"
                                                                       accept="image/*" />
                                                                <button onClick={() => refInputImage.current.up.click()} className={style.upload_image_ico} />
                                                            </div>
                                                            <div>
                                                                <button onClick={handlerClickWriteUpReply} className={style.util_btn}>등록</button>
                                                                <button onClick={handlerClickCancelUpReply} className={style.util_btn}>취소</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                )}
                                            </div>
                                        </>
                                    ) : (
                                        <div className={style.write_reply_wrap}>
                                            <div className={style.write_reply_header}>
                                                <div className={style.write_reply_nickname}>{nickname}</div>
                                                <div className={style.write_reply_content_cnt}>{modifyReplyInputs.content.length}/3000</div>
                                            </div>
                                            <textarea onChange={handlerChangeModifyReplyContent} value={modifyReplyInputs.content}
                                                      ref={refModifyReply}
                                                      className={style.reply_content_text}
                                                      placeholder="댓글을 수정하세요"
                                                      style={{ height: '28px' }}
                                            />
                                            {modifyReplyInputs.image && (
                                                <div className={style.upload_reply_image_wrap}>
                                                    <a href={'#none'} onClick={(e) => handlerClickModifyReplyImageRemove(e)} className={style.reply_image_remove}>
                                                        <img className={style.upload_reply_image} src={modifyReplyInputs.image} alt="" />
                                                    </a>
                                                </div>
                                            )}
                                            <div className={style.write_util_wrap}>
                                                <div>
                                                    <input type="file"
                                                           onChange={(e) => handlerChangeModifyReplyImage(e)}
                                                           ref={(el) => refInputImage.current.modify = el}
                                                           className="hide"
                                                           accept="image/*" />
                                                    <button onClick={() => refInputImage.current.modify.click()} className={style.upload_image_ico} />
                                                </div>
                                                <div>
                                                    <button onClick={handlerClickModifyReply} className={style.util_btn}>등록</button>
                                                    <button onClick={handlerClickCancelModifyReply} className={style.util_btn}>취소</button>
                                                </div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
                <div className={style.list_btn_wrap}>
                    <Link to={BROWSER_PATH.EAT_TOGETHER.POST_LIST}>
                        <button className={style.list_btn}>목록</button>
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default GetEatTogetherPost;