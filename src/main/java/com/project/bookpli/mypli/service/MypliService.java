package com.project.bookpli.mypli.service;

import com.project.bookpli.auth.client.SpotifyApiClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public void deleteTrack(String playlistId, String trackUri) {
        Map<String, Object> track = Map.of(
                "tracks", List.of(Map.of("uri", trackUri))
        );
        String endPoint = "/playlists/" + playlistId + "/tracks";
        spotifyApiClient.sendDeleteRequest(endPoint, track);
    }
    public Map<String, Object> createPlaylist(String spotifyId, Map<String, Object> request) {
        String endPoint = "/users/" + spotifyId + "/playlists";
        return spotifyApiClient.sendPostRequest(endPoint, request);
    }
}
