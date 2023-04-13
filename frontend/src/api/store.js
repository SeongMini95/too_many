import { authAxios } from "./axios";
import { API_PATH } from "../constants/path";
import { urlUtils } from "../utils/urlUtils";

const storeApi = {
    searchPlaceList: async (param) => {
        const url = urlUtils.setParam(API_PATH.STORE.SEARCH_PLACE_LIST, param);
        const { data } = await authAxios.get(url);

        return data;
    }
}

export default storeApi;