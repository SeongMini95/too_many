import { atom, selector } from "recoil";

const userState = atom({
    key: 'userState',
    default: {
        id: '',
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