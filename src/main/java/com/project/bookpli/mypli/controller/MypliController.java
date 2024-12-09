package com.project.bookpli.mypli.controller;

import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.mypli.service.MypliService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypli")
public class MypliController {

    private final MypliService mypliService;


    @GetMapping
    public BaseResponse< Map<String, Object>> getMyPlaylist() {
        Map<String, Object> playlists = mypliService.getUserPlaylists();
        return new BaseResponse<>(playlists);
    }

    @GetMapping("/playlist/{playlistId}")
    public BaseResponse< Map<String, Object>> getPlaylistWithMusic(@PathVariable String playlistId) {
        System.out.println("playlistId : " + playlistId);
        Map<String, Object> music = mypliService.getPlaylistWithMusic(playlistId);
        return new BaseResponse<>(music);
    }
}
