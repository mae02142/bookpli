package com.project.bookpli.comment.dto;


import com.project.bookpli.entity.Comment;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
public class CommentDTO {

    private Long commentId;

    private Long userId;
    private Long postId;
    private String commentContent;
    private LocalDate commentDate;

    private String userNickname;
    private String profilePath;

    private Long bookClubId;


    // dto -> entity
    public Comment toEntity(){
        return Comment.builder()
                .commentId(this.commentId)
                .userId(this.userId)
                .postId(this.postId)
                .commentContent(this.commentContent)
                .commentDate(this.commentDate)
                .build();
    }

            // entity -> dto
    public static  CommentDTO fromEntity (Comment comment, String userNickname, String profilePath){
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .commentContent(comment.getCommentContent())
                .commentDate(comment.getCommentDate())
                .userNickname(userNickname)
                .profilePath(profilePath)
                .build();
    }


            // entity  -> dto include bookclubId
    public static  CommentDTO fromEntityBookClub (Comment comment, String userNickname, String profilePath, Long bookClubId){
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .commentContent(comment.getCommentContent())
                .commentDate(comment.getCommentDate())
                .userNickname(userNickname)
                .profilePath(profilePath)
                .bookClubId(bookClubId)
                .build();
    }
}
