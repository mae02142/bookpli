package com.project.bookpli.miniroom.dto;

import com.project.bookpli.entity.Book;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class BookResponseDTO {
    private Long libraryId;
    private Long userId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    private String isbn13;
    private String title;
    private String author;
    private String description;
    private LocalDate pubdate;
    private String publisher;
    private String cover;
    private Integer startindex;
    private String genre;
}
