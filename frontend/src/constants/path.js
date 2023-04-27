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
    },
    USER: {
        GET_MY_INFO: '/user/my'
    },
    EAT_TOGETHER: {
        WRITE: '/eatTogether/post/write',
        LIST: '/eatTogether/post/list',
        GET: '/eatTogether/post/:postId'
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
        GET_STORE: '/api/store/:storeId',
        LIKE_STORE: '/api/store/:storeId/like',
        GET_STORE_LIKE_LOG_OF_USER: '/api/store/:storeId/like',
        GET_REVIEW_IMAGE_LIST: '/api/store/:storeId/reviewImageList'
    },
    IMAGE: {
        UPLOAD: '/api/image/upload'
    },
    REVIEW: {
        WRITE_REVIEW: '/api/review/place/:placeId',
        GET_REVIEW_LIST: '/api/review/store/:storeId',
        MODIFY_REVIEW: '/api/review/:reviewId',
        DELETE_REVIEW: '/api/review/:reviewId',
        LIKE_REVIEW: '/api/review/:reviewId/like',
        GET_REVIEW_LIKE_LOG_LIST_OF_STORE: '/api/review/store/:storeId/like'
    },
    USER: {
        GET_MY_INFO: '/api/user/my',
        MODIFY_NICKNAME: '/api/user/my/nickname',
        MODIFY_PROFILE: '/api/user/my/profile'
    },
    EAT_TOGETHER: {
        WRITE_POST: '/api/eatTogether/post',
        GET_POST_LIST: '/api/eatTogether/post/list',
        GET_POST: '/api/eatTogether/post/:postId',
        WRITE_REPLY: '/api/eatTogether/post/:postId/reply'
    }
}

export { BROWSER_PATH, API_PATH };