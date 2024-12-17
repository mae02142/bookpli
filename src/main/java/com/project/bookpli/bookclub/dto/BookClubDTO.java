package com.project.bookpli.bookclub.dto;

import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.BookClub;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class BookClubDTO {
    private Long bookClubId;

    private String isbn13;
    private String title;
    private String author;
    private String cover;
    private String description;


        // DTO -> Entity 변환
    public BookClub toEntity(Book book){
        return BookClub.builder()
                .bookClubId(this.bookClubId)
                .book(book)
                .build();
    }

        // Entity -> DTO 변환
    public static BookClubDTO fromEntity(BookClub bookClub){
        return BookClubDTO.builder()
                .bookClubId(bookClub.getBookClubId())
                .isbn13(bookClub.getBook().getIsbn13())
                .title(bookClub.getBook().getTitle())
                .author(bookClub.getBook().getAuthor())
                .cover(bookClub.getBook().getCover())
                .description(bookClub.getBook().getDescription())
                .build();
    }
}
