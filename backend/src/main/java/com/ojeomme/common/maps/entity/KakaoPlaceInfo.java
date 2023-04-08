package com.ojeomme.common.maps.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoPlaceInfo {

    private boolean isExist;
    private BasicInfo basicInfo;

    @NoArgsConstructor
    @Getter
    public static class BasicInfo {

        private Long cid;
        private Address address;
        private String placenamefull;
        private int wpointx;
        private int wpointy;

        @Builder
        public BasicInfo(Long cid, Address address, String placenamefull, int wpointx, int wpointy) {
            this.cid = cid;
            this.address = address;
            this.placenamefull = placenamefull;
            this.wpointx = wpointx;
            this.wpointy = wpointy;
        }

        @NoArgsConstructor
        @Getter
        public static class Address {

            private String addrbunho;
            private String addrdetail;
            private Newaddr newaddr;
            private Region region;

            @Builder
            public Address(String addrbunho, String addrdetail, Newaddr newaddr, Region region) {
                this.addrbunho = addrbunho;
                this.addrdetail = addrdetail;
                this.newaddr = newaddr;
                this.region = region;
            }

            @NoArgsConstructor
            @Getter
            public static class Newaddr {

                private String newaddrfull;

                @Builder
                public Newaddr(String newaddrfull) {
                    this.newaddrfull = newaddrfull;
                }
            }

            @NoArgsConstructor
            @Getter
            public static class Region {

                private String fullname;
                private String newaddrfullname;

                @Builder
                public Region(String fullname, String newaddrfullname) {
                    this.fullname = fullname;
                    this.newaddrfullname = newaddrfullname;
                }
            }
        }
    }

    @Builder
    public KakaoPlaceInfo(boolean isExist, BasicInfo basicInfo) {
        this.isExist = isExist;
        this.basicInfo = basicInfo;
    }

    public boolean getIsExist() {
        return isExist;
    }

    public Long getPlaceId() {
        return basicInfo.getCid();
    }

    public String getPlaceName() {
        return basicInfo.getPlacenamefull();
    }

    public String getRoadAddress() {
        return String.format("%s %s %s",
                basicInfo.getAddress().getRegion().getNewaddrfullname(),
                basicInfo.getAddress().getNewaddr().getNewaddrfull(),
                basicInfo.getAddress().getAddrdetail());
    }

    public String getAddress() {
        return String.format("%s %s", basicInfo.getAddress().getRegion().getFullname(), basicInfo.getAddress().getAddrbunho());
    }

    public int getX() {
        return basicInfo.getWpointx();
    }

    public int getY() {
        return basicInfo.getWpointy();
    }
}
