package com.project.bookpli.miniroom.controller;

import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/miniroom")
public class MiniroomController {

    @Autowired
    private BookApiService bookApiService;

    @GetMapping
    public ResponseEntity<String> bookList(@RequestParam String keyword, @RequestParam int maxData){
        try{
            bookApiService.saveBookList(keyword, maxData);
            return ResponseEntity.ok("도서 데이터가 성공적으로 저장되었습니다.");
        }catch(Exception e){
            return ResponseEntity.status(500).body("데이터 저장 중 오류발생:"+e.getMessage());
        }
    }
}
