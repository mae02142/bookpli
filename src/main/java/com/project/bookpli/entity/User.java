package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickName(String userNickname){
        this.userNickname = userNickname;
    }

}


