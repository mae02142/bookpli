package com.project.bookpli.library.dto;

import com.project.bookpli.entity.BookLike;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class BookLikeDTO {
    private Long bookLikeId;
    private Long userId;
    private String isbn13;

    // Entity -> DTO
    public static BookLikeDTO fromEntity(BookLike bookLike) {
        return BookLikeDTO.builder()
                .bookLikeId(bookLike.getBookLikeId())
                .userId(bookLike.getUserId())
                .isbn13(bookLike.getIsbn13())
                .build();
    }
}
