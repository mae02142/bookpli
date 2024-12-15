package com.project.bookpli.post.dto;

import com.project.bookpli.entity.Post;
import lombok.*;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
public class PostDTO {

    private Long postId;
    private Long userId;
    private Long bookClubId;
    private String postContent;
    private LocalDate postDate;

    private String userNickname;
    private String profilePath;


          // DTO -> Entity
    public Post toEntity() {

        return Post.builder()
                .postId(this.postId)
                .userId(this.userId)
                .bookClubId(this.bookClubId)
                .postContent(this.postContent)
                .postDate(this.postDate)
                .build();
    }

            //Entity - > DTO
    public static PostDTO fromEntity(Post post, String userNickname, String profilePath){
        return PostDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .bookClubId(post.getBookClubId())
                .postContent(post.getPostContent())
                .postDate(post.getPostDate())
                .userNickname(userNickname)
                .profilePath(profilePath)
                .build();
    }

    public static PostDTO fromEntityForOne(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .bookClubId(post.getBookClubId())
                .postContent(post.getPostContent())
                .postDate(post.getPostDate())
                .build();
    }


}
