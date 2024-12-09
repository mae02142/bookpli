package com.project.bookpli.miniroom.dto;

import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.Library;
import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class LibraryDTO {
    private Long user_id;
    private String isbn13;
    private String status;
    private Date startDate;
    private Date endDate;


    // DTO -> Entity
    public Library toEntity(Book book) {
        return Library.builder()
                .userId(this.user_id)
                .book(book)
                .status(this.status)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .build();
    }

    // Entity -> DTO
    public static LibraryDTO fromEntity(Library library) {
        return LibraryDTO.builder()
                .user_id(library.getUserId())
                .isbn13(library.getBook().getIsbn13())
                .status(library.getStatus())
                .startDate(library.getStartDate())
                .endDate(library.getEndDate())
                .build();
    }
}
