import React, { useEffect, useState } from 'react';
import { useParams } from "react-router-dom";
import storeApi from "../../api/store";
import reviewApi from "../../api/review";
import WriteReview from "../Review/writeReview";
import { useRecoilValue } from "recoil";
import { userInfoState } from "../../recoils/user";

const StoreReviews = () => {
    const { storeId } = useParams();
    const { id } = useRecoilValue(userInfoState);
    const [storeInfo, setStoreInfo] = useState({
        store: {
            storeId: '',
            placeId: '',
            storeName: '',
            categoryName: '',
            addressName: '',
            roadAddressName: '',
            x: '',
            y: '',
            likeCnt: 0
        },
        previewImages: [],
        reviews: []
    });
    const [meta, setMeta] = useState({
        isEnd: true,
        reviewId: ''
    });
    const [storeLikeLog, setStoreLikeLog] = useState(false);
    const [reviewLikeLogList, setReviewLikeLogList] = useState([]);
    const [writeReview, setWriteReview] = useState({
        writeYn: false,
        review: null,
    });

    useEffect(() => {
        const getStoreReviews = async () => {
            try {
                const { store, previewImages } = await storeApi.getStore(storeId);
                const { reviews } = await reviewApi.getReviewList(storeId);

                setStoreInfo({
                    ...storeInfo,
                    store: {
                        storeId: store.storeId,
                        placeId: store.placeId,
                        storeName: store.storeName,
                        categoryName: store.categoryName,
                        addressName: store.addressName,
                        roadAddressName: store.roadAddressName,
                        x: store.x,
                        y: store.y,
                        likeCnt: store.likeCnt
                    },
                    previewImages,
                    reviews
                });
                setMeta({
                    ...meta,
                    isEnd: reviews.length === 0 || reviews.length !== 5,
                    reviewId: reviews[reviews.length - 1].reviewId
                });
            } catch (e) {
                alert(e.response.data);
            }
        }

        const getStoreLikeLog = async () => {
            try {
                const savedYn = await storeApi.getStoreLikeLogOfUser(storeId);
                setStoreLikeLog(savedYn);
            } catch (e) {
                alert(e.response.data);
            }
        }

        const getReviewLikeLogList = async () => {
            try {
                const logs = await reviewApi.getReviewLikeLogListOfStore(storeId);
                setReviewLikeLogList(logs);
            } catch (e) {
                alert(e.response.data);
            }
        }

        if (id) {
            getStoreReviews();
            getStoreLikeLog();
            getReviewLikeLogList();
        }
    }, [id]);

    const handlerClickMoreReview = async () => {
        try {
            const { isEnd, reviewId } = meta;
            if (!isEnd) {
                const { reviews } = await reviewApi.getReviewList(storeId, reviewId);
                if (reviews.length !== 0) {
                    setStoreInfo({
                        ...storeInfo,
                        reviews: storeInfo.reviews.concat(reviews)
                    });
                    setMeta({
                        ...meta,
                        isEnd: false,
                        reviewId: reviews[reviews.length - 1].reviewId
                    });
                } else {
                    setMeta({
                        ...meta,
                        isEnd: true,
                    });
                }
            }
        } catch (e) {
            alert(e.response.data);
        }
    };

    const handlerClickDeleteReview = async (reviewId) => {
        try {
            if (window.confirm('리뷰를 삭제하시겠습니까?')) {
                await reviewApi.deleteReview(reviewId);
                setStoreInfo({
                    ...storeInfo,
                    reviews: storeInfo.reviews.filter(v => v.reviewId !== reviewId)
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickLikeReview = async (reviewId) => {
        try {
            const savedYn = await reviewApi.likeReview(reviewId);
            if (savedYn) {
                setReviewLikeLogList([...reviewLikeLogList, reviewId]);
                setStoreInfo({
                    ...storeInfo,
                    reviews: storeInfo.reviews.map(v => v.reviewId === reviewId ? {
                        ...v,
                        likeCnt: v.likeCnt + 1
                    } : v)
                });
            } else {
                setReviewLikeLogList(reviewLikeLogList.filter(v => v !== reviewId));
                setStoreInfo({
                    ...storeInfo,
                    reviews: storeInfo.reviews.map(v => v.reviewId === reviewId ? {
                        ...v,
                        likeCnt: v.likeCnt - 1
                    } : v)
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickLikeStore = async () => {
        try {
            const savedYn = await storeApi.likeStore(storeId);
            setStoreLikeLog(savedYn);

            if (savedYn) {
                setStoreInfo({
                    ...storeInfo,
                    store: {
                        ...storeInfo.store,
                        likeCnt: storeInfo.store.likeCnt + 1
                    }
                });
            } else {
                setStoreInfo({
                    ...storeInfo,
                    store: {
                        ...storeInfo.store,
                        likeCnt: storeInfo.store.likeCnt - 1
                    }
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <div style={{ border: '1px solid black' }}>
                {storeInfo.previewImages.map(v => (
                    <p key={'preview' + v}>{v}</p>
                ))}
            </div>
            <div style={{ border: '1px solid black' }}>
                <p>
                    <button onClick={handlerClickLikeStore}>{!storeLikeLog ? '좋아요' : '좋아요 취소'}</button>
                </p>
                <p>{storeInfo.store.placeId}</p>
                <p>{storeInfo.store.storeName}</p>
                <p>{storeInfo.store.categoryName}</p>
                <p>{storeInfo.store.addressName}</p>
                <p>{storeInfo.store.roadAddressName}</p>
                <p>{storeInfo.store.likeCnt}</p>
                <button onClick={() => setWriteReview({ ...writeReview, writeYn: true, review: null })}>리뷰 작성</button>
                {writeReview.writeYn && (
                    <WriteReview placeId={storeInfo.store.placeId}
                                 x={storeInfo.store.x}
                                 y={storeInfo.store.y}
                                 onClickClose={() => setWriteReview({ ...writeReview, writeYn: false })}
                                 storeInfo={storeInfo}
                                 setStoreInfo={setStoreInfo}
                                 getReview={writeReview.review} />
                )}
            </div>
            <div style={{ border: '1px solid black' }}>
                {storeInfo.reviews.map(v => (
                    <div key={'review' + v.reviewId} style={{ border: '2px solid blue' }}>
                        {v.userId === id && (
                            <>
                                <button onClick={() => setWriteReview({
                                    ...writeReview,
                                    writeYn: true,
                                    review: v
                                })}>수정
                                </button>
                                <button onClick={() => handlerClickDeleteReview(v.reviewId)}>삭제</button>
                            </>
                        )}
                        <p>
                            <button onClick={() => handlerClickLikeReview(v.reviewId)}>
                                {!reviewLikeLogList.includes(v.reviewId) ? '공감' : '공감 취소'}
                            </button>
                        </p>
                        <p>{v.userId}</p>
                        <p>{v.reviewId}</p>
                        <p>{v.nickname}</p>
                        <p>{v.starScore}</p>
                        <p>{v.content}</p>
                        <p>{v.revisitYn ? '함' : '안함'}</p>
                        <p>{v.likeCnt}</p>
                        <div>
                            {v.images.map(image => (
                                <p key={'image' + image}>{image}</p>
                            ))}
                        </div>
                        <div>
                            {v.recommends.map(recommend => (
                                <span key={`recommend_${v.reviewId}_${recommend}`}>{recommend}, </span>
                            ))}
                        </div>
                        <p>{v.createDate}</p>
                    </div>
                ))}
                {!meta.isEnd && (
                    <button onClick={handlerClickMoreReview}>더보기</button>
                )}
            </div>
        </div>
    );
};

export default StoreReviews;