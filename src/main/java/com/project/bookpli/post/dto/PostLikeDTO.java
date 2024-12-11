package com.project.bookpli.post.dto;

import com.project.bookpli.entity.PostLike;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
public class PostLikeDTO {

    private Long postLikeId;
    private Long postId;
    private Long userId;

    // dto -> entity
    public PostLike toEntity(){
        return PostLike.builder()
                .postId(this.postId)
                .userId(this.userId)
                .build();
    }


    // entity -> dto
    public static PostLikeDTO fromEntity(PostLike postLike){
        return PostLikeDTO.builder()
                .postLikeId(postLike.getPostLikeId())
                .postId(postLike.getPostId())
                .userId(postLike.getUserId())
                .build();
    }
}
