const BROWSER_PATH = {
    BASE: '/',
    AUTH: {
        LOGIN: '/auth/login',
        NAVER_LOGIN: '/auth/naver/login',
        KAKAO_LOGIN: '/auth/kakao/login',
    },
}

const API_PATH = {
    AUTH: {
        GET_NAVER_LOGIN_URI: '/api/auth/naver/login/uri',
        GET_KAKAO_LOGIN_URI: '/api/auth/kakao/login/uri',
        NAVER_LOGIN: '/api/auth/naver/login',
        KAKAO_LOGIN: '/api/auth/kakao/login',
        REISSUE: '/api/auth/reissue',
        CHECK: 'api/auth/check',
    },
}

export { BROWSER_PATH, API_PATH };