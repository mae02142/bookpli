package com.project.bookpli.miniroom.controller;

import com.project.bookpli.miniroom.dto.BookResponseDTO;
import com.project.bookpli.miniroom.service.BookApiService;
import com.project.bookpli.mypage.dto.UserDTO;
import com.project.bookpli.mypage.service.MypageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/miniroom")
public class MiniroomController {

    @Autowired
    private BookApiService bookApiService;

    @Autowired
    private MypageService mypService;

    //도서 저장
    @PostMapping("/search/{itemId}")
    public ResponseEntity<String> searchBook(@PathVariable String itemId){
        try {
             bookApiService.searchBook(itemId);
            return ResponseEntity.ok("도서 정보가 성공적으로 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("오류: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }


    //담은 도서 리스트
    @GetMapping("/user/{userId}/book")
    public ResponseEntity<List<BookResponseDTO>> getBookList(@PathVariable Long userId,
                                                             @RequestParam(required = false, defaultValue = "wished") String status){
        List<BookResponseDTO> bookList= bookApiService.getBookList(userId, status);
        return ResponseEntity.ok(bookList);
    }

    //유저정보 가져오기
    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<UserDTO> getUserId(@PathVariable Long userId){
        UserDTO userInfo= mypService.getUserProfile(userId);
        return ResponseEntity.ok(userInfo);
    }

    //완독도서 처리
    @PutMapping("/clear/{isbn13}")
    public ResponseEntity<Integer> compReading(@PathVariable String isbn13, @RequestParam("status") String status){
        int updateStatus= bookApiService.clearRead(isbn13,status);
        return ResponseEntity.ok(updateStatus);
    }


    //도서 실패처리- 자동
    @PutMapping("/fail/{isbn13}")
    public ResponseEntity<Integer> changeToFail(@PathVariable String isbn13){
        int failRead= bookApiService.failReading(isbn13);
        return ResponseEntity.ok(failRead);
    }

}
