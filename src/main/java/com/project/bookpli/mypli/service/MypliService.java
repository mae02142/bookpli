package com.project.bookpli.mypli.service;

import com.project.bookpli.auth.client.SpotifyApiClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MypliService {
    private final SpotifyApiClient spotifyApiClient;

    public MypliService(SpotifyApiClient spotifyApiClient) {
        this.spotifyApiClient = spotifyApiClient;
    }

    public Map<String, Object> getUserPlaylists() {
        return spotifyApiClient.sendGetRequest("/me/playlists");
    }

    public Map<String, Object> getPlaylistWithMusic(String playlistId) {
        String endpoint = "/playlists/" + playlistId + "/tracks";
        return spotifyApiClient.sendGetRequest(endpoint);
    }
}
