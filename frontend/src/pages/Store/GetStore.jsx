import React, { useEffect, useState } from 'react';
import { useParams } from "react-router-dom";
import style from '../../css/Store/GetStore.module.css';
import reviewImageApi from "../../api/reviewImage";
import storeApi from "../../api/store";

const GetStore = () => {
    const { storeId } = useParams();

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
        likeCnt: ''
    });
    const [isLike, setIsLike] = useState(false);

    useEffect(() => {
        const getPreviewImageList = async () => {
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
        }

        const getStore = async () => {
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
                likeCnt: store.likeCnt
            });
        }

        const getStoreLikeLogOfUser = async () => {
            const isLike = await storeApi.getStoreLikeLogOfUser(storeId);
            setIsLike(isLike);
        }

        getPreviewImageList();
        getStore();
        getStoreLikeLogOfUser();
    }, []);

    const handlerClickLikeStore = async (e) => {
        try {
            e.preventDefault();

            const isLike = await storeApi.likeStore(storeId);
            setIsLike(isLike);

            if (isLike) {
                setStore({
                    ...store,
                    likeCnt: store.likeCnt + 1
                });
            } else {
                setStore({
                    ...store,
                    likeCnt: store.likeCnt - 1
                });
            }
        } catch (e) {
            alert(e.response.data);
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
                                        <li key={'image' + i} className={i === 0 && previewImage.imageCnt >= 2 ? style.size_l : i === 1 && previewImage.imageCnt === 4 ? style.size_m : ''}>
                                            <a href={'#none'} className={style.link_photo} style={{ backgroundImage: `url('${v}')` }}>
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
                                        <span className={style.point}>59명의 평가</span>
                                        <strong className={style.lbl_review_point}>4.4점</strong>
                                        <span className={[style.ico_star, style.star_rate].join(' ')}>
                                            <span className={[style.ico_star, style.inner_star].join(' ')}></span>
                                        </span>
                                    </p>
                                </div>
                                <div className={style.favor_pic}>
                                    <a href={'#none'} className={!isLike ? style.favor : [style.favor, style.favor_on].join(' ')} onClick={handlerClickLikeStore}>
                                        <span>좋아요(<i>{store.likeCnt}</i>)</span>
                                    </a>
                                    <button className={style.appra}>
                                        <span>리뷰작성</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div className={style.review_list}>
                            <p className={style.review_title}>101건의 방문자 평가</p>
                            <div className={style.grade_info}>
                                <p>
                                    <strong className={style.review_list_point}>4.4점</strong>
                                    <span className={[style.ico_star, style.star_rate].join(' ')}>
                                            <span className={[style.ico_star, style.inner_star].join(' ')}></span>
                                    </span>
                                </p>
                                <div className={style.view_like_point}>
                                    <span className={[style.chip_like_point, style.chip_like_point_type1].join(' ')}>맛 11</span>
                                    <span className={[style.chip_like_point, style.chip_like_point_type2].join(' ')}>친절 11</span>
                                    <span className={[style.chip_like_point, style.chip_like_point_type3].join(' ')}>분위기 11</span>
                                    <span className={[style.chip_like_point, style.chip_like_point_type4].join(' ')}>가성비 11</span>
                                    <span className={[style.chip_like_point, style.chip_like_point_type5].join(' ')}>주차 11</span>
                                </div>
                            </div>
                            <div className={style.review_graph}>
                                <div style={{ position: 'absolute', width: '70px', height: '70px' }}>
                                    <div
                                        style={{
                                            borderRadius: '50%',
                                            background: "url('https://s3-ap-northeast-1.amazonaws.com/dc-user-profile-resized/profilephoto_A_20210509013019_160x160.jpg') no-repeat",
                                            backgroundSize: 'auto 70px',
                                            backgroundPosition: 'center',
                                            width: 'auto',
                                            height: '70px'
                                        }}></div>
                                </div>
                                <p className={style.person_grade}>
                                    <span className={style.person_btxt}>
                                        <strong>먹고주글테다</strong>
                                        (406곳 작성, 420개 공감받음)
                                    </span>
                                    <span className={style.star_date}>
                                        <span className={[style.ico_star, style.review_star_rate].join(' ')}>
                                            <span className={[style.ico_star, style.review_inner_star].join(' ')}></span>
                                        </span>
                                        <i className={style.date}>2023년 11월 12일</i>
                                    </span>
                                </p>
                                <p className={style.content_btxt}>여기맛이 사이드메뉴 카라카츠고기가떨어져서시킨건데 엄청육즙갇혀있고질긴부위도여리여리하게 맛있었어요 사이드를종류별로시켜보리라생각했어요
                                    저는 일요일 저녁 여섯시에 전화걸어보니 열팀기다리고있다고하더라구요 저는알죠 요리만드는데 한삼십분기다려야돼오 가게에는 아예붙여있어요 고객이들어올태부터 요리를시작한다고요 손님이많아서일수도있는데 요리하는데오래걸리니까 대기없을때가지않으면 무조건시간걸려요 스파게티는 그냥 그랬는데 사이드 맛집인듯 늘
                                    수제카라아게는 정말 맛있었어요 밥 먹고 다 못 먹어서 싸왔거든요 싸와서 식었는데도 맛있었어요 웨지감자는 안먹고그냥버렸어요 상온에 한네시간있고도 맛완전좋음</p>
                                <div className={style.review_image_wrap}>
                                    <ul className={style.review_image_photo_list}>
                                        <li>
                                            <a href={'#none'} className={style.box_photo}>
                                                <img className={style.img_thumb}
                                                     src='https://img1.kakaocdn.net/cthumb/local/C139x139/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flocal%2FkakaomapPhoto%2Freview%2F45f3604c404abf3545ea3e0239e9953a18046742%3Foriginal'
                                                     alt="" />
                                            </a>
                                        </li>
                                        <li>
                                            <a href={'#none'} className={style.box_photo}>
                                                <img className={style.img_thumb}
                                                     src='https://img1.kakaocdn.net/cthumb/local/C139x139/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flocal%2FkakaomapPhoto%2Freview%2F45f3604c404abf3545ea3e0239e9953a18046742%3Foriginal'
                                                     alt="" />
                                            </a>
                                        </li>
                                        <li>
                                            <a href={'#none'} className={style.box_photo}>
                                                <img className={style.img_thumb}
                                                     src='https://img1.kakaocdn.net/cthumb/local/C139x139/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flocal%2FkakaomapPhoto%2Freview%2F45f3604c404abf3545ea3e0239e9953a18046742%3Foriginal'
                                                     alt="" />
                                            </a>
                                        </li>
                                        <li>
                                            <a href={'#none'} className={style.box_photo}>
                                                <img className={style.img_thumb}
                                                     src='https://img1.kakaocdn.net/cthumb/local/C139x139/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flocal%2FkakaomapPhoto%2Freview%2F45f3604c404abf3545ea3e0239e9953a18046742%3Foriginal'
                                                     alt="" />
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                                <div className={style.review_like_btn}>공감(2)</div>
                            </div>
                        </div>
                        <a href={'#none'} className={style.more_btn}>
                            <span>더보기</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default GetStore;