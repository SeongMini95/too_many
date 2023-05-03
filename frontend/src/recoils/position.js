import { atom, selector } from "recoil";
import { positionProvider } from "../utils/positionUtils";

const coordState = atom({
    key: 'coordState',
    default: {
        codes: [],
        address: '',
        x: '',
        y: ''
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