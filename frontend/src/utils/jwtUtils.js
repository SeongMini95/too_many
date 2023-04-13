const TOKEN = {
    ACCESS_TOKEN: 'accessToken',
    REFRESH_TOKEN: 'refreshToken',
};

export const tokenProvider = {
    getAccessToken: () => {
        return localStorage.getItem(TOKEN.ACCESS_TOKEN) ?? '';
    },
    setAccessToken: (accessToken) => {
        localStorage.setItem(TOKEN.ACCESS_TOKEN, accessToken);
    },
    removeAccessToken: () => {
        localStorage.removeItem(TOKEN.ACCESS_TOKEN);
    },
    getRefreshToken: () => {
        return localStorage.getItem(TOKEN.REFRESH_TOKEN) ?? '';
    },
    setRefreshToken: (refreshToken) => {
        localStorage.setItem(TOKEN.REFRESH_TOKEN, refreshToken);
    },
    removeRefreshToken: () => {
        localStorage.removeItem(TOKEN.REFRESH_TOKEN);
    }
};
