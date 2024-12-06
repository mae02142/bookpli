package com.project.bookpli.miniroom.controller;

import com.project.bookpli.entity.Library;
import com.project.bookpli.miniroom.dto.BookDTO;
import com.project.bookpli.miniroom.dto.BookResponseDTO;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/miniroom")
public class MiniroomController {

    @Autowired
    private BookApiService bookApiService;

    @GetMapping("{itemId}")
    public ResponseEntity<String> bookList(@RequestParam String itemId){
        try{
            bookApiService.searchBook(itemId);
            return ResponseEntity.ok("도서 데이터가 성공적으로 저장되었습니다.");
        }catch(Exception e){
            return ResponseEntity.status(500).body("데이터 저장 중 오류발생:"+e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookResponseDTO>> getBookList(@PathVariable Long userId){
            List<BookResponseDTO> bookList= bookApiService.getBookList(userId);
            return ResponseEntity.ok(bookList);
    }
}
