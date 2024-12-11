package com.project.bookpli.music.controller;

import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.music.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/music")
public class MusicController {
    private final MusicService musicService;

    @GetMapping("/album/{albumId}")
    public BaseResponse<Map<String, Object>> getAlbumTracks(@PathVariable String albumId) {
        Map<String, Object> response = musicService.getAlbumTracks(albumId);
        return new BaseResponse<>(response);
    }



}
