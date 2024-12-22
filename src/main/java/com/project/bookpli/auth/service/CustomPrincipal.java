package com.project.bookpli.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomPrincipal {
    private final String spotifyId;
    private final Long userId;
}
