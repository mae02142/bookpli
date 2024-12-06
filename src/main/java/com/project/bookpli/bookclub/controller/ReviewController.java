package com.project.bookpli.bookclub.controller;

import com.project.bookpli.bookclub.dto.ReviewDTO;
import com.project.bookpli.bookclub.service.ReviewService;
import com.project.bookpli.entity.Review;
import com.project.bookpli.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService service;

    // 유저 Id 조회 생각해보기..
    @GetMapping("/getId")
    public ResponseEntity<Long> getSession(HttpSession session){
        User info = (User) session.getAttribute("userInfo");
        System.out.println("호스트 세션세션 : "+ info);

        Long id = info !=null ?  info.getUserId() : null;
        System.out.println("호스트 아이디: "+id);
        return  ResponseEntity.ok(3L);
    }

    // 해당 유저가 작성한 리뷰 전체 조회
    @GetMapping("/myreview/{userId}")
    public ResponseEntity<List<ReviewDTO>> getListByUserId (@PathVariable Long userId ){

        try {
            List<ReviewDTO> mylist = service.readByUserId(userId);
            System.out.println(mylist);
            if(mylist.isEmpty()){
                // List가 비어있는 경우에는 빈 리스트를 그대로 반환하고, 상태 코드로 오류를 나타냄
                return ResponseEntity.badRequest().body(Collections.emptyList());  // 빈 리스트 반환
            }
            return ResponseEntity.ok(mylist);
        }catch (Exception e){
            log.error("컨트롤러 : 리뷰 조회에 오류 발생 " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 해당 도서의 리뷰 전체 조회
    @GetMapping("/book/{isbn13}")
    public ResponseEntity<List<ReviewDTO>> getListByisbn (@PathVariable String isbn){
        try{
            List<ReviewDTO> review = service.readByIsbn(isbn);
            System.out.println(review);
            if(review.isEmpty()){
                // List가 비어있는 경우에는 빈 리스트를 그대로 반환하고, 상태 코드로 오류를 나타냄
                return ResponseEntity.badRequest().body(Collections.emptyList());  // 빈 리스트 반환
            }
            return ResponseEntity.ok(review);
        }catch (Exception e){
            log.error("컨트롤러 : 리뷰 조회에 오류 발생 " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 리뷰 수정
    @PutMapping("/update")
    public ResponseEntity<ReviewDTO> update (@RequestBody  ReviewDTO review){
        try{
            // 수정한 값을 보내고 다시 dto 로 변환해서 가져오기
         ReviewDTO newReview = service.save(review);
            return ResponseEntity.ok(newReview);

        }catch (Exception e){
            log.error("컨트롤러 : 리뷰 업데이트에 오류 발생 " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
