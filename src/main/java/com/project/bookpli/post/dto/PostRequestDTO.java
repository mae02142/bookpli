package com.project.bookpli.post.dto;

import com.project.bookpli.entity.Post;
import com.project.bookpli.entity.PostImage;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
public class PostRequestDTO {

    private Long imageId;
    private Long postId;
    private List<String> imageUrl; // 이미지 URL 리스트

    private Long userId;
    private Long bookClubId;
    private String postContent;
    private LocalDate postDate;


    // dto -> entity
    public List <PostImage> toEntity(){
        return imageUrl.stream()
                .map(url -> new PostImage(postId,url))
                .collect(Collectors.toList());
    }

}
