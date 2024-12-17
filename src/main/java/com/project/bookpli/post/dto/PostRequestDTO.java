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


    private Long postId;
    private List<PostImageDTO> imageUrl; // 이미지 URL 리스트

    private Long userId;
    private Long bookClubId;
    private String postContent;
    private LocalDate postDate;


    // dto -> entity
    public List<PostImage> toEntity() {
        List<PostImage> entity = new ArrayList<>();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            for (PostImageDTO postImageDTO : imageUrl) {
                // imageId가 null이 아니면 기존 이미지 ID 사용, 없으면 null
                Long imgId = postImageDTO.getImageId() != null ? postImageDTO.getImageId() : null;
                // imageUrl 가져오기
                String url = postImageDTO.getImageUrl();
                // PostImage entity 생성
                entity.add(PostImage.builder()
                        .imageId(imgId)
                        .postId(postId)
                        .imageUrl(url)
                        .build());
            }
        }
        return entity;
    }
}


