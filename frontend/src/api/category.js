import { urlUtils } from "../utils/urlUtils";
import { API_PATH } from "../constants/path";
import { defaultAxios } from "./axios";

const categoryApi = {
    getCategoryList: async (upCategory, depth) => {
        let url = urlUtils.setParam(API_PATH.CATEGORY.GET_CATEGORY_LIST, { depth });
        if (upCategory) {
            url = urlUtils.setParam(url, { upCategory });
        }
        const { data } = await defaultAxios.get(url);

        return data;
    }
}

export default categoryApi;