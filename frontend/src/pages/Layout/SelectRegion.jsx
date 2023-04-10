import React, { useEffect, useState } from 'react';
import { useSetRecoilState } from "recoil";
import { positionState } from "../../recoils/position";
import regionApi from "../../api/region";

const SelectRegion = () => {
    const setPosition = useSetRecoilState(positionState);

    const [regionCodeList, setRegionCodeList] = useState([]);
    const [inputs, setInputs] = useState({
        region1: '',
        region2: '',
        region3: '',
    });
    const [isSetRegion, setIsSetRegion] = useState(false);
    const [currentPosition, setCurrentPosition] = useState({
        code: '',
        address: '',
    });

    useEffect(() => {
        const getRegionCodeList = async () => {
            const { regions } = await regionApi.getRegionCodeList();
            const code = regions[0].code;
            const name = regions[0].name;

            setRegionCodeList(regions);
            setInputs({
                ...inputs,
                region1: code
            });
            setCurrentPosition({
                ...currentPosition,
                code,
                name
            });
        }

        getRegionCodeList();
    }, []);

    const handlerChangeRegion = (e, depth) => {
        const { value } = e.target;

        setCurrentPosition({
            ...currentPosition,
            code: value
        });

        if (depth === 1) {
            setInputs({
                ...inputs,
                region1: value,
                region2: '',
                region3: ''
            });
        } else if (depth === 2) {
            setInputs({
                ...inputs,
                region2: value,
                region3: '',
            });
        } else {
            setInputs({
                ...inputs,
                region3: value
            });
        }
    }

    const handlerClickSelectRegion = async () => {
        const { code } = currentPosition;
        const { address, x, y } = await regionApi.getCoordOfRegionCode(code);
        setPosition({
            code,
            address,
            x,
            y
        });
    }

    const handlerClickSetCurrentPosition = () => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(async position => {
                const { longitude, latitude } = position.coords;
                const code = await regionApi.getRegionCodeOfCoord(longitude, latitude);
                setPosition({
                    code,
                    x: longitude,
                    y: latitude
                });
            });
        } else {
            console.log('실패');
        }
    }

    return (
        <div>
            <button onClick={() => setIsSetRegion(true)}>다른 지역 선택</button>
            <button onClick={handlerClickSetCurrentPosition}>현 위치로 설정</button>
            {isSetRegion && (
                <div>
                    <div>
                        <select onChange={(e) => handlerChangeRegion(e, 1)}>
                            {regionCodeList.map(v => (
                                <option key={v.code} value={v.code}>{v.name}</option>
                            ))}
                        </select>
                        <select onChange={(e) => handlerChangeRegion(e, 2)}>
                            <option value="">전체</option>
                            {regionCodeList.find(v => v.code === inputs.region1).children.map(v => (
                                <option key={v.code} value={v.code}>{v.name}</option>
                            ))}
                        </select>
                        <select onChange={(e) => handlerChangeRegion(e, 3)}>
                            {inputs.region2 && (
                                <option value="">전체</option>
                            )}
                            {inputs.region2 && regionCodeList.find(v => v.code === inputs.region1).children.find(v => v.code === inputs.region2).children.map(v => (
                                <option key={v.code} value={v.code}>{v.name}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <button onClick={handlerClickSelectRegion}>선택완료</button>
                        <button onClick={() => setIsSetRegion(false)}>취소</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SelectRegion;