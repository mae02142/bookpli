package com.project.bookpli.post.dto;

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

}
