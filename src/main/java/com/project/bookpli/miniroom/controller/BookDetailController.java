package com.project.bookpli.miniroom.controller;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.entity.BookLike;
import com.project.bookpli.library.repository.BookLikeRepository;
import com.project.bookpli.library.service.LibraryService;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/book")
public class BookDetailController {

    @Autowired
    private BookRepository bookrep;

    @Autowired
    private BookLikeRepository blrep;

    @Autowired
    private BookApiService bookService;

    @Autowired
    private LibraryService libraryService;

    //추천도서 상세조회
//    @GetMapping("/{isbn13}")
//    public ResponseEntity<?> getBookDetails(@PathVariable String isbn13) {
//        try {
//            // 서비스에서 도서 정보 조회
//            Optional<BookDTO> book = bookService.getBookDetailsByIsbn(isbn13);
//            return ResponseEntity.ok(book.get()); // 조회 성공
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(e.getMessage()); // 서버 오류
//        }
//    }

}

