package com.project.bookpli.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String spotifyId;
    private String displayName;
    private String email;
    private String refreshToken;
    private String userNickname;
    private String profilePath;
}
