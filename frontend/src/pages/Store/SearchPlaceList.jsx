import React, { useEffect, useRef, useState } from 'react';
import { useRecoilValue } from "recoil";
import { positionState } from "../../recoils/position";
import storeApi from "../../api/store";

const SearchPlaceList = () => {
    const { x, y } = useRecoilValue(positionState);
    const [placeListInfo, setPlaceListInfo] = useState({
        meta: {
            totalCount: 0,
            pageableCount: 0,
            isEnd: true
        },
        places: []
    });
    const [inputs, setInputs] = useState({
        query: '',
        page: 1
    });
    const placeObserver = useRef(null);
    const [lastPlace, setLastPlace] = useState(null);

    useEffect(() => {
        placeObserver.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting) {
                handlerClickSearchPlaceList()
            }
        }, { threshold: 0.5, rootMargin: '0px' });
    }, [placeListInfo]);

    useEffect(() => {
        const observer = placeObserver.current;
        if (lastPlace && !placeListInfo.meta.isEnd) {
            observer.observe(lastPlace)
        }

        return () => {
            if (lastPlace) {
                observer.unobserve(lastPlace);
            }
        }
    }, [placeListInfo.meta.isEnd]);

    const handlerChangeInputs = (e) => {
        const { name, value } = e.target;
        setInputs({
            ...inputs,
            [name]: value
        });
    }

    const handlerClickSearchPlaceList = async () => {
        try {
            const { query, page } = inputs;
            const param = {
                query,
                x,
                y,
                page
            };

            const { meta, places } = await storeApi.searchPlaceList(param);
            setPlaceListInfo({
                ...placeListInfo,
                meta,
                places: placeListInfo.places.concat(places)
            });
            setInputs({
                ...inputs,
                page: inputs.page + 1
            });
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <div>
                <input type="text" name="query" onChange={handlerChangeInputs} value={inputs.query} />
                <button onClick={handlerClickSearchPlaceList}>검색</button>
            </div>
            <div>
                {placeListInfo.places.map(v => (
                    <div key={v.placeId} style={{ border: '1px solid black' }}>
                        <p>storeId: {v.storeId}</p>
                        <p>placeId: {v.placeId}</p>
                        <p>placeName: {v.placeName}</p>
                        <p>categoryName: {v.categoryName}</p>
                        <p>phone: {v.phone}</p>
                        <p>addressName: {v.addressName}</p>
                        <p>roadAddressName: {v.roadAddressName}</p>
                        <p>x: {v.x}</p>
                        <p>y: {v.y}</p>
                        <p>likeCnt: {v.likeCnt}</p>
                        <p>reviewCnt: {v.reviewCnt}</p>
                        <button>이 매장 리뷰 작성</button>
                    </div>
                ))}
                <div ref={setLastPlace} />
            </div>
        </div>
    );
};

export default SearchPlaceList;