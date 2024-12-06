package com.project.bookpli.miniroom.dto;

import com.project.bookpli.entity.Book;
import lombok.*;

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
    private Date pubdate;
    private String publisher;
    private String cover;
    private Integer startindex;
    private String genre;

    // DTO -> Entity
    public Book toEntity() {
        return Book.builder()
                .isbn13(this.isbn13)
                .title(this.title)
                .author(this.author)
                .description(this.description)
                .pubdate(this.pubdate)
                .publisher(this.publisher)
                .cover(this.cover)
                .startindex(this.startindex)
                .genre(this.genre)
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
