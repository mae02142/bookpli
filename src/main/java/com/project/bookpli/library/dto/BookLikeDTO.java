package com.project.bookpli.library.dto;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.entity.Book;
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
    private BookDTO bookDTO;


    // Entity -> DTO
    public static BookLikeDTO fromEntity(BookLike bookLike, Book book) {
        return BookLikeDTO.builder()
                .bookLikeId(bookLike.getBookLikeId())
                .userId(bookLike.getUserId())
                .bookDTO(BookDTO.fromEntity(book))
                .build();
    }
}
