import { authAxios } from "./axios";
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
    }
}

export default storeApi;