import { authAxios } from "./axios";
import { API_PATH } from "../constants/path";

const imageApi = {
    upload: async (image) => {
        const formData = new FormData();
        formData.append('image', image);

        const { data } = await authAxios.post(API_PATH.IMAGE.UPLOAD, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });

        return data;
    }
}

export default imageApi;