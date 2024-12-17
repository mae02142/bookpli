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

    private Long postId;
    private List<PostImageDTO> imageUrl; // 이미지 URL 리스트

    private Long userId;
    private Long bookClubId;
    private String postContent;
    private LocalDate postDate;

    private String userNickname;
    private String profilePath;



    // entity -> dto
    public static PostResponseDTO fromEntity(PostDTO post, List<PostImage> images) {
        // PostImage를 PostImageDTO로 변환
        List<PostImageDTO> imageDtos = images.stream()
                .filter(image -> image.getPostId().equals(post.getPostId())) // postId 기준 매핑
                .map(image -> PostImageDTO.builder()
                        .imageId(image.getImageId())
                        .imageUrl(image.getImageUrl())
                        .postId(image.getPostId())
                        .build())
                .collect(Collectors.toList());

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .bookClubId(post.getBookClubId())
                .postContent(post.getPostContent())
                .postDate(post.getPostDate())
                .imageUrl(imageDtos) // 매핑된 이미지 리스트 전달
                .userNickname(post.getUserNickname()) // DTO에 필요하다면 추가
                .profilePath(post.getProfilePath())   // 프로필 경로 추가
                .build();
    }


    // 수정 조회
    public static PostResponseDTO fromEntityOne(PostDTO post, List<PostImage> images) {
        // PostImage 엔티티 리스트를 DTO 리스트로 변환
        List<PostImageDTO> imageDtos = images.stream()
                .filter(image -> image.getPostId().equals(post.getPostId()))
                .map(image -> PostImageDTO.builder()
                        .imageId(image.getImageId())
                        .imageUrl(image.getImageUrl())
                        .postId(image.getPostId())
                        .build())
                .collect(Collectors.toList());

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .bookClubId(post.getBookClubId())
                .postContent(post.getPostContent())
                .postDate(post.getPostDate())
                .imageUrl(imageDtos) // 이미지 리스트 설정
                .build();
    }


}
