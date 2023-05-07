import { authAxios, defaultAxios } from "./axios";
import { API_PATH } from "../constants/path";
import { urlUtils } from "../utils/urlUtils";

const storeApi = {
    searchPlaceList: async (param) => {
        const url = urlUtils.setParam(API_PATH.STORE.SEARCH_PLACE_LIST, param);
        const { data } = await authAxios.get(url);

        return data;
    },
    getStore: async (storeId) => {
        const url = urlUtils.setPath(API_PATH.STORE.GET_STORE, { storeId });
        const { data } = await authAxios.get(url);

        return data;
    },
    likeStore: async (storeId) => {
        const url = urlUtils.setPath(API_PATH.STORE.LIKE_STORE, { storeId });
        const { data } = await authAxios.post(url);

        return data;
    },
    getStoreLikeLogOfUser: async (storeId) => {
        const url = urlUtils.setPath(API_PATH.STORE.GET_STORE_LIKE_LOG_OF_USER, { storeId });
        const { data } = await authAxios.get(url);

        return data;
    },
    getReviewImageList: async (storeId, reviewImageId) => {
        let url = urlUtils.setPath(API_PATH.STORE.GET_REVIEW_IMAGE_LIST, { storeId });
        if (reviewImageId) {
            url = urlUtils.setParam(url, { reviewImageId });
        }
        const { data } = await authAxios.get(url);

        return data;
    },
    getRealTimeStoreRanking: async (regionCode) => {
        const url = urlUtils.setParam(API_PATH.STORE.GET_REAL_TIME_STORE_RANKING, { regionCode });
        const { data } = await defaultAxios.get(url);

        return data;
    },
    getStoreList: async (regionCode, category, page) => {
        let url = urlUtils.setParam(API_PATH.STORE.GET_STORE_LIST, { regionCode, category });
        if (page) {
            url = urlUtils.setParam(url, { page });
        }
        const { data } = await defaultAxios.get(url);

        return data;
    }
}

export default storeApi;