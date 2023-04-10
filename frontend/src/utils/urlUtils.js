export const urlUtils = (url, params) => {
    for (const paramsKey in params) {
        url = url.replace(`:${paramsKey}`, params[paramsKey]);
    }

    return url;
}