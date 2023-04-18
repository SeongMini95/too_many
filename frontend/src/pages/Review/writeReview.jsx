import React, { useRef, useState } from 'react';
import imageApi from "../../api/image";
import reviewApi from "../../api/review";
import { useNavigate } from "react-router-dom";
import { urlUtils } from "../../utils/urlUtils";
import { BROWSER_PATH } from "../../constants/path";

const WriteReview = ({ placeId, x, y, onClickClose, storeInfo, setStoreInfo }) => {
    const navigate = useNavigate();
    const [inputs, setInputs] = useState({
        starScore: 0,
        content: '',
        revisitYn: false,
        images: [],
        recommends: [],
        x: x,
        y: y
    });
    const [star, setStar] = useState([false, false, false, false, false]);
    const refImage = useRef(null);

    const handlerChangeInputs = (e) => {
        const { name, value } = e.target;
        setInputs({
            ...inputs,
            [name]: value
        });
    }

    const handlerClickStar = (index) => {
        const star = [false, false, false, false, false];
        for (let i = 0; i <= index; i++) {
            star[i] = true;
        }
        setStar(star);
        setInputs({
            ...inputs,
            starScore: index + 1
        });
    }

    const handlerChangeRevisit = (e) => {
        const { checked } = e.target;
        setInputs({
            ...inputs,
            revisitYn: checked
        });
    }

    const handlerChangeRecommend = (e) => {
        const { checked, value } = e.target;
        if (checked) {
            setInputs({
                ...inputs,
                recommends: [
                    ...inputs.recommends,
                    value
                ]
            });
        } else {
            setInputs({
                ...inputs,
                recommends: inputs.recommends.filter(v => v !== value)
            });
        }
    }

    const handlerChangeImage = async () => {
        try {
            const image = refImage.current.files[0];
            if (image) {
                const url = await imageApi.upload(image);
                setInputs({
                    ...inputs,
                    images: [...inputs.images, url]
                });
            }
        } catch (e) {
            alert(e.response.data);
        }
    }

    const handlerClickWriteReview = async () => {
        try {
            const { storeId, review } = await reviewApi.writeReview(placeId, inputs);

            if (storeInfo) {
                setStoreInfo({
                    ...storeInfo,
                    reviews: [review].concat(storeInfo.reviews)
                });
            } else {
                const url = urlUtils.setPath(BROWSER_PATH.STORE.GET_STORE_REVIEWS, { storeId });
                navigate(url, { replace: true });
            }
            onClickClose();
        } catch (e) {
            alert(e.response.data);
        }
    }

    return (
        <div>
            <div>
                <button onClick={onClickClose}>닫기</button>
            </div>
            <div style={{ display: 'flex' }}>
                <Star onClick={() => handlerClickStar(0)} style={star[0] ? { fill: '#ffc107' } : { fill: '#eeeeee' }}></Star>
                <Star onClick={() => handlerClickStar(1)} style={star[1] ? { fill: '#ffc107' } : { fill: '#eeeeee' }}></Star>
                <Star onClick={() => handlerClickStar(2)} style={star[2] ? { fill: '#ffc107' } : { fill: '#eeeeee' }}></Star>
                <Star onClick={() => handlerClickStar(3)} style={star[3] ? { fill: '#ffc107' } : { fill: '#eeeeee' }}></Star>
                <Star onClick={() => handlerClickStar(4)} style={star[4] ? { fill: '#ffc107' } : { fill: '#eeeeee' }}></Star>
            </div>
            <div>
                재방문 의사<input type="checkbox" onChange={handlerChangeRevisit} checked={inputs.revisitYn} />
            </div>
            <div>
                <textarea name="content" onChange={handlerChangeInputs} value={inputs.content}></textarea>
            </div>
            <div>
                맛<input type="checkbox" onChange={handlerChangeRecommend} value="1" checked={inputs.recommends.includes('1')} />
                가성비<input type="checkbox" onChange={handlerChangeRecommend} value="2" checked={inputs.recommends.includes('2')} />
                친절<input type="checkbox" onChange={handlerChangeRecommend} value="3" checked={inputs.recommends.includes('3')} />
                분위기<input type="checkbox" onChange={handlerChangeRecommend} value="4" checked={inputs.recommends.includes('4')} />
                주차<input type="checkbox" onChange={handlerChangeRecommend} value="5" checked={inputs.recommends.includes('5')} />
            </div>
            <div>
                <input type="file" accept="image/*" ref={refImage} />
                <button onClick={handlerChangeImage}>변경하기</button>
            </div>
            <div>
                <button onClick={handlerClickWriteReview}>리뷰 작성</button>
            </div>
        </div>
    );
};

const Star = ({ style, onClick }) => {
    return (
        <div style={style} onClick={onClick}>
            <svg
                viewBox="0 -10 511.98685 511"
                xmlns="http://www.w3.org/2000/svg"
                width="100px"
                height="100px"
            >
                <path
                    d="m510.652344 185.902344c-3.351563-10.367188-12.546875-17.730469-23.425782-18.710938l-147.773437-13.417968-58.433594-136.769532c-4.308593-10.023437-14.121093-16.511718-25.023437-16.511718s-20.714844 6.488281-25.023438 16.535156l-58.433594 136.746094-147.796874 13.417968c-10.859376 1.003906-20.03125 8.34375-23.402344 18.710938-3.371094 10.367187-.257813 21.738281 7.957031 28.90625l111.699219 97.960937-32.9375 145.089844c-2.410156 10.667969 1.730468 21.695313 10.582031 28.09375 4.757813 3.4375 10.324219 5.1875 15.9375 5.1875 4.839844 0 9.640625-1.304687 13.949219-3.882813l127.46875-76.183593 127.421875 76.183593c9.324219 5.609376 21.078125 5.097657 29.910156-1.304687 8.855469-6.417969 12.992187-17.449219 10.582031-28.09375l-32.9375-145.089844 111.699219-97.941406c8.214844-7.1875 11.351563-18.539063 7.980469-28.925781zm0 0" />
            </svg>
        </div>
    )
}

export default WriteReview;