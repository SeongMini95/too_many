import React, { useRef, useState } from 'react';
import { useNavigate } from "react-router-dom";
import style from '../../css/Review/WriteReview.module.css';
import imageApi from "../../api/image";
import reviewApi from "../../api/review";
import { urlUtils } from "../../utils/urlUtils";
import { BROWSER_PATH } from "../../constants/path";

const WriteReview = ({ onClickClose, placeInfo }) => {
    const navigate = useNavigate();

    const [inputs, setInputs] = useState({
        starScore: 0,
        content: '',
        revisitYn: false,
        images: [],
        recommends: [],
        x: placeInfo.x,
        y: placeInfo.y
    });
    const refFileUpload = useRef(null);

    const handlerClickStarScore = (e) => {
        const x = e.nativeEvent.offsetX;

        const star = (Math.floor(x / (204 / 5)) + 1)
        setInputs({
            ...inputs,
            starScore: star
        });
    }

    const handlerClickRecommend = (e) => {
        const id = parseInt(e.currentTarget.dataset.id);
        if (!inputs.recommends.includes(id)) {
            setInputs({
                ...inputs,
                recommends: inputs.recommends.concat(id)
            });
        } else {
            setInputs({
                ...inputs,
                recommends: inputs.recommends.filter(v => v !== id)
            });
        }
    }

    const handlerClickRevisitYn = () => {
        setInputs({
            ...inputs,
            revisitYn: !inputs.revisitYn
        });
    }

    const handlerChangeContent = (e) => {
        const { value } = e.target;

        if (value.length <= 2000) {
            setInputs({
                ...inputs,
                content: value
            });
        } else {
            alert('2000자를 초과 할 수 없습니다.');
        }
    }

    const handlerChangeImage = async () => {
        try {
            const image = refFileUpload.current.files[0];
            if (image) {
                const url = await imageApi.upload(image);
                setInputs({
                    ...inputs,
                    images: inputs.images.concat(url)
                });
            }

            refFileUpload.current.value = '';
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickDeleteImage = (url) => {
        setInputs({
            ...inputs,
            images: inputs.images.filter(v => v !== url)
        });
    }

    const handlerClickWriteReview = async () => {
        try {
            const { storeId } = await reviewApi.writeReview(placeInfo.placeId, inputs);

            const url = urlUtils.setPath(BROWSER_PATH.STORE.GET_STORE_REVIEWS, { storeId });
            navigate(url, { replace: true });
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div className={style.review_modal}>
            <div className={style.review_modal_wrap}>
                <div className={style.inner_layer}>
                    <div className={style.layer_head}>
                        <button className={style.btn_reset} onClick={onClickClose}>취소</button>
                        <strong className={style.tit_layer}>{placeInfo.placeName}</strong>
                        <button className={style.btn_submit} onClick={handlerClickWriteReview}>등록</button>
                    </div>
                </div>
                <div className={style.layer_body}>
                    <div className={style.group_rate}>
                        <div className={style.grade_rate}>
                            <div>
                            <span className={[style.star_rate, style.ico_star].join(' ')} onClick={handlerClickStarScore}>
                                <span className={[style.ico_star, style.inner_star].join(' ')} style={{ width: inputs.starScore * 20 + '%' }}></span>
                            </span>
                            </div>
                        </div>
                        <span>
                        <span className={style.num_rate}>{inputs.starScore}</span>/<span className={style.num_limit}>5</span>
                    </span>
                    </div>
                    <div className={style.group_like}>
                        <strong className={style.tit_group}>
                            이 장소의 추천 포인트는?<span className={style.txt_guide}>(중복선택 가능)</span>
                        </strong>
                        <div className={style.box_like}>
                            <button className={!inputs.recommends.includes(1) ? style.btn_like : [style.btn_like, style.btn_on].join(' ')} onClick={handlerClickRecommend} data-id="1">
                                <span className={style.txt_like}>맛</span>
                            </button>
                            <button className={!inputs.recommends.includes(2) ? style.btn_like : [style.btn_like, style.btn_on].join(' ')} onClick={handlerClickRecommend} data-id="2">
                                <span className={style.txt_like}>가성비</span>
                            </button>
                            <button className={!inputs.recommends.includes(3) ? style.btn_like : [style.btn_like, style.btn_on].join(' ')} onClick={handlerClickRecommend} data-id="3">
                                <span className={style.txt_like}>친절</span>
                            </button>
                            <button className={!inputs.recommends.includes(4) ? style.btn_like : [style.btn_like, style.btn_on].join(' ')} onClick={handlerClickRecommend} data-id="4">
                                <span className={style.txt_like}>분위기</span>
                            </button>
                            <button className={!inputs.recommends.includes(5) ? style.btn_like : [style.btn_like, style.btn_on].join(' ')} onClick={handlerClickRecommend} data-id="5">
                                <span className={style.txt_like}>주차</span>
                            </button>
                        </div>
                    </div>
                    <div className={style.box_evaluation}>
                        <div className={style.group_revisit}>
                            <p className={style.desc_revisit}>재방문 의사가 있습니까?</p>
                            <button className={!inputs.revisitYn ? style.btn_like : [style.btn_like, style.btn_on].join(' ')} onClick={handlerClickRevisitYn}>
                                <span className={style.txt_like}>네</span>
                            </button>
                        </div>
                        <div>
                            <textarea className={style.tf_review} onChange={handlerChangeContent} value={inputs.content} placeholder="리뷰를 작성해주세요."></textarea>
                        </div>
                        <div className={style.group_upload}>
                        <span className={style.thumb_upload}>
                            <label className={style.lab_upload} onClick={() => refFileUpload.current.click()}>사진 등록하기</label>
                            <input type="file" className={style.inp_upload} onChange={handlerChangeImage} ref={refFileUpload} accept="image/jpeg, image/png" />
                        </span>
                            {inputs.images.map(v => (
                                <span key={'image' + v} className={style.thumb_upload}>
                                  <img src={v} className={style.img_thumb} alt="" width="78" height="78" />
                                  <span className={style.frame_g}></span>
                                  <a href={'#none'} className={style.btn_del}>
                                    <span className={style.ico_del} onClick={() => handlerClickDeleteImage(v)}>사진 삭제</span>
                                  </a>
                                </span>
                            ))}
                        </div>
                        <div className={style.group_etc}>
                        <span className={style.num_letter}>
                            <span className="txt_len">{inputs.content.length}</span>
                            <span className={style.num_total}> / 2000</span>
                        </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default WriteReview;