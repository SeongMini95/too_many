const BROWSER_PATH = {
    BASE: '/',
    AUTH: {
        LOGIN: '/auth/login',
        NAVER_LOGIN: '/auth/naver/login',
        KAKAO_LOGIN: '/auth/kakao/login',
    },
    STORE: {
        SEARCH_PLACE_LIST: '/store/searchPlaceList',
        GET_STORE_REVIEWS: '/store/:storeId'
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
        SEARCH_PLACE_LIST: '/api/store/searchPlaceList',
        GET_STORE: '/api/store/:storeId'
    },
    IMAGE: {
        UPLOAD: '/api/image/upload'
    },
    REVIEW: {
        WRITE_REVIEW: '/api/review/:placeId',
        GET_REVIEW_LIST: '/api/review/:storeId',
        MODIFY_REVIEW: '/api/review/:reviewId'
    }
}

export { BROWSER_PATH, API_PATH };