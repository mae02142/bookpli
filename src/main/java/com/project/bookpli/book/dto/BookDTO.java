package com.project.bookpli.book.dto;

import com.project.bookpli.entity.Book;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class BookDTO {
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
    public static Book toEntity(BookDTO bookDTO) {
        return Book.builder()
            .isbn13(bookDTO.getIsbn13())
            .title(bookDTO.getTitle())
            .author(bookDTO.getAuthor())
            .description(bookDTO.getDescription())
            .pubdate(bookDTO.getPubdate())
            .publisher(bookDTO.getPublisher())
            .cover(bookDTO.getCover())
            .startindex(bookDTO.getStartindex())
            .genre(bookDTO.getGenre())
            .build();
    }

    // Entity -> DTO
    public static BookDTO fromEntity(Book book) {
        return BookDTO.builder()
            .isbn13(book.getIsbn13())
            .title(book.getTitle())
            .author(book.getAuthor())
            .description(book.getDescription())
            .pubdate(book.getPubdate())
            .publisher(book.getPublisher())
            .cover(book.getCover())
            .startindex(book.getStartindex())
            .genre(book.getGenre())
            .build();
}
}
