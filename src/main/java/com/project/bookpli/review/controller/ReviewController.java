package com.project.bookpli.review.controller;

import com.project.bookpli.entity.Review;
import com.project.bookpli.review.dto.ReviewDTO;
import com.project.bookpli.review.service.ReviewService;
import com.project.bookpli.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService service;

            // 해당 유저가 작성한 리뷰 전체 조회
    @GetMapping("/myreview/{userId}")
    public ResponseEntity<List<ReviewDTO>> getListByUserId (@PathVariable Long userId ){
            List<ReviewDTO> mylist = service.readByUserId(userId);
            System.out.println(mylist);

            return ResponseEntity.ok(mylist);
    }

            // 해당 도서의 리뷰 전체 조회
    @GetMapping("/book/{isbn13}")
    public BaseResponse<List<ReviewDTO>> getListByisbn (@PathVariable String isbn13){
            List<ReviewDTO> review = service.readByIsbn(isbn13);
            System.out.println("컨트롤러 : 도서 리뷰 전체 출력 : "+ review);

            return new BaseResponse<>(review);
    }

                // 리뷰 수정
    @PutMapping("/update")
    public BaseResponse<ReviewDTO> updateReview(@RequestBody ReviewDTO review) {
        log.info("우선 도착은 했니 ..?");
        try {
            ReviewDTO updatedReview = service.update(review);
            log.info("리뷰 업데이트 성공");

            return new BaseResponse<>(updatedReview);
        } catch (Exception e) {
            log.error(e.getMessage());

            return new BaseResponse<>(false, e.getMessage(),500, null);
        }
    }

                //리뷰 등록
    @PostMapping("/post")
    public BaseResponse<Void> newReview (@RequestBody ReviewDTO reviewDTO){

        try{
            System.out.println("컨트롤러 : 책번호 -> "+reviewDTO.getIsbn13());
            service.save(reviewDTO);
            log.info("리뷰 등록 성공");

            return new BaseResponse<>();
        }catch (Exception e){
            log.error(e.getMessage());

            return new BaseResponse<>(false, e.getMessage(), 500,null);
        }
    }

                //리뷰 삭제
    @DeleteMapping("/delete/{reviewId}")
    public BaseResponse<Boolean> removeReview (@PathVariable Long reviewId) {

        System.out.println("컨트롤러 : 삭제할 리뷰 -> " + reviewId);
         boolean result= service.remove(reviewId);

         return new BaseResponse<>(result);
    }
}

