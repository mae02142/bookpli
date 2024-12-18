package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String spotifyId;

    private String displayName;
    private String userNickname;

    @Column(unique = true)
    private String email;

    private String profilePath;

    private String refreshToken;

    private String role;

    @Builder
    public User(Long userId, String spotifyId, String displayName, String userNickname, String email, String profilePath, String refreshToken, String role) {
        this.userId = userId;
        this.spotifyId = spotifyId;
        this.displayName = displayName;
        this.userNickname = userNickname;
        this.email = email;
        this.profilePath = profilePath;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickName(String userNickname){
        this.userNickname = userNickname;
    }

}


