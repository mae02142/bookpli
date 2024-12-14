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
public class PostResponseDTO {

    private Long imageId;
    private Long postId;
    private List<String> imageUrl; // 이미지 URL 리스트

    private Long userId;
    private Long bookClubId;
    private String postContent;
    private LocalDate postDate;

    private String userNickname;
    private String profilePath;


    // entity -> dto

    public static PostResponseDTO fromEntity(PostDTO postDTO, List<String> images) {
        return PostResponseDTO.builder()
                .postId(postDTO.getPostId())
                .userId(postDTO.getUserId())
                .bookClubId(postDTO.getBookClubId())
                .postContent(postDTO.getPostContent())
                .postDate(postDTO.getPostDate())
                .imageUrl(images)  // List<String>을 전달
                .userNickname(postDTO.getUserNickname())  // 필요시 추가적인 데이터도 넘길 수 있습니다
                .profilePath(postDTO.getProfilePath())
                .build();
    }

}
