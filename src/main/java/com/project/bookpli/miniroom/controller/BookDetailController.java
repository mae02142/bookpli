package com.project.bookpli.miniroom.controller;

import com.project.bookpli.miniroom.dto.BookDTO;
import com.project.bookpli.miniroom.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
public class BookDetailController {

    @Autowired
    private BookDTO dto;

    @Autowired
    private BookRepository bookrep;

    @GetMapping("/{isbn13}")
    public BookDTO bookInfo(@PathVariable String isbn13){

    }
}
