package com.project.bookpli.comment.dto;

import com.project.bookpli.entity.CommentLike;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
public class CommentLikeDTO {
    private Long commentLikeId;
    private Long commentId;
    private Long userId;

    //dto -> entity
    public CommentLike toEntity(){
        return CommentLike.builder()
                .commentLikeId(this.commentLikeId)
                .commentId(this.commentId)
                .userId(this.userId)
                .build();
    }

    // entity -> dto
    public static CommentLikeDTO fromEntity(CommentLike commentLike){
        return CommentLikeDTO.builder()
                .commentLikeId(commentLike.getCommentLikeId())
                .commentId(commentLike.getCommentId())
                .userId((commentLike.getUserId()))
                .build();
    }
}
