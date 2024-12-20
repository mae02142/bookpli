import { defineStore } from "pinia";
import axios from "axios";
import { useUserStore } from "@/stores/user";

export const usePlayerStore = defineStore("player", {
  state: () => ({
    player: null,
    currentTrack: null,
    isPlaying: false,
    currentTime: 0,
    volume: 0.5,
    deviceId: null,
    token: null,
    updateInterval: null,
  }),
  actions: {
    initPlayer(token) {
      const userStore = useUserStore();
      this.token = userStore.accessToken;
      if (this.player) {
        return;
      }

      window.onSpotifyWebPlaybackSDKReady = () => {
        this.player = new window.Spotify.Player({
          name: "Web Playback SDK Player",
          getOAuthToken: (cb) => {
            cb(this.token);
          },
          volume: this.volume,
        });

        this.player.addListener("ready", ({ device_id }) => {
          this.deviceId = device_id;
          this.transferPlayback(device_id);
          console.log("Device ID : " + this.deviceId);
        });

        this.player.addListener("player_state_changed", (state) => {
          this.updateStateFromPlayerState(state);
          if (state) {
            const { position } = state;
            this.currentTime = Math.floor(position / 1000);
          }
        });

        this.player
          .connect()
          .then((success) => {
            if (success) {
              this.startLocalTimeIncrement();
            }
          })
          .catch((error) =>
            console.error("Failed to connect the Web Playback SDK:", error)
          );
      };

      if (window.Spotify && window.Spotify.Player) {
        window.onSpotifyWebPlaybackSDKReady();
      }
    },

    async transferPlayback(deviceId) {
      try {
        await axios.put(
          "https://api.spotify.com/v1/me/player",
          {
            device_ids: [deviceId],
            play: false,
          },
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${this.token}`,
            },
          }
        );
      } catch (error) {
        console.error(
          "Error transferring playback:",
          error.response ? error.response.data : error.message
        );
      }
    },

    togglePlay() {
      if (this.player) {
        this.player
          .togglePlay()
          .catch((err) => console.error("Error toggling play state:", err));
      }
    },

    setVolume(volume) {
      this.volume = volume;
      if (this.player) {
        this.player
          .setVolume(volume)
          .catch((err) => console.error("Error setting volume:", err));
      }
    },

    seekTrack(seconds) {
      if (this.player) {
        this.player
          .seek(seconds * 1000)
          .catch((err) => console.error("Error seeking track:", err));
        this.currentTime = seconds;
      }
    },

    nextTrack() {
      if (this.player) {
        this.player
          .nextTrack()
          .catch((err) => console.error("Error skipping to next track:", err));
      }
    },

    previousTrack() {
      if (this.player) {
        this.player
          .previousTrack()
          .catch((err) =>
            console.error("Error skipping to previous track:", err)
          );
      }
    },

    // URI에 따라 플레이리스트/단일 곡 재생
    async playContent(uri) {
      if (!this.deviceId || !this.token) {
        console.error("Device ID or Token is missing. Cannot start playback.");
        return;
      }

      try {
        if (uri.startsWith("spotify:playlist:")) {
          // 플레이리스트 재생 (첫 곡부터)
          await axios.put(
            "https://api.spotify.com/v1/me/player/play",
            {
              context_uri: uri,
              offset: {
                position: 0,
              },
            },
            {
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${this.token}`,
              },
              params: {
                device_id: this.deviceId,
              },
            }
          );
        } else if (uri.startsWith("spotify:track:")) {
          // 단일 곡 재생
          await axios.put(
            "https://api.spotify.com/v1/me/player/play",
            {
              uris: [uri],
            },
            {
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${this.token}`,
              },
              params: {
                device_id: this.deviceId,
              },
            }
          );
        } else {
          console.warn(
            "Unsupported URI type. Only playlists and tracks are supported."
          );
        }
      } catch (error) {
        console.error(
          "Error playing content:",
          error.response ? error.response.data : error.message
        );
      }
    },

    updateStateFromPlayerState(state) {
      if (!state || !state.track_window.current_track) {
        this.currentTrack = null;
        this.isPlaying = false;
        this.currentTime = 0;
        return;
      }
      const current_track = state.track_window.current_track;
      const { paused } = state;

      this.currentTrack = {
        title: current_track.name,
        artist: current_track.artists.map((artist) => artist.name).join(", "),
        albumCover:
          current_track.album.images[0]?.url ||
          "https://via.placeholder.com/200x200.png?text=No+Cover",
        duration: Math.floor(current_track.duration_ms / 1000),
        uri: current_track.uri,
      };
      this.isPlaying = !paused;
    },

    startLocalTimeIncrement() {
      if (this.updateInterval) return;
      this.updateInterval = setInterval(() => {
        if (this.isPlaying && this.currentTrack) {
          this.currentTime = Math.min(
            this.currentTime + 1,
            this.currentTrack.duration
          );
        }
      }, 1000);
    },

    stopLocalTimeIncrement() {
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
      }
    },

    async fetchAndCorrectState() {
      if (!this.player) return;
      const state = await this.player.getCurrentState();
      if (state) {
        this.updateStateFromPlayerState(state);
        this.currentTime = Math.floor(state.position / 1000);
      }
    },
  },
});
