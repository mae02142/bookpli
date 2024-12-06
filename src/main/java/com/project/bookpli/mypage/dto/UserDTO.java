package com.project.bookpli.mypage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookpli.entity.User;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class UserDTO {

    private Long userId;

    @JsonProperty("id")
    private String spotifyId;

    @JsonProperty("display_name")
    private String displayName;

    private String email;
    private String userNickname;

    @JsonProperty("images")
    private void setImages(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            this.profilePath = images.get(0).getUrl(); // 첫 번째 URL 추출
        }
    }

    @JsonProperty("profilePath") // JSON 직렬화 시 이 필드만 포함
    private String profilePath;

    // DTO -> Entity
    public User toEntity() {
        return User.builder()
                .spotifyId(this.spotifyId)
                .displayName(this.displayName)
                .email(this.email)
                .profilePath(this.profilePath)
                .build();
    }

    // Entity -> DTO
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .spotifyId(user.getSpotifyId())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .userNickname(user.getUserNickname())
                .profilePath(user.getProfilePath())
                .build();
    }

    // 내부 클래스: 이미지 구조 매핑
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String url;
    }


}
