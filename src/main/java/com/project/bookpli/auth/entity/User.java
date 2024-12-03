package com.project.bookpli.auth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String spotifyId;

    @Column
    private String displayName;

    @Column
    private String userNickname;

    @Column
    private String email;

    @Column
    private String profilePath;

    @Column
    private String refreshToken;

    // refreshToken 업데이트 메서드
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}


