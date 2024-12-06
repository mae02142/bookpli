package com.project.bookpli.auth.dto;

import com.project.bookpli.mypage.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private UserDTO user;
    private String accessToken;
}
