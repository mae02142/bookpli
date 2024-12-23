package com.project.bookpli.mypli.controller;

import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.mypli.service.MypliService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypli")
public class MypliController {

    private final MypliService mypliService;


    @GetMapping
    public BaseResponse<Map<String, Object>> getMyPlaylist() {
        Map<String, Object> playlists = mypliService.getUserPlaylists();
        return new BaseResponse<>(playlists);
    }

    @GetMapping("/playlist/{playlistId}")
    public BaseResponse<Map<String, Object>> getPlaylistWithMusic(@PathVariable String playlistId) {
        System.out.println("playlistId : " + playlistId);
        Map<String, Object> music = mypliService.getPlaylistWithMusic(playlistId);
        return new BaseResponse<>(music);
    }

    @PostMapping("{spotifyId}")
    public BaseResponse<Map<String, Object>> createPlaylist(@PathVariable String spotifyId, @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> response = mypliService.createPlaylist(spotifyId, requestBody);
        return new BaseResponse<>(response);
    }

    @DeleteMapping("/playlist/{playlistId}/tracks")
    public BaseResponse<Void> deleteTrack(@PathVariable String playlistId, @RequestBody Map<String, String> requestBody) {
        mypliService.deleteTrack(playlistId, requestBody.get("uri"));
        return new BaseResponse<>();
    }

    @PutMapping("/playlist/{playlistId}")
    public BaseResponse<Void> updatePlaylistTitle(@PathVariable String playlistId, @RequestBody Map<String, String> request) {
        mypliService.updatePlaylistTitle(playlistId, request);
        return new BaseResponse<>();

    }
    @DeleteMapping("/playlist/{playlistId}")
    public BaseResponse<Void> deletePlaylist(@PathVariable String playlistId) {
        mypliService.deletePlaylist(playlistId);
        return new BaseResponse<>();
    }

    @PostMapping("/playlist/{playlistId}")
    public BaseResponse<Void> addPlaylist(@PathVariable String playlistId, @RequestBody Map<String, Object> request) {
        mypliService.addPlaylist(playlistId, request);
        return new BaseResponse<>();
    }


}
