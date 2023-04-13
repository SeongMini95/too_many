import { authAxios } from "./axios";
import { API_PATH } from "../constants/path";

const regionApi = {
    getRegionCodeOfCoord: async (x, y) => {
        const url = `${API_PATH.REGION.GET_REGION_CODE_OF_COORD}?x=${x}&y=${y}`;
        const { data } = await authAxios.get(url);

        return data;
    },
    getCoordOfRegionCode: async (code) => {
        const url = `${API_PATH.REGION.GET_COORD_OF_REGION_CODE}?code=${code}`;
        const { data } = await authAxios.get(url);

        return data;
    },
    getRegionCodeList: async () => {
        const url = API_PATH.REGION.GET_REGION_CODE_LIST;
        const { data } = await authAxios.get(url);

        return data;
    }
}

export default regionApi;