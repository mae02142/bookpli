package com.project.bookpli.miniroom.dto;

import com.project.bookpli.entity.Book;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class MiniroomDTO {
    private String isbn13;
    private String title;
    private String author;
    private String description;
    private Date pubdate;
    private String publisher;
    private String cover;
    private Integer startIndex;
    private String genre;

    // DTO -> Entity
    @Builder
    public Book toEntity() {
        return Book.builder()
                .isbn13(this.isbn13)
                .title(this.title)
                .author(this.author)
                .description(this.description)
                .pubdate(this.pubdate)
                .publisher(this.publisher)
                .cover(this.cover)
                .startIndex(this.startIndex)
                .genre(this.genre)
                .build();
    }

    // Entity -> DTO
    public static MiniroomDTO fromEntity(Book book) {
        MiniroomDTO dto = new MiniroomDTO();

        dto.setIsbn13(book.getIsbn13());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setPubdate(book.getPubdate());
        dto.setPublisher(book.getPublisher());
        dto.setStartIndex(book.getStartIndex());
        dto.setCover(book.getCover());

        return dto;
    }
}
