import React, { useEffect, useState } from 'react';
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import { useInView } from "react-intersection-observer";
import style from '../../css/Store/SearchPlaceList.module.css';
import { Button, Form } from "react-bootstrap";
import storeApi from "../../api/store";
import WriteReview from "../Review/WriteReview";

const SearchPlaceList = () => {
    const { x, y } = useRecoilValue(positionState);
    const { ref, inView } = useInView();

    const [placeList, setPlaceList] = useState([]);
    const [meta, setMeta] = useState({
        page: 1,
        isEnd: true
    });
    const [query, setQuery] = useState('');
    const [isWriteReview, setIsWriteReview] = useState(false);
    const [placeInfo, setPlaceInfo] = useState({
        placeId: 0,
        placeName: '',
        x: '',
        y: ''
    });

    useEffect(() => {
        if (inView) {
            handlerSearchPlace(meta.page + 1);
        }
    }, [inView]);

    const handlerSearchPlace = async (page) => {
        try {
            const param = {
                query,
                x,
                y,
                page
            };

            const { meta, places } = await storeApi.searchPlaceList(param);
            if (page === 1) {
                setPlaceList(places);
            } else {
                setPlaceList(placeList.concat(places));
            }

            setMeta({
                ...meta,
                page,
                isEnd: meta.isEnd
            });
        } catch (e) {
            console.log(e);
            alert(e.response.data);
        }
    }

    const handlerClickWriteReview = (placeId, placeName, x, y) => {
        setIsWriteReview(true);
        setPlaceInfo({
            ...placeInfo,
            placeId,
            placeName,
            x,
            y
        });
    }

    const handlerClickCloseReview = () => {
        if (window.confirm('리뷰 작성을 취소하시겠습니까?')) {
            setIsWriteReview(false);
        }
    }

    return (
        <>
            <main className={style.main}>
                <div className={style.place}>
                    <h1 className={style.place_title1}>매장 검색</h1>
                    <div className={style.place_search}>
                        <Form.Control style={{ width: '350px' }} onChange={(e) => setQuery(e.target.value)} value={query} />
                        <Button variant="primary" style={{ width: '100px' }} onClick={() => handlerSearchPlace(1)}>검색</Button>
                    </div>
                    <ul>
                        {placeList.map(v => (
                            <li key={'place' + v.placeId}>
                            <span className={style.img}>
                                {v.image && (
                                    <img src={v.image} alt="" />
                                )}
                            </span>
                                <div className={style.cnt}>
                                    <div className={style.box_tit}>
                                        <strong className={style.store_name} title={v.placeName}>{v.placeName}</strong>
                                    </div>
                                    <p>{v.categoryName}</p>
                                    <p id={style['road_address']}>{v.roadAddressName}</p>
                                    <p id={style['address']}>{v.addressName}</p>
                                    <p className={style.phone}>{v.phone ? v.phone : '-'}</p>
                                    <div className={style.btn_group}>
                                        <Button variant={"primary"} onClick={() => handlerClickWriteReview(v.placeId, v.placeName, v.x, v.y)}>리뷰 작성</Button>
                                    </div>
                                </div>
                            </li>
                        ))}
                    </ul>
                    {!meta.isEnd && (
                        <a href={'#none'} className={style.more_btn} ref={ref} onClick={(e) => handlerSearchPlace(e, meta.page + 1)}>
                            <span>더보기</span>
                        </a>
                    )}
                </div>
            </main>
            {isWriteReview && (
                <WriteReview onClickClose={handlerClickCloseReview}
                             placeInfo={placeInfo}
                />
            )}
        </>
    );
};

export default SearchPlaceList;