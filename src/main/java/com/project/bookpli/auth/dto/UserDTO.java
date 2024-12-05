package com.project.bookpli.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookpli.entity.User;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
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
    @Builder
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
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setSpotifyId(user.getSpotifyId());
        dto.setDisplayName(user.getDisplayName());
        dto.setEmail(user.getEmail());
        dto.setUserNickname(user.getUserNickname());
        dto.setProfilePath(user.getProfilePath());
        return dto;
    }

    // 내부 클래스: 이미지 구조 매핑
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String url;
    }


}
