export const urlUtils = {
    setPath: (url, params) => {
        for (const paramsKey in params) {
            url = url.replace(`:${paramsKey}`, params[paramsKey]);
        }

        return url;
    },
    setParam: (url, params) => {
        const urlSearchParams = new URLSearchParams();
        for (const paramsKey in params) {
            urlSearchParams.append(paramsKey, params[paramsKey]);
        }

        return `${url}?${urlSearchParams.toString()}`;
    }
}