import { API_PATH } from "../constants/path";
import { authAxios } from "./axios";
import { urlUtils } from "../utils/urlUtils";

const eatTogetherApi = {
    writeEatTogetherPost: async (param) => {
        const url = API_PATH.EAT_TOGETHER.WRITE;
        const { data } = await authAxios.post(url, param);

        return data;
    },
    getEatTogetherPostList: async (regionCode, moreId) => {
        let url = urlUtils.setParam(API_PATH.EAT_TOGETHER.GET_POST_LIST, { regionCode });
        if (moreId != null) {
            url = urlUtils.setParam(url, { moreId });
        }

        const { data } = await authAxios.get(url);

        return data;
    },
    getEatTogetherPost: async (postId) => {
        const url = urlUtils.setPath(API_PATH.EAT_TOGETHER.GET_POST, { postId });
        const { data } = await authAxios.get(url);

        return data;
    }
}

export default eatTogetherApi;