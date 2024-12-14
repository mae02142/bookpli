package com.project.bookpli.review.service;

import com.project.bookpli.review.dto.ReviewDTO;
import com.project.bookpli.review.repository.ReviewRepository;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository repository;


    // 해당 유저가 작성한 리뷰 전체 조회
    public  List<ReviewDTO> readByUserId (Long userId) {
        List<Object[]> list;

        try {
            list = repository.findByUserId(userId);
            if (list.isEmpty()) {
                System.out.println("서비스단 : 리스트가 비어있습니다. userId :" + userId);
                return Collections.emptyList();
            }
        }catch (Exception e) {
            System.out.println("서비스단에서 오류 발생 : " + e.getMessage());
            return Collections.emptyList();
        }
        return list.stream()
                .map(row -> {
                    Review review = (Review) row[0];
                    String cover = (String) row[1];
                    String title = (String) row[2];
                return ReviewDTO.fromEntityForBook(review,cover,title);
                })
                .collect(Collectors.toList());
    }

    // 해당 도서에 대한 리뷰 전체 조회
    public List<ReviewDTO> readByIsbn (String isbn13){
        try {
            // 이미 repository.findByIsbn은 List<ReviewDTO>를 반환
            List<ReviewDTO> list = repository.findByIsbn(isbn13);

            if ( list.isEmpty()) {
                System.out.println("서비스단 : 리스트가 비어있습니다. isbn : " + isbn13);
                return Collections.emptyList();
            }

            return list; // 직접 반환
        } catch (Exception e) {
            System.out.println("서비스단에서 오류 발생 : " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 리뷰 수정
    @Transactional
    public ReviewDTO update(ReviewDTO reviewDTO) {
        try {
            Review old = reviewDTO.toEntity();
            repository.save(old);
            Review newOne = repository.findById(reviewDTO.getReviewId())
                    .orElseThrow(()-> new NoSuchElementException("찾는 데이터가 없습니다."));

        // 변경된 리뷰를 DTO로 변환하여 반환
        return ReviewDTO.fromEntity(newOne);
    }catch (Exception e){
            System.out.println("서비스단: 오류 발생");
            e.printStackTrace();
            return null;
        }
    }

    // 리뷰 등록
    @Transactional
    public void save(ReviewDTO reviewDTO){
        try{
            Review add = reviewDTO.toEntity();
            System.out.println("서비스단 : 엔티티 변환 성공");
            repository.save(add);
            System.out.println("서비스단 : 엔티티에 저장 성공");
        }catch (Exception e){
            System.out.println("리뷰 등록 중 오류가 발생하였습니다." +e.getMessage());
        }
    }

    // 리뷰 삭제
    public Boolean remove (Long reviewId){
        try {
            repository.deleteById(reviewId);

            Optional<Review> list = repository.findById(reviewId);

            return list.isEmpty(); // 리뷰가 존재하지 않으면 true 반환
        }catch (Exception e){
            System.out.println("서비스단 : 리뷰 삭제 중 오류 발생  reviewId -->"+ reviewId);
            e.printStackTrace();
            return  false;
        }
    }
}
