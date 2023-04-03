import { atom, selector } from "recoil";
import { tokenProvider } from "../utils/jwt";

const loginState = atom({
    key: 'loginState',
    default: false,
});

const accessTokenState = selector({
    key: 'accessTokenState',
    get: () => {
        return tokenProvider.getAccessToken();
    },
    set: ({ set }, accessToken) => {
        if (accessToken) {
            tokenProvider.setAccessToken(accessToken);
        } else {
            tokenProvider.removeAccessToken();
        }
    }
});

const refreshTokenState = selector({
    key: 'refreshTokenState',
    get: () => {
        return tokenProvider.getRefreshToken();
    },
    set: ({ set }, refreshToken) => {
        if (refreshToken) {
            tokenProvider.setRefreshToken(refreshToken);
        } else {
            tokenProvider.removeRefreshToken();
        }
    }
});

export { loginState, accessTokenState, refreshTokenState };