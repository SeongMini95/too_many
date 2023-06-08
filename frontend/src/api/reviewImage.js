import { urlUtils } from "../utils/urlUtils";
import { API_PATH } from "../constants/path";
import { authAxios } from "./axios";

const reviewImageApi = {
    getPreviewImageList: async (storeId) => {
        const url = urlUtils.setPath(API_PATH.REVIEW_IMAGE.GET_PREVIEW_IMAGE_LIST, { storeId });
        const { data } = await authAxios.get(url);

        return data;
    },
    getReviewImageList: async (storeId, moreId) => {
        let url = urlUtils.setPath(API_PATH.REVIEW_IMAGE.GET_REVIEW_IMAGE_LIST, { storeId });
        if (moreId) {
            url = urlUtils.setParam(url, { moreId });
        }
        const { data } = await authAxios.get(url);

        return data;
    },
};

export default reviewImageApi;