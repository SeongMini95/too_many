import { atom, selector } from "recoil";
import { positionProvider } from "../utils/positionUtils";

const coordState = atom({
    key: 'coordState',
    default: {
        codes: ['1100000000'],
        address: '서울',
        x: '126.978652258309',
        y: '37.566826004661'
    }
});

const positionState = selector({
    key: 'positionState',
    get: ({ get }) => {
        return get(coordState);
    },
    set: ({ set, reset }, position) => {
        if (position) {
            set(coordState, position);
            positionProvider.setPosition(JSON.stringify(position));
        } else {
            reset(coordState);
            positionProvider.removePosition();
        }
    }
});

export { positionState };