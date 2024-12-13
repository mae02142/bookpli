package com.project.bookpli.book.controller;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.service.BookService;
import com.project.bookpli.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    @GetMapping("{isbn13}")
    public BaseResponse<BookDTO> getBookDetail(@PathVariable String isbn13){
        BookDTO response = bookService.getBookDetail(isbn13);
        System.out.println(">>>>>>>>>>>>>>>>"+response);
        return new BaseResponse<>(response);
    }
}
