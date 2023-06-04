import React, { useEffect, useState } from 'react';
import style from "../../css/Main/Main.module.css";
import { Button, Col, Form, Row } from "react-bootstrap";
import { BROWSER_PATH } from "../../constants/path";
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import categoryApi from "../../api/category";
import storeApi from "../../api/store";
import { useInView } from "react-intersection-observer";
import { urlUtils } from "../../utils/urlUtils";
import { Link } from "react-router-dom";

const GetStoreList = () => {
    const { lastCode } = useRecoilValue(positionState);
    const { ref, inView } = useInView();

    const [categoryList, setCategoryList] = useState([[], [], [], []]);
    const [category, setCategory] = useState(['', '', '', '']);
    const [storeList, setStoreList] = useState([]);
    const [meta, setMeta] = useState({
        page: 0,
        isEnd: false
    });

    useEffect(() => {
        const getDepth1Category = async () => {
            const { categories } = await categoryApi.getCategoryList(null, 1);
            setCategoryList(
                categoryList.map((v, i) => i === 1 ? categories : [])
            );
        }

        getDepth1Category();
    }, []);

    useEffect(() => {
        if (lastCode) {
            handlerGetStoreList(1);
        }
    }, [lastCode]);

    useEffect(() => {
        if (inView && meta.page !== 0) {
            handlerGetStoreList(meta.page + 1);
        }
    }, [inView]);

    const handlerChangeCategory = async (e, depth) => {
        const categoryId = e.target.value;

        setCategory(
            category.map((v, i) => i === depth ? categoryId : i > depth ? '' : v)
        );

        if (categoryId && depth < 3) {
            const { categories } = await categoryApi.getCategoryList(categoryId, depth + 1);
            setCategoryList(
                categoryList.map((v, i) => i === depth + 1 ? categories : i > depth + 1 ? [] : v)
            );
        } else {
            setCategoryList(
                categoryList.map((v, i) => i > depth ? [] : v)
            );
        }
    }

    const handlerGetStoreList = async (page) => {
        try {
            const lastCategory = category[3] ? category[3] : category[2] ? category[2] : category[1];
            const getStoreList = await storeApi.getStoreList(lastCode, lastCategory, page);

            if (page <= 1) {
                setStoreList(getStoreList.stores);
            } else {
                setStoreList(storeList.concat(getStoreList.stores));
            }

            setMeta({
                ...meta,
                page: getStoreList.page,
                isEnd: getStoreList.isEnd
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div className={style.recommend}>
            <h1 className={style.recommend_title1}>추천 맛집</h1>
            <div style={{ marginLeft: '20px' }}>
                <div className={style.category}>카테고리</div>
                <Row className="mb-3">
                    <Form.Group as={Col}>
                        <Form.Select onChange={(e) => handlerChangeCategory(e, 1)} value={category[1]}>
                            <option value="">전체</option>
                            {categoryList[1].map(v => (
                                <option key={'category' + v.categoryId} value={v.categoryId}>{v.categoryName}</option>
                            ))}
                        </Form.Select>
                    </Form.Group>

                    <Form.Group as={Col}>
                        <Form.Select onChange={(e) => handlerChangeCategory(e, 2)} value={category[2]}>
                            <option value="">전체</option>
                            {categoryList[2].map(v => (
                                <option key={'category' + v.categoryId} value={v.categoryId}>{v.categoryName}</option>
                            ))}
                        </Form.Select>
                    </Form.Group>

                    <Form.Group as={Col}>
                        <Form.Select onChange={(e) => handlerChangeCategory(e, 3)} value={category[3]}>
                            <option value="">전체</option>
                            {categoryList[3].map(v => (
                                <option key={'category' + v.categoryId} value={v.categoryId}>{v.categoryName}</option>
                            ))}
                        </Form.Select>
                    </Form.Group>

                    <Form.Group as={Col}>
                        <Button variant="primary" type="button" onClick={() => handlerGetStoreList(1)}>검색</Button>
                    </Form.Group>
                </Row>
            </div>
            <ul>
                {storeList.map(v => (
                    <li key={'store' + v.storeId}>
                        <div>
                            <Link to={urlUtils.setPath(BROWSER_PATH.STORE.GET_STORE_REVIEWS, { storeId: v.storeId })}>
                                <span className={style.img}>
                                    {v.image && (
                                        <img src={v.image} alt="" />
                                    )}
                                </span>
                                <div className={style.cnt}>
                                    <em className={style.score}>★ {parseFloat(v.starScore).toFixed(1)}</em>
                                    <div className={style.box_tit}>
                                        <strong className={style.store_name}>{v.storeName}</strong>
                                    </div>
                                    <ul>
                                        <li>{v.regionName}</li>
                                    </ul>
                                    <p>{v.categoryName}</p>
                                    <ul id={style['recommend_state_ul']}>
                                        <li className={style.ico_st01}><p>{v.likeCnt}</p></li>
                                        <li className={style.ico_st02}><p>{v.reviewCnt}</p></li>
                                    </ul>
                                </div>
                            </Link>
                        </div>
                    </li>
                ))}
            </ul>
            {!meta.isEnd && (
                <a href={'#none'} className={style.more_btn} ref={ref} onClick={() => handlerGetStoreList(meta.page + 1)}>
                    <span>더보기</span>
                </a>
            )}
        </div>
    );
};

export default GetStoreList;