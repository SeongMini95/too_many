import { API_PATH, BROWSER_PATH } from "../constants/path";
import { authAxios, defaultAxios } from "./axios";

const authApi = {
    getNaverLoginUri: async () => {
        const redirectUri = `${window.location.origin}${BROWSER_PATH.AUTH.NAVER_LOGIN}`;
        const { data } = await defaultAxios.get(`${API_PATH.AUTH.GET_NAVER_LOGIN_URI}?redirectUri=${redirectUri}`);

        return data;
    },
    getKakaoLoginUri: async () => {
        const redirectUri = `${window.location.origin}${BROWSER_PATH.AUTH.KAKAO_LOGIN}`;
        const { data } = await defaultAxios.get(`${API_PATH.AUTH.GET_KAKAO_LOGIN_URI}?redirectUri=${redirectUri}`);

        return data;
    },
    loginNaver: async (code) => {
        const redirectUri = `${window.location.origin}${BROWSER_PATH.AUTH.NAVER_LOGIN}`;
        const { data } = await defaultAxios.get(`${API_PATH.AUTH.NAVER_LOGIN}?redirectUri=${redirectUri}&code=${code}`);

        return data;
    },
    loginKakao: async (code) => {
        const redirectUri = `${window.location.origin}${BROWSER_PATH.AUTH.KAKAO_LOGIN}`;
        const { data } = await defaultAxios.get(`${API_PATH.AUTH.KAKAO_LOGIN}?redirectUri=${redirectUri}&code=${code}`);

        return data;
    },
    check: async () => {
        const { data } = await authAxios.get(API_PATH.AUTH.CHECK);
        return data;
    },
    reissue: async (refreshToken) => {
        const { data } = await authAxios.post(API_PATH.AUTH.REISSUE, {
            refreshToken: refreshToken
        });

        return data;
    }
};

export default authApi;