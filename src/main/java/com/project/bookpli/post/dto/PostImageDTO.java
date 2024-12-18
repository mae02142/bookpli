package com.project.bookpli.post.dto;

import com.project.bookpli.entity.PostImage;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
public class PostImageDTO {

    private Long imageId;
    private Long postId;
    private String imageUrl;


    //dto -> entity
    public PostImage toEntity(){
        return PostImage.builder()
                .imageId(this.imageId)
                .postId(this.postId)
                .imageUrl(this.getImageUrl())
                .build();
    }
}
