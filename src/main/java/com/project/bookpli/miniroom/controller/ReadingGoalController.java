package com.project.bookpli.miniroom.controller;

import com.project.bookpli.miniroom.dto.BookResponseDTO;
import com.project.bookpli.miniroom.repository.BookRepository;
import com.project.bookpli.miniroom.repository.LibraryRepository;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goal")
public class ReadingGoalController {

    @Autowired
    private BookRepository bookrep;

    @Autowired
    private LibraryRepository libraryrep;

    @Autowired
    private BookApiService service;

    @PutMapping("/{isbn13}")
    public ResponseEntity<String> bookGoal(@RequestParam String status,
                                           @RequestParam String startDate,
                                           @RequestParam String endDate){

        int update= libraryrep.setReadGoal(status,startDate, endDate);

        if(update > 0){
            return ResponseEntity.ok("독서목표가 설정되었습니다.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("독서 목표 설정 실패");
        }
    }
}
