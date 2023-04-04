package com.toomany.domain.regioncode;

import com.toomany.domain.BaseTimeEntity;
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
@Table(name = "region_code")
public class RegionCode extends BaseTimeEntity {

    @Id
    @Column(name = "code", nullable = false, length = 10)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_code")
    private RegionCode upCode;

    @Column(name = "region_depth", nullable = false)
    private int regionDepth;

    @Column(name = "region_name", nullable = false, length = 10)
    private String regionName;

    @OneToMany(mappedBy = "upCode")
    private List<RegionCode> regionCodes = new ArrayList<>();

    @Builder
    public RegionCode(String id, RegionCode upCode, int regionDepth, String regionName) {
        this.id = id;
        this.upCode = upCode;
        this.regionDepth = regionDepth;
        this.regionName = regionName;
    }
}