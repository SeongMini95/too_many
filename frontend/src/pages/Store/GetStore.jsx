import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from "react-router-dom";
import style from '../../css/Store/GetStore.module.css';
import reviewImageApi from "../../api/reviewImage";
import storeApi from "../../api/store";
import reviewApi from "../../api/review";
import { useInView } from "react-intersection-observer";
import WriteReview from "../Review/WriteReview";

const GetStore = () => {
    const navigate = useNavigate();
    const { storeId } = useParams();
    const { ref, inView } = useInView();

    const [previewImage, setPreviewImage] = useState({
        imageCnt: 0,
        images: []
    });
    const [photoType, setPhotoType] = useState('');
    const [store, setStore] = useState({
        storeId: '',
        placeId: '',
        storeName: '',
        categoryName: '',
        regionName: '',
        addressName: '',
        roadAddressName: '',
        x: '',
        y: '',
        likeCnt: 0,
        reviewCnt: 0,
        avgStarScore: 0,
        isLike: false
    });
    const [imageMeta, setImageMeta] = useState({
        isEnd: false,
        moreId: 0
    });
    const [reviewList, setReviewList] = useState([]);
    const [reviewMeta, setReviewMeta] = useState({
        isEnd: false,
        moreId: 0
    });
    const [viewUtil, setViewUtil] = useState(0);
    const [recommendCounts, setRecommendCounts] = useState([]);
    const [isViewPhoto, setIsViewPhoto] = useState(false);
    const [viewPhoto, setViewPhoto] = useState({
        pIndex: 0,
        current: '',
        list: []
    });
    const [writeReview, setWriteReview] = useState({
        isWrite: false,
        placeInfo: null,
        reviewInfo: null
    });

    const refViewImageDiv = useRef(null);
    const refViewPhotoCurrentImg = useRef(null);

    useEffect(() => {
        const getPreviewImageList = async () => {
            try {
                const { imageCnt, images } = await reviewImageApi.getPreviewImageList(storeId);
                setPreviewImage({
                    ...previewImage,
                    imageCnt,
                    images
                });

                if (imageCnt === 2) {
                    setPhotoType(style.photo_type2);
                } else if (imageCnt === 3) {
                    setPhotoType(style.photo_type3);
                } else if (imageCnt === 4) {
                    setPhotoType(style.photo_type4);
                } else if (imageCnt >= 5) {
                    setPhotoType(style.photo_type5);
                }
            } catch (e) {
                alert(e.response.data);
            }
        }

        const getStore = async () => {
            try {
                const store = await storeApi.getStore(storeId);
                setStore({
                    storeId: store.storeId,
                    placeId: store.placeId,
                    storeName: store.storeName,
                    categoryName: store.categoryName,
                    regionName: store.regionName,
                    addressName: store.addressName,
                    roadAddressName: store.roadAddressName,
                    x: store.x,
                    y: store.y,
                    likeCnt: store.likeCnt,
                    reviewCnt: store.reviewCnt,
                    avgStarScore: store.avgStarScore,
                    isLike: store.isLike
                });
            } catch (e) {
                navigate(-1);
                alert(e.response.data);
            }
        }

        const getReviewList = async () => {
            try {
                const { reviews, recommendCounts } = await reviewApi.getReviewList(storeId, null);
                setReviewList(reviews);
                setRecommendCounts(recommendCounts);
                setReviewMeta({
                    ...reviewMeta,
                    isEnd: reviews.length < 5,
                    moreId: reviews[reviews.length - 1].reviewId
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        getStore();
        getPreviewImageList();
        getReviewList();
    }, []);

    useEffect(() => {
        if (refViewImageDiv.current == null || refViewPhotoCurrentImg.current == null) {
            return;
        }

        const maxWidth = refViewImageDiv.current.clientWidth;
        const maxHeight = refViewImageDiv.current.clientHeight;
        const width = refViewPhotoCurrentImg.current.naturalWidth;
        const height = refViewPhotoCurrentImg.current.naturalHeight;

        let resizeWidth = width;
        let resizeHeight = height;

        if (width > height) {
            if (maxWidth < width) {
                resizeWidth = maxWidth;
                resizeHeight = height * maxWidth / width;

                if (resizeHeight > maxHeight) {
                    resizeWidth = width * maxHeight / height;
                    resizeHeight = maxHeight;
                }
            }
        } else {
            if (maxHeight < height) {
                resizeWidth = width * maxHeight / height;
                resizeHeight = maxHeight;

                if (resizeWidth > maxWidth) {
                    resizeWidth = maxWidth;
                    resizeHeight = height * maxWidth / width;
                }
            }
        }

        refViewPhotoCurrentImg.current.style = `width: ${resizeWidth}px; height: ${resizeHeight}px`;
    }, [isViewPhoto, viewPhoto.pIndex, viewPhoto.current]);

    useEffect(() => {
        const getMoreReviewList = async () => {
            try {
                const { reviews } = await reviewApi.getReviewList(storeId, reviewMeta.moreId);
                setReviewList(reviewList.concat(reviews));
                setReviewMeta({
                    ...reviewMeta,
                    isEnd: reviews.length < 5,
                    moreId: reviews[reviews.length - 1].reviewId
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        if (inView && !reviewMeta.isEnd && reviewMeta.moreId !== 0) {
            getMoreReviewList();
        }
    }, [inView]);

    useEffect(() => {
        const getReviewImageList = async () => {
            try {
                if (!imageMeta.isEnd && viewPhoto.list.length - viewPhoto.pIndex < 5) {
                    const { isEnd, moreId, images } = await reviewImageApi.getReviewImageList(storeId, imageMeta.moreId);
                    setImageMeta({
                        ...imageMeta,
                        isEnd,
                        moreId
                    });
                    setViewPhoto({
                        ...viewPhoto,
                        list: viewPhoto.list.concat(images)
                    });
                }
            } catch (e) {
                alert(e.response.data);
            }
        }

        if (viewPhoto.pIndex !== 0) {
            getReviewImageList();
        }
    }, [viewPhoto.pIndex]);

    const handlerClickLikeStore = async (e) => {
        try {
            e.preventDefault();

            const { result, likeCnt } = await storeApi.likeStore(storeId);
            setStore({
                ...store,
                likeCnt,
                isLike: result
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickReviewImage = (e, reviewId, pIndex) => {
        e.preventDefault();

        setIsViewPhoto(true);

        const review = reviewList.find(v => v.reviewId === reviewId);
        setViewPhoto({
            ...viewPhoto,
            pIndex,
            current: review.images[pIndex],
            list: review.images
        });
        setImageMeta({
            ...imageMeta,
            isEnd: true
        });
    }

    const handlerClickViewPhotoDirection = (e, increase) => {
        e.preventDefault();

        if (viewPhoto.pIndex + increase < 0 || viewPhoto.pIndex + increase > viewPhoto.list.length - 1) {
            return;
        }

        const pIndex = viewPhoto.pIndex + increase;
        setViewPhoto({
            ...viewPhoto,
            current: viewPhoto.list[pIndex],
            pIndex: pIndex
        });
    }

    const handlerClickPreviewImage = async (e, pIndex) => {
        e.preventDefault();

        setIsViewPhoto(true);

        const { isEnd, moreId, images } = await reviewImageApi.getReviewImageList(storeId, null);
        setImageMeta({
            ...imageMeta,
            isEnd,
            moreId
        });

        pIndex = pIndex === 4 && previewImage.imageCnt > 5 ? 0 : pIndex;
        setViewPhoto({
            ...viewPhoto,
            current: images[pIndex],
            pIndex: pIndex,
            list: images
        });
    }

    const handlerClickViewPhotoImage = async (e, pIndex) => {
        try {
            e.preventDefault();

            setViewPhoto({
                ...viewPhoto,
                current: viewPhoto.list[pIndex],
                pIndex: pIndex
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickLikeReview = async (reviewId) => {
        try {
            const { result, likeCnt } = await reviewApi.likeReview(reviewId);
            setReviewList(reviewList.map(v => v.reviewId === reviewId ? { ...v, likeCnt, isLike: result } : v));
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickUtilButton = (reviewId) => {
        setViewUtil(viewUtil !== reviewId ? reviewId : 0);
    }

    const handlerClickCloseModifyReview = () => {
        if (window.confirm('리뷰 수정을 취소하시겠습니까?')) {
            setWriteReview({
                ...writeReview,
                isWrite: false,
                reviewInfo: null
            });
        }
    }

    const handlerModifyReviewConfirm = (review) => {
        const { content, starScore, images, recommends } = review;
        setReviewList(reviewList.map(v => v.reviewId === review.reviewId ? {
            ...v,
            content,
            starScore,
            images,
            recommends
        } : v));
        setWriteReview({
            ...writeReview,
            isWrite: false,
            reviewInfo: null
        });
    }

    const handlerWriteReviewConfirm = async () => {
        try {
            const { reviews } = await reviewApi.getRefreshReviewList(storeId, reviewList[reviewList.length - 1].reviewId);
            setReviewList(reviews);

            setWriteReview({
                ...writeReview,
                isWrite: false,
                placeInfo: null,
                reviewInfo: null
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickModifyReview = async (reviewId) => {
        const review = await reviewApi.getReview(reviewId);
        setWriteReview({
            ...writeReview,
            isWrite: true,
            placeInfo: null,
            reviewInfo: {
                ...review,
                recommends: review.recommends.map(v => v.type)
            }
        });
        setViewUtil(0);
    }

    const handlerClickDeleteReview = async (reviewId) => {
        try {
            if (window.confirm('리뷰를 삭제하시겠습니까?')) {
                await reviewApi.deleteReview(reviewId);
                setReviewList(reviewList.filter(v => v.reviewId !== reviewId));
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerWriteReview = () => {
        setWriteReview({
            ...writeReview,
            isWrite: true,
            placeInfo: {
                placeId: store.placeId,
                placeName: store.storeName,
                x: store.x,
                y: store.y
            },
            reviewInfo: null
        });
    }

    const handlerClickCloseWriteReview = () => {
        if (window.confirm('리뷰 작성을 취소하시겠습니까?')) {
            setWriteReview({
                ...writeReview,
                isWrite: false,
                placeInfo: null,
                reviewInfo: null
            });
        }
    }

    return (
        <div id={style['container_wrap']}>
            <div className={style.div_cont}>
                <div className={style.content}>
                    <div className={style.sub_content}>
                        <div className={style.cont_photo}>
                            <div className={style.pic_grade}>
                                <ul className={[style.list_photo, photoType].join(' ')}>
                                    {previewImage.images.map((v, i) => (
                                        <li key={'image_' + v} className={i === 0 && previewImage.imageCnt >= 2 ? style.size_l : i === 1 && previewImage.imageCnt === 4 ? style.size_m : ''}>
                                            <a href={'#none'} onClick={(e) => handlerClickPreviewImage(e, i)} className={style.link_photo} style={{ backgroundImage: `url('${v}')` }}>
                                                {previewImage.imageCnt > 5 && i === 4 && (
                                                    <span className={style.more_photo}>
                                                        <span className={style.num_photo}>+{previewImage.imageCnt}</span>
                                                    </span>
                                                )}
                                                <span className={style.frame_g}></span>
                                            </a>
                                        </li>
                                    ))}
                                </ul>
                                <div className={style.title_point}>
                                    <p className={style.title}>{store.storeName}</p>
                                </div>
                                <div className={style.btxt}>
                                    {store.regionName} | {store.categoryName}
                                </div>
                                <div className={style.basic_info}>
                                    <ul>
                                        <li className={style.location}>{store.roadAddressName}</li>
                                        <li className={style.old_location}>{store.addressName}</li>
                                    </ul>
                                </div>
                                <div className={style.sns_grade}>
                                    <p>
                                        <span className={style.point}>{store.reviewCnt}명의 평가</span>
                                        <strong className={style.lbl_review_point}>{parseFloat(store.avgStarScore).toFixed(1)}점</strong>
                                        <span className={[style.ico_star, style.star_rate].join(' ')}>
                                            <span className={[style.ico_star, style.inner_star].join(' ')} style={{ width: `${(store.avgStarScore / 5) * 100}%` }}></span>
                                        </span>
                                    </p>
                                </div>
                                <div className={style.favor_pic}>
                                    <a href={'#none'} className={!store.isLike ? style.favor : [style.favor, style.favor_on].join(' ')} onClick={handlerClickLikeStore}>
                                        <span>좋아요(<i>{store.likeCnt}</i>)</span>
                                    </a>
                                    <button onClick={handlerWriteReview} className={style.appra}>
                                        <span>리뷰작성</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div className={style.review_list}>
                            <p className={style.review_title}>{store.reviewCnt}건의 방문자 평가</p>
                            <div className={style.grade_info}>
                                <p>
                                    <strong className={style.review_list_point}>{parseFloat(store.avgStarScore).toFixed(1)}점</strong>
                                    <span className={[style.ico_star, style.star_rate].join(' ')}>
                                            <span className={[style.ico_star, style.inner_star].join(' ')} style={{ width: `${(store.avgStarScore / 5) * 100}%` }}></span>
                                    </span>
                                </p>
                                {!!recommendCounts.length && (
                                    <div className={style.view_like_point}>
                                        {recommendCounts.map(v => (
                                            <span key={'recommend_' + v.type} className={[style.chip_like_point, style['chip_like_point_type' + v.type]].join(' ')}>{v.name} {v.count}</span>
                                        ))}
                                    </div>
                                )}
                            </div>
                            {reviewList.map(v => (
                                <div key={'review' + v.reviewId} className={style.review_graph}>
                                    <div className={style.profile}>
                                        <div className={style.profile_img}
                                             style={{ backgroundImage: `url('${v.profile ? v.profile : `${process.env.PUBLIC_URL}/assets/image/default_profile.png`}')` }}></div>
                                    </div>
                                    <p className={style.person_grade}>
                                    <span className={style.person_btxt}>
                                        <strong>{v.nickname}</strong> ({v.userReviewCnt}곳 작성, {v.userLikeCnt}개 공감받음)
                                    </span>
                                        <span className={style.star_date}>
                                        <span className={[style.review_ico_star, style.review_star_rate].join(' ')}>
                                            <span className={[style.review_ico_star, style.review_inner_star].join(' ')} style={{ width: `${(v.starScore / 5) * 100}%` }}></span>
                                        </span>
                                        <i className={style.date}>{v.createDate}</i>
                                    </span>
                                    </p>
                                    <p className={style.point_detail}>
                                        {v.recommends.map(v2 => (
                                            <span key={'userRecommend' + v2.type} className={style.point_detail_like_point}>{v2.name}</span>
                                        ))}
                                    </p>
                                    <p className={style.content_btxt}>{v.content}</p>
                                    <div className={style.review_image_wrap}>
                                        <ul className={style.review_image_photo_list}>
                                            {v.images.slice(0, 4).map((v2, i2) => (
                                                <li key={'reviewImage' + v2} className={v.images.length > 4 && i2 === 3 ? style.lst : ''}>
                                                    <a href={'#none'} className={style.box_photo} onClick={(e) => handlerClickReviewImage(e, v.reviewId, i2)}>
                                                        <img className={style.img_thumb} src={v2} alt="" />
                                                        {v.images.length > 4 && i2 === 3 && (
                                                            <span className={style.info_photo_num}>
                                                                <span className={style.txt_num}>+{v.images.length}</span>
                                                            </span>
                                                        )}
                                                    </a>
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                    <div onClick={() => handlerClickLikeReview(v.reviewId)}
                                         className={!v.isLike ? style.review_like_btn : [style.review_like_btn, style.active].join(' ')}>공감({v.likeCnt})
                                    </div>
                                    {v.isWrite && (
                                        <div className={style.wrap_util}>
                                            <button onClick={() => handlerClickUtilButton(v.reviewId)} className={viewUtil !== v.reviewId ? style.btn_util : [style.btn_util, style.util_on].join(' ')}>
                                                <span className={[style.btn_util_ico, style.ico_more].join(' ')}>메뉴</span>
                                            </button>
                                            <div className={style.layer_util}>
                                                <ul>
                                                    <li>
                                                        <a onClick={() => handlerClickModifyReview(v.reviewId)} href={'#none'} className={style.link_util}>수정</a>
                                                    </li>
                                                    <li>
                                                        <a onClick={() => handlerClickDeleteReview(v.reviewId)} href={'#none'} className={style.link_util}>삭제</a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                        {!reviewMeta.isEnd && (
                            <a href={'#none'} className={style.more_btn} ref={ref}>
                                <span>더보기</span>
                            </a>
                        )}
                    </div>
                </div>
            </div>

            {isViewPhoto && (
                <div className={style.map_layer}>
                    <div className={style.inner_map_photo}>
                        <div className={style.layer_head}>
                            <strong className={style.tit_photo}>
                                포토
                                <span className={style.layer_head_num}>
                                <span>{viewPhoto.pIndex + 1}</span> / <span style={{ fontWeight: '400' }}>{imageMeta.isEnd ? viewPhoto.list.length : previewImage.imageCnt}</span>
                            </span>
                            </strong>
                        </div>
                        <div className={style.layer_body}>
                            <div className={style.view_photo}>
                                <div className={style.view_image} ref={refViewImageDiv}>
                                    <span className={style.gap_g}></span>
                                    <img className={style.img_photo} src={viewPhoto.current} ref={refViewPhotoCurrentImg} alt="" />
                                    <span className={style.layer_body_frame_g}></span>
                                    <a href={'#none'} onClick={(e) => handlerClickViewPhotoDirection(e, -1)}
                                       className={[style.link_direction, style.link_prev].join(' ')}
                                       style={{ display: viewPhoto.pIndex <= 0 ? 'none' : '' }}>
                                        <span className={[style.ico_comm, style.ico_prev].join(' ')}>이전</span>
                                    </a>
                                    <a href={'#none'} onClick={(e) => handlerClickViewPhotoDirection(e, 1)}
                                       className={[style.link_direction, style.link_next].join(' ')}
                                       style={{ display: viewPhoto.pIndex >= viewPhoto.list.length - 1 ? 'none' : '' }}>
                                        <span className={[style.ico_comm, style.ico_next].join(' ')}>다음</span>
                                    </a>
                                </div>
                            </div>
                            <div className={style.item_photo}>
                                <div className={style.wrap_preview}>
                                    <ul className={style.list_photo_view} style={{ marginLeft: viewPhoto.pIndex < 4 ? '0px' : `${-30 + (viewPhoto.pIndex - 4) * -90}px` }}>
                                        {viewPhoto.list.map((v, i) => (
                                            <li key={'viewImage_' + v}>
                                                <a href={'#none'} className={style.link_item} onClick={(e) => handlerClickViewPhotoImage(e, i)}>
                                                    {viewPhoto.pIndex === i && (
                                                        <em className={style.current_photo}></em>
                                                    )}
                                                    <img className={style.img_thumb_view} src={v} alt="" />
                                                    <span className={style.list_photo_view_frame_g}></span>
                                                </a>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div>
                            <a href={'#none'} className={style.link_close} onClick={(e) => {
                                e.preventDefault();
                                setIsViewPhoto(false);
                            }}>닫기</a>
                        </div>
                    </div>
                </div>
            )}

            {writeReview.isWrite && (
                <WriteReview onClickClose={writeReview.placeInfo ? handlerClickCloseWriteReview : handlerClickCloseModifyReview}
                             placeInfo={writeReview.placeInfo}
                             reviewInfo={writeReview.reviewInfo}
                             modifyReview={handlerModifyReviewConfirm}
                             writeInReview={handlerWriteReviewConfirm} />
            )}
        </div>
    );
};

export default GetStore;