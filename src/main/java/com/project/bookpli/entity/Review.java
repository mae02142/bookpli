package com.project.bookpli.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 외부에서 직접 객체 생성 막음
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private Long userId;
    private String isbn13;
    private String reviewContent;
    private Integer rating;

    // 리뷰 수정을 위한 메서드 
    public void updateReview (String reviewContent, Integer rating) {
        this.reviewContent = reviewContent;
        this.rating = rating;
    }

}
