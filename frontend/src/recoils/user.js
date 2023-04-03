import { atom, selector } from "recoil";

const userState = atom({
    key: 'userState',
    default: {
        nickname: '',
        profile: '',
    }
});

const userInfoState = selector({
    key: 'userInfoState',
    get: ({ get }) => {
        return get(userState);
    },
    set: ({ set }, user) => {
        set(userState, user);
    }
});

export { userInfoState };