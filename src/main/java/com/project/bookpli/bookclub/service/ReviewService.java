package com.project.bookpli.bookclub.service;

import com.project.bookpli.bookclub.dto.ReviewDTO;
import com.project.bookpli.entity.Review;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ReviewService {

    // 유저의 리뷰 전체 조회
    List<ReviewDTO> readByUserId (Long userId);
    // 특정 도서의 리뷰 전체 조회
    List<ReviewDTO> readByIsbn(String isbn13);
    // 리뷰 등록 & 수정
    ReviewDTO save(ReviewDTO review);
    // 리뷰 삭제
    boolean remove(Long reviewId);
}
