package com.ojeomme.domain.reviewimage.repository;

import com.ojeomme.domain.reviewimage.ReviewImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    @Query(value = "select ri.imageUrl from Review r inner join ReviewImage ri on r.id = ri.review.id where r.store.id = :storeId order by r.id desc, ri.id asc")
    List<String> getPreviewImageList(Long storeId, Pageable pageable);
}