package com.project.bookpli.bookclub.dto;

import com.project.bookpli.entity.Review;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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


    //dto  엔티티  변환
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

    // 엔티티 DTO 리스트로 변환하는 메서드
    public  static List<ReviewDTO> fromEntityList (List<Review> reviews){
       List<ReviewDTO> list = new ArrayList<>();

       for(Review review : reviews) {
            list.add(fromEntity(review));
       }
       return list;
    }
}
