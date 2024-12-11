package com.project.bookpli.music.service;

import com.project.bookpli.auth.client.SpotifyApiClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MusicService {
    private final SpotifyApiClient spotifyApiClient;

    public MusicService(SpotifyApiClient spotifyApiClient) {
        this.spotifyApiClient = spotifyApiClient;
    }

    public Map<String, Object> getAlbumTracks(String albumId) {
        String endPoint = "/albums/" + albumId + "/tracks";
        return spotifyApiClient.sendGetRequest(endPoint);
    }
}
