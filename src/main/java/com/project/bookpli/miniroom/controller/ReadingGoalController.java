package com.project.bookpli.miniroom.controller;

import com.project.bookpli.miniroom.repository.BookRepository;
import com.project.bookpli.library.repository.LibraryRepository;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
    @PutMapping("register/{isbn13}")
    public ResponseEntity<String> bookGoal(@PathVariable String isbn13,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate){

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
                                              @RequestParam String status){
        int stopRead= libraryrep.changeStatus(isbn13,status);

        if(stopRead > 0){
            return ResponseEntity.ok("독서목표가 해제되었습니다.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("독서 목표 해제 실패");
        }
    }

}
