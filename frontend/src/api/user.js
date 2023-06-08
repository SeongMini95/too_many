import { API_PATH } from "../constants/path";
import { authAxios } from "./axios";

const userApi = {
    getMyInfo: async () => {
        const url = API_PATH.USER.GET_MY_INFO;
        const { data } = await authAxios.get(url);

        return data;
    },
    modifyNickname: async (nickname) => {
        const url = API_PATH.USER.MODIFY_NICKNAME;
        await authAxios.put(url, { nickname });
    },
    modifyProfile: async (profile) => {
        const url = API_PATH.USER.MODIFY_PROFILE;
        const { data } = await authAxios.put(url, { profile });

        return data;
    },
    modifyMyInfo: async (param) => {
        const url = API_PATH.USER.MODIFY_MY_INFO;
        const { data } = await authAxios.put(url, param);

        return data;
    }
}

export default userApi;