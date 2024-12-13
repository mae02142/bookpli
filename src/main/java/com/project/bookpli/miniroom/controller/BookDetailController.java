package com.project.bookpli.miniroom.controller;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.entity.BookLike;
import com.project.bookpli.miniroom.dto.BookLikeDTO;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookDetailController {

    @Autowired
    private BookRepository bookrep;

    @Autowired
    private BookApiService service;

    //도서 상세
//    @GetMapping("/{isbn13}")
//    public ResponseEntity<List<BookDTO>> bookInfo(@PathVariable String isbn13){
//        List<BookDTO> bookDetail= bookrep.findByIsbn13(isbn13);
//        return ResponseEntity.ok(bookDetail);
//    }
    
    //도서 찜하기
    @PostMapping("/like/{userId}/{isbn13}")
    public ResponseEntity<BookLike> likeBook(@PathVariable Long userId,@PathVariable String isbn13){
        BookLike likeBook=service.likeBook(userId,isbn13);
        return ResponseEntity.ok(likeBook);
    }

    //찜하기 해제
    @DeleteMapping("/dislike/{userId}/{isbn13}")
    public ResponseEntity<String> dislike(@PathVariable Long userId, @PathVariable String isbn13){
        service.dislike(userId,isbn13);
        return ResponseEntity.ok("찜하기가 해제되었습니다.");
    }

}

