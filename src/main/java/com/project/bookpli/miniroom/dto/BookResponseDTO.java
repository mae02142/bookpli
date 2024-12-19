package com.project.bookpli.miniroom.dto;

import com.project.bookpli.book.dto.BookDTO;
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


    // DTO -> Entity
//    public static Book toEntity(BookResponseDTO dto) {
//        return Book.builder()
//                .isbn13(dto.getIsbn13())
//                .title(dto.getTitle())
//                .author(dto.getAuthor())
//                .description(dto.getDescription())
//                .pubdate(dto.getPubdate())
//                .publisher(dto.getPublisher())
//                .cover(dto.getCover())
//                .startindex(dto.getStartindex())
//                .genre(dto.getGenre())
//                .build();
//    }
//
//    // Entity -> DTO
//    public static BookResponseDTO fromEntity(Book book) {
//        return BookResponseDTO.builder()
//                .title(book.getTitle())
//                .author(book.getAuthor())
//                .description(book.getDescription())
//                .pubdate(book.getPubdate())
//                .publisher(book.getPublisher())
//                .cover(book.getCover())
//                .startindex(book.getStartindex())
//                .genre(book.getGenre())
//                .build();
//    }

}
