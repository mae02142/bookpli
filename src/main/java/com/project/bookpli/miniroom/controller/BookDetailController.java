package com.project.bookpli.miniroom.controller;

import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.entity.BookLike;
import com.project.bookpli.library.repository.BookLikeRepository;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookDetailController {

    @Autowired
    private BookRepository bookrep;

    @Autowired
    private BookLikeRepository blrep;

    @Autowired
    private BookApiService service;

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

    //찜하기 여부
    @GetMapping("/{userId}/{isbn13}")
    public ResponseEntity<Boolean> likeordislike(@PathVariable Long userId, @PathVariable String isbn13){
        boolean isliked= service.isLiked(userId, isbn13);

        return ResponseEntity.ok(isliked);
    }
}

