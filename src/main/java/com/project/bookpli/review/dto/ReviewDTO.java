package com.project.bookpli.review.dto;

import com.project.bookpli.entity.Review;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Long userId;
    private String isbn13;
    private String reviewContent;
    private Integer rating;

    private String title;
    private String cover;

    private String userNickname;
    private String profilePath;

    //리뷰조회 생성자
    public ReviewDTO(Long reviewId, Long userId, String isbn13, String reviewContent, Integer rating, String userNickname, String profilePath) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.isbn13 = isbn13;
        this.reviewContent = reviewContent;
        this.rating = rating;
        this.userNickname = userNickname;
        this.profilePath = profilePath;
    }
    
    //dto ->  엔티티  변환
    public Review toEntity() {
        return Review.builder()
                .reviewId(this.reviewId)
                .userId(this.userId)
                .isbn13(this.isbn13)
                .reviewContent(this.reviewContent)
                .rating(this.rating)
                .build();
    }

    // 엔티티를 DTO로 변환하는 메서드
    public static ReviewDTO fromEntity(Review review) {
        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .isbn13(review.getIsbn13())
                .reviewContent(review.getReviewContent())
                .rating(review.getRating())
                .build();
    }

    // 엔티티를 DTO로 변환하는 메서드
    public static ReviewDTO fromEntityForBook(Review review, String cover, String title) {
        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .isbn13(review.getIsbn13())
                .reviewContent(review.getReviewContent())
                .rating(review.getRating())
                .cover(cover)
                .title(title)
                .build();
    }


    // 엔티티를 DTO로 변환하는 메서드
    public static ReviewDTO fromEntityForList(Review review, String userNickname, String profilePath) {
        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .isbn13(review.getIsbn13())
                .reviewContent(review.getReviewContent())
                .rating(review.getRating())
                .userNickname(userNickname)
                .profilePath(profilePath)
                .build();
    }
}

