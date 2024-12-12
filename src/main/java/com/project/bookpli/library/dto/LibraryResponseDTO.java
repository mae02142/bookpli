package com.project.bookpli.library.dto;

import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.Library;
import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class LibraryResponseDTO {
    private Long libraryId;
    private Long userId;
    private String status;
    private Date startDate;
    private Date endDate;

    private String isbn13;
    private String title;
    private String author;
    private String cover;

    // DTO -> Entity
    public Library toEntity(Book book) {
        return Library.builder()
                .userId(this.userId)
                .status(this.status)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .book(book)
                .build();
    }

    //Entity -> DTO
    public static LibraryResponseDTO fromEntity(Library library, Book book) {
        return LibraryResponseDTO.builder()
                .libraryId(library.getLibraryId())
                .userId(library.getUserId())
                .status(library.getStatus())
                .startDate(library.getStartDate())
                .endDate(library.getEndDate())
                .isbn13(book.getIsbn13())
                .title(book.getTitle())
                .author(book.getAuthor())
                .cover(book.getCover())
                .build();
    }
}
