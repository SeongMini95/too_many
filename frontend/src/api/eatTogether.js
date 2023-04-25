import { API_PATH } from "../constants/path";
import { authAxios } from "./axios";

const eatTogetherApi = {
    writeEatTogetherPost: async (param) => {
        const url = API_PATH.EAT_TOGETHER.WRITE;
        const { data } = await authAxios.post(url, param);

        return data;
    }
}

export default eatTogetherApi;