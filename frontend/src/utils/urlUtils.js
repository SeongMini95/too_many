export const urlUtils = {
    setPath: (url, params) => {
        for (const paramsKey in params) {
            url = url.replace(`:${paramsKey}`, params[paramsKey]);
        }

        return url;
    },
    setParam: (url, params) => {
        const split = url.split('?')
        const rtnUrl = split[0];

        let urlSearchParams;
        if (split.length === 2) {
            urlSearchParams = new URLSearchParams(split[1]);
        } else {
            urlSearchParams = new URLSearchParams();
        }

        for (const paramsKey in params) {
            urlSearchParams.append(paramsKey, params[paramsKey]);
        }

        return `${rtnUrl}?${urlSearchParams.toString()}`;
    }
}