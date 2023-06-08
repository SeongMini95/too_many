import { urlUtils } from "../utils/urlUtils";
import { API_PATH } from "../constants/path";
import { authAxios } from "./axios";

const reviewApi = {
    writeReview: async (placeId, param) => {
        const url = urlUtils.setPath(API_PATH.REVIEW.WRITE_REVIEW, { placeId });
        const { data } = await authAxios.post(url, param);

        return data;
    },
    getReviewList: async (storeId, moreId) => {
        let url = urlUtils.setPath(API_PATH.REVIEW.GET_REVIEW_LIST, { storeId });
        if (moreId) {
            url = urlUtils.setParam(url, { moreId });
        }
        const { data } = await authAxios.get(url);

        return data;
    },
    modifyReview: async (reviewId, param) => {
        const url = urlUtils.setPath(API_PATH.REVIEW.MODIFY_REVIEW, { reviewId });
        const { data } = await authAxios.put(url, param);

        return data;
    },
    deleteReview: async (reviewId) => {
        const url = urlUtils.setPath(API_PATH.REVIEW.DELETE_REVIEW, { reviewId });
        await authAxios.delete(url);
    },
    likeReview: async (reviewId) => {
        const url = urlUtils.setPath(API_PATH.REVIEW.LIKE_REVIEW, { reviewId });
        const { data } = await authAxios.post(url);

        return data;
    },
    getReview: async (reviewId) => {
        const url = urlUtils.setPath(API_PATH.REVIEW.GET_REVIEW, { reviewId });
        const { data } = await authAxios.get(url);

        return data;
    },
    getRefreshReviewList: async (storeId, lastId) => {
        let url = urlUtils.setPath(API_PATH.REVIEW.GET_REFRESH_REVIEW_LIST, { storeId });
        url = urlUtils.setParam(url, { lastId });

        const { data } = await authAxios.get(url);

        return data;
    }
}

export default reviewApi;