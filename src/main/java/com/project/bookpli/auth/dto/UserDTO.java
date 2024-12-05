package com.project.bookpli.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookpli.entity.User;
import lombok.*;

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
    @JsonProperty("images[0].url")
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
        dto.setProfilePath(user.getProfilePath());
        return dto;
    }


}
