package com.ojeomme.domain.store;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.review.Review;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "stores")
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "kakao_place_id", nullable = false)
    private Long kakaoPlaceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_code", nullable = false)
    private RegionCode regionCode;

    @Column(name = "store_name", nullable = false, length = 45)
    private String storeName;

    @Column(name = "address_name", nullable = false, length = 100)
    private String addressName;

    @Column(name = "road_address_name", nullable = false, length = 100)
    private String roadAddressName;

    @Column(name = "x", nullable = false, length = 20)
    private String x;

    @Column(name = "y", nullable = false, length = 20)
    private String y;

    @Column(name = "like_cnt")
    private int likeCnt;

    @Column(name = "main_image_url", length = 2083)
    private String mainImageUrl;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Store(Long id, Long kakaoPlaceId, Category category, RegionCode regionCode, String storeName, String addressName, String roadAddressName, String x, String y, int likeCnt, String mainImageUrl) {
        this.id = id;
        this.kakaoPlaceId = kakaoPlaceId;
        this.category = category;
        this.regionCode = regionCode;
        this.storeName = storeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.x = x;
        this.y = y;
        this.likeCnt = likeCnt;
        this.mainImageUrl = mainImageUrl;
    }

    public void updateStoreInfo(Store store) {
        this.category = store.getCategory();
        this.regionCode = store.getRegionCode();
        this.storeName = store.getStoreName();
        this.addressName = store.getAddressName();
        this.roadAddressName = store.getRoadAddressName();
        this.x = store.getX();
        this.y = store.getY();
    }

    public void writeReview(Review review) {
        this.reviews.add(review);
    }

    public void like() {
        this.likeCnt += 1;
    }

    public void cancelLike() {
        this.likeCnt -= 1;
    }

    public void changeMainImage(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }
}