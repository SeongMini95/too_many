import React, { useEffect, useRef, useState } from 'react';
import style from '../../css/Main/SelectRegion.module.css';
import regionApi from "../../api/region";
import { useRecoilState } from "recoil";
import { positionState } from "../../recoils/position";
import { useQuery } from "@tanstack/react-query";
import { API_PATH } from "../../constants/path";

const SelectRegion = ({ handlerClickClose }) => {
    const [{ codes }, setPosition] = useRecoilState(positionState);

    const [inputs, setInputs] = useState({
        region1: '',
        region2: '',
        region3: ''
    });
    const refSelectRegion = useRef(null);
    const refRegion1 = useRef(null);
    const refRegion2 = useRef(null);
    const refRegion3 = useRef(null);

    const { data, isSuccess } = useQuery([API_PATH.REGION.GET_REGION_CODE_LIST], async () => {
        const { regions } = await regionApi.getRegionCodeList();
        return regions;
    }, {
        refetchOnMount: false,
        staleTime: Infinity
    });

    useEffect(() => {
        if (data) {
            if (!codes.length) {
                setInputs({
                    ...inputs,
                    region1: data[0].code
                });
            } else {
                setInputs({
                    ...inputs,
                    region1: codes[0] ?? '',
                    region2: codes[1] ?? '',
                    region3: codes[2] ?? ''
                })
            }
        }
    }, [data]);

    useEffect(() => {
        if (refRegion1.current) {
            refRegion1.current.scrollIntoView({
                block: 'center'
            });
        }

        if (refRegion2.current) {
            refRegion2.current.scrollIntoView({
                block: 'center'
            });
        }

        if (refRegion3.current) {
            refRegion3.current.scrollIntoView({
                block: 'center'
            });
        }
    }, [inputs]);

    useEffect(() => {
        const handlerClickOutside = (e) => {
            if (refSelectRegion.current && !refSelectRegion.current.contains(e.target)) {
                handlerClickClose();
            }
        }

        document.addEventListener('mousedown', handlerClickOutside);

        return () => {
            document.removeEventListener('mousedown', handlerClickOutside);
        };
    }, [refSelectRegion]);

    const handlerClickRegion = (code, depth) => {
        if (depth === 1) {
            setInputs({
                ...inputs,
                region1: code,
                region2: '',
                region3: ''
            });
        } else if (depth === 2) {
            setInputs({
                ...inputs,
                region2: code,
                region3: '',
            });
        } else {
            setInputs({
                ...inputs,
                region3: code
            });
        }
    }

    const handlerClickConfirm = async () => {
        try {
            const codes = [];
            for (const inputsKey in inputs) {
                const code = inputs[inputsKey];
                if (code) {
                    codes.push(code);
                }
            }

            const { address, x, y } = await regionApi.getCoordOfRegionCode(codes[codes.length - 1]);
            setPosition({
                codes,
                address,
                x,
                y
            });

            handlerClickClose();
        } catch (e) {
            alert(e.response.data);
        }
    }

    return isSuccess && (
        <div className={style.location_modal}>
            <div className={style.location_modal_wrap} ref={refSelectRegion}>
                <div className={style.modal_box}>
                    <div className={style.modal_header}>
                        <span className={style.modal_title}>지역 선택</span>
                        <button className={style.modal_close} onClick={handlerClickClose}>
                            <img className={style.modal_close_img} src={`${process.env.PUBLIC_URL}/assets/image/modal_close.png`} alt="" />
                        </button>
                    </div>
                    <div className={style.modal_main}>
                        <div className={style.modal_main_header}>
                            <div className={style.modal_main_header_title}>광역시도</div>
                            <div className={style.modal_main_header_title}>시군구</div>
                            <div className={style.modal_main_header_title}>읍면동</div>
                        </div>
                        <div className={style.list_box}>
                            <ul className={style.list}>
                                {data.map(v => (
                                    <li key={v.code} className={style.item} ref={inputs.region1 === v.code ? refRegion1 : null}>
                                        <button className={inputs.region1 !== v.code ? style.item_button : [style.item_button, style.now].join(' ')} onClick={() => handlerClickRegion(v.code, 1)}>
                                            {v.name}
                                            {inputs.region1 === v.code && (
                                                <img className={style.item_button_img} src={`${process.env.PUBLIC_URL}/assets/image/region_arrow.png`} alt="" />
                                            )}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                            <ul className={style.list}>
                                <li className={style.item} ref={!inputs.region2 ? refRegion2 : null}>
                                    <button className={inputs.region2 ? style.item_button : [style.item_button, style.now].join(' ')} onClick={() => handlerClickRegion('', 2)}>
                                        전체
                                        {!inputs.region2 && (
                                            <img className={style.item_button_img} src={`${process.env.PUBLIC_URL}/assets/image/region_arrow.png`} alt="" />
                                        )}
                                    </button>
                                </li>
                                {inputs.region1 && data.find(v => v.code === inputs.region1).children.map(v => (
                                    <li key={v.code} className={style.item} ref={inputs.region2 === v.code ? refRegion2 : null}>
                                        <button className={inputs.region2 !== v.code ? style.item_button : [style.item_button, style.now].join(' ')} onClick={() => handlerClickRegion(v.code, 2)}>
                                            {v.name}
                                            {inputs.region2 === v.code && (
                                                <img className={style.item_button_img} src={`${process.env.PUBLIC_URL}/assets/image/region_arrow.png`} alt="" />
                                            )}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                            <ul className={style.list}>
                                {inputs.region2 && (
                                    <li className={style.item} ref={!inputs.region3 ? refRegion3 : null}>
                                        <button className={inputs.region3 ? style.item_button : [style.item_button, style.now].join(' ')} onClick={() => handlerClickRegion('', 3)}>
                                            전체
                                            {!inputs.region2 && (
                                                <img className={style.item_button_img} src={`${process.env.PUBLIC_URL}/assets/image/region_arrow.png`} alt="" />
                                            )}
                                        </button>
                                    </li>
                                )}
                                {inputs.region1 && inputs.region2 && data.find(v => v.code === inputs.region1).children.find(v => v.code === inputs.region2).children.map(v => (
                                    <li key={v.code} className={style.item} ref={inputs.region3 === v.code ? refRegion3 : null}>
                                        <button className={inputs.region3 !== v.code ? style.item_button : [style.item_button, style.now].join(' ')} onClick={() => handlerClickRegion(v.code, 3)}>
                                            {v.name}
                                            {inputs.region3 === v.code && (
                                                <img className={style.item_button_img} src={`${process.env.PUBLIC_URL}/assets/image/region_arrow.png`} alt="" />
                                            )}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                    <div className={style.modal_footer}>
                        <button className={style.button} onClick={handlerClickConfirm}>
                            <span className={style.span}>선택 완료</span>
                        </button>
                        <button className={[style.button, style.cancel].join(' ')} onClick={handlerClickClose}>
                            <span className={[style.span, style.cancel].join(' ')}>취소</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SelectRegion;