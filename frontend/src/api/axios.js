import axios from "axios";
import { tokenProvider } from "../utils/jwtUtils";
import { API_PATH } from "../constants/path";
import authApi from "./auth";

const defaultAxios = axios.create({
    baseURL: process.env.REACT_APP_BASE_URL
});

const authAxios = axios.create({
    baseURL: process.env.REACT_APP_BASE_URL
});

authAxios.interceptors.request.use(
    (config) => {
        const accessToken = tokenProvider.getAccessToken();
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

let lock = false;
let subscribers = [];

const addSubscriber = (callback) => {
    subscribers.push(callback);
};

const onTokenReissued = (accessToken) => {
    subscribers.forEach((callback) => callback(accessToken));
};

const reissuedToken = async () => {
    try {
        const savedAccessToken = tokenProvider.getAccessToken();
        if (savedAccessToken) {
            const { accessToken, refreshToken } = await authApi.reissue(tokenProvider.getRefreshToken());

            lock = false;

            onTokenReissued(accessToken);
            subscribers = [];

            tokenProvider.setAccessToken(accessToken);
            tokenProvider.setRefreshToken(refreshToken);

            return accessToken;
        } else {
            return '';
        }
    } catch (e) {
        lock = false;
        subscribers = [];
        tokenProvider.removeAccessToken();
        tokenProvider.removeRefreshToken();
    }
};

authAxios.interceptors.response.use(
    async (response) => {
        const { data, config } = response;
        if (config.url === API_PATH.AUTH.CHECK && !data.result) {
            const originalRequest = config;

            if (lock) {
                return new Promise((resolve) => {
                    addSubscriber((accessToken) => {
                        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                        resolve(authAxios(originalRequest));
                    });
                });
            }

            lock = true;
            const accessToken = await reissuedToken();
            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
                return authAxios(config);
            }
        }

        return response;
    },
    async (error) => {
        const {
            config,
            response: { status },
        } = error;
        const originalRequest = config;

        if (config.url === API_PATH.AUTH.REISSUE || status !== 401) {
            return Promise.reject(error);
        }

        if (lock) {
            return new Promise((resolve) => {
                addSubscriber((accessToken) => {
                    originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                    resolve(authAxios(originalRequest));
                });
            });
        }

        lock = true;
        const accessToken = await reissuedToken();
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
            return authAxios(config);
        }

        return Promise.reject(error);
    }
);

export { defaultAxios, authAxios };