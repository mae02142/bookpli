package com.project.bookpli.post.dto;


import com.project.bookpli.entity.Post;
import com.project.bookpli.entity.PostImage;
import lombok.*;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostImageDTO {

    private Long imageId;
    private Long postId;
    private String imageUrl;

    // dto -> entity

    public  PostImage toEntity(){
        return PostImage.builder()
                .postId(this.postId)
                .imageUrl(this.imageUrl)
                .build();
    }

    // entity -> dto

    public static PostImageDTO fromEntity (PostImage postImage){
        return PostImageDTO.builder()
                .imageId(postImage.getImageId())
                .postId(postImage.getPostId())
                .imageUrl(postImage.getImageUrl())
                .build();
    }

}
