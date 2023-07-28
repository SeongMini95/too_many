# 오늘 점심 메뉴, 오점메

## 🍚소개

오점메는 직장인들의 고민중 하나인 점심메뉴 선택을 위해 제작된 서비스 입니다.

좀 더 편리한 로그인을 위해 OAuth2로 네이버와 카카오 소셜 로그인을 사용하였고, Jwt를 사용하여 Access Token을 사용해 사용자에 접근하도록 하였고 Refresh Token을 통해 Access Token을 갱신해주었습니다.

카카오 Map Api를 사용하여 카카오맵에 등록되어 있는 매장으로 데이터가 이루어져있습니다.

매장 검색은 geolocation api를 사용하거나 주소 선택을 통해 현지 위치 기준으로 검색합니다.

프론트는 SPA를 경험해보고 싶어서 React를 사용했습니다.

오류의 발견과 해결을 위해서 테스트 코드를 작성했습니다.
실제로 구현하는 과정에서 잘못된 로직을 발견 하고 수정하였습니다.
소셜 로그인이나 외부 api를 테스트 하기 위해서 mock web server를 사용하였습니다.
테스트 커버리지를 최대한 높이기 위해 다양한 테스트 상황을 작성하도록 노력하였습니다.

테스트 환경에서는 H2를 사용하였는데 기존 환경은 MariaDB를 사용해 구현하였습니다. 하지만 H2와 MariaDB의 SQL문이 조금씩 다른 환경이 있어서 H2를 사용하지 말고 문법이 다른 부분만 MariaDB 환경을 사용할까 싶었지만 H2의 문법을 찾고 그 부분만 테스트 코드를 따로 작성하여 사용하였습니다. 만약에 H2에 없는 문법이라면 이렇게 MariaDB를 적용 시키는 것을 고민했습니다.

현재 대한민국에 있는 주소를 DB에 Insert 되어있습니다. 이 주소 부분은 변경이 그렇게 빈번하지 않기 때문에 Cache를 적용하였습니다. 이로 인해 DB의 접근 횟수를 줄였습니다.

자동화 배포를 위하여 Jenkins를 사용하였습니다.

Nginx를 사용하여 front와 api서버를 분리하였고 무중단 배포를 위하여 blue/green 으로 포트를 다르게 하여 Reverse Proxy를 사용하였습니다.


## 🛠️기술 스택

![skill](https://github.com/SeongMini95/o_jeom_me/assets/88890604/771e9bf1-6467-47f2-baad-01e5cb5b940c)

## 🏗️인프라

![infra](https://github.com/SeongMini95/o_jeom_me/assets/88890604/8c5ad7fe-4967-4578-8552-d58c2bea1906)

## 🔁CI/CD

![ci/cd](https://github.com/SeongMini95/o_jeom_me/assets/88890604/905fad18-a953-4324-99a7-1d4b7c983ddc)
