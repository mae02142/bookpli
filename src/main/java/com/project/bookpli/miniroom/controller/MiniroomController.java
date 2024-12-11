package com.project.bookpli.miniroom.controller;

import com.project.bookpli.entity.Library;
import com.project.bookpli.miniroom.dto.BookDTO;
import com.project.bookpli.miniroom.dto.BookResponseDTO;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/miniroom")
public class MiniroomController {

    @Autowired
    private BookApiService bookApiService;

    //담은 도서 리스트
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookResponseDTO>> getBookList(@PathVariable Long userId,
                                                             @RequestParam(required = false, defaultValue = "wished") String status){
        List<BookResponseDTO> bookList= bookApiService.getBookList(userId, status);
        return ResponseEntity.ok(bookList);
    }

}
