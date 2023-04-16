import { urlUtils } from "../utils/urlUtils";
import { API_PATH } from "../constants/path";
import { authAxios } from "./axios";

const reviewApi = {
    writeReview: async (placeId, param) => {
        const url = urlUtils.setPath(API_PATH.REVIEW.WRITE_REVIEW, { placeId });
        const { data } = await authAxios.post(url, param);

        return data;
    }
}

export default reviewApi;