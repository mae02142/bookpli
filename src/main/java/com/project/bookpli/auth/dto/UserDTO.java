package com.project.bookpli.auth.dto;

import com.project.bookpli.entity.User;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserDTO {

    private String spotifyId;
    private String displayName;
    private String email;
    private String profilePath;
    private String refreshToken;

    // DTO -> Entity
    @Builder
    public User toEntity() {
        return User.builder()
                .spotifyId(this.spotifyId)
                .displayName(this.displayName)
                .email(this.email)
                .refreshToken(this.refreshToken)
                .profilePath(this.profilePath)
                .build();
    }

    // Entity -> DTO
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setSpotifyId(user.getSpotifyId());
        dto.setDisplayName(user.getDisplayName());
        dto.setEmail(user.getEmail());
        dto.setRefreshToken(user.getRefreshToken());
        dto.setProfilePath(user.getProfilePath());
        return dto;
    }


}
