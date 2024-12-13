package com.project.bookpli.miniroom.controller;

import com.project.bookpli.miniroom.dto.BookDTO;
import com.project.bookpli.miniroom.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookDetailController {

    @Autowired
    private BookRepository bookrep;

    //도서 상세
    @GetMapping("/{isbn13}")
    public ResponseEntity<List<BookDTO>> bookInfo(@PathVariable String isbn13){
        List<BookDTO> bookDetail= bookrep.findByIsbn13(isbn13);
        return ResponseEntity.ok(bookDetail);
    }
    
    //도서찜하기
//    @PutMapping("/{isbn13}")
//    public ResponseEntity<Integer> likeBook(@PathVariable String isbn13){
//        int bookLike =
//    }

    //도서 내서재 담기
}

