package com.project.bookpli.bookclub.dto;


import com.project.bookpli.entity.UserBookClub;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class UserBookClubDTO {
    private Long userClubId;
    private Long userId;
    private Long bookClubId;
    private String title;
    private String cover;

    private String author;

        // dto -> entity
    public UserBookClub toEntity (){
        return UserBookClub.builder()
                .userId(this.userId)
                .bookClubId(this.bookClubId)
                .build();
    }


            // entity -> dto
    public static UserBookClubDTO fromEntity(UserBookClub userBookClub){
        return UserBookClubDTO.builder()
                .userClubId(userBookClub.getUserClubId())
                .userId(userBookClub.getUserId())
                .bookClubId(userBookClub.getBookClubId())
                .title(userBookClub.getBookClub().getBook().getTitle())
                .cover(userBookClub.getBookClub().getBook().getCover())
                .author(userBookClub.getBookClub().getBook().getAuthor())
                .build();
    }

}
