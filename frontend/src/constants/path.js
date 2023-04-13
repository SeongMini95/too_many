const BROWSER_PATH = {
    BASE: '/',
    AUTH: {
        LOGIN: '/auth/login',
        NAVER_LOGIN: '/auth/naver/login',
        KAKAO_LOGIN: '/auth/kakao/login',
    },
    STORE: {
        SEARCH_PLACE_LIST: '/store/searchPlaceList'
    }
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
    REGION: {
        GET_REGION_CODE_OF_COORD: '/api/region/regionOfCoord',
        GET_COORD_OF_REGION_CODE: '/api/region/coordOfRegion',
        GET_REGION_CODE_LIST: '/api/region/list'
    },
    STORE: {
        SEARCH_PLACE_LIST: '/api/store/searchPlaceList'
    }
}

export { BROWSER_PATH, API_PATH };