package com.project.bookpli.miniroom.controller;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.Library;
import com.project.bookpli.library.repository.LibraryRepository;
import com.project.bookpli.miniroom.dto.LibraryDTO;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/goal")
public class ReadingGoalController {

    @Autowired
    private BookRepository bookrep;

    @Autowired
    private LibraryRepository libraryrep;

    @Autowired
    private BookApiService service;


    //독서목표 설정 status 변경
//    @PutMapping("/change/{isbn13}")
//    public ResponseEntity<String> bookGoal(@PathVariable String isbn13, @RequestParam Long userId,
//                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
//
//            int update= libraryrep.setReadGoal(isbn13, userId, startDate, endDate);
//
//            if(update > 0){
//                return ResponseEntity.ok("독서목표가 설정되었습니다.");
//            }else{
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("독서 목표 설정 실패");
//            }
//    }

    //library 등록
//    @PutMapping("/register/{isbn13}")
//    public ResponseEntity<String> bookGoal(@PathVariable String isbn13,
//                                           @RequestBody LibraryDTO libraryDTO) {
//        try {
//             service.saveGoal(libraryDTO, isbn13);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("도서를 찾을 수 없습니다: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
//        }
//        return ResponseEntity.ok("도서 저장성공");
//    }

    //독서목표 설정 status 변경
    @PutMapping("/register/{isbn13}")
    public ResponseEntity<String> bookGoal(@PathVariable String isbn13,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){

        System.out.println(startDate + " >>>>>>>>>>> " + endDate);
        int update= libraryrep.setReadGoal(isbn13,startDate, endDate);


        if(update > 0){
            return ResponseEntity.ok("독서목표가 설정되었습니다.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("독서 목표 설정 실패");
        }
    }

    //독서 목표 해제
    @DeleteMapping("/{isbn13}")
    public ResponseEntity<String> dropReading(@PathVariable String isbn13,
                                              @RequestParam String status) {
        int stopRead = libraryrep.changeStatus(isbn13, status);

        if (stopRead > 0) {
            return ResponseEntity.ok("독서목표가 해제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("독서 목표 해제 실패");
        }
    }

    //독서 기간 수정
    @PutMapping("/reset/{isbn13}")
    public ResponseEntity<Integer> resetDate(@PathVariable String isbn13,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        int setPeriod = libraryrep.updateDate(isbn13,startDate,endDate);
        return ResponseEntity.ok(setPeriod);
    }

}
