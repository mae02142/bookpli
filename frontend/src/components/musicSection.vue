<template>
    <div class="music-app">
    <!-- Recommendations Section -->
    <section class="recommendations">
        <h1 class="section-title">추천 플레이리스트</h1>
        <hr />
        <div class="music-list">
            <div
                v-for="pli in recommendedPli.slice(0, 5)"
                :key="pli.id"
                class="music-item"
                style="padding-top: 50px;"
            >
                <div class="image-container">
                    <img
                        :src="pli.image"
                        :alt="pli.title"
                        class="music-image"
                        @click="playPlaylist(pli.uri)"
                        style="cursor: pointer;"
                        :title="`플레이리스트 재생: ${pli.title}`"
                    />
                    <button class="add-btn">+</button>
                </div>
                <p class="pli-title" style="font-size: 20px; padding-bottom: 5px;">
                    {{ pli.title }}
                </p>
                <p class="pli-producer">{{ pli.artist }}</p>
            </div>
        </div>
    </section>

    <!-- Rankings Section -->
    <section class="rankings" style="padding-top: 50px;">
        <h2 class="section-title">음악 순위</h2>
        <hr />
        <div class="rankings-container" style="width: 90%;">
            <!-- Domestic Rankings -->
            <div class="ranking" style="padding-top: 30px;">
                <h3 class="ranking-title">국내</h3>
                <table class="ranking-table">
                    <tr
                        class="ranking-tr"
                        v-for="(song, index) in domesticRankingPli.slice(0, 5)"
                        :key="song.uri"
                    >
                        <td style="width: 30px;">{{ index + 1 }}</td>
                        <td>
                            <img
                                :src="song.image"
                                class="album-cover"
                                @click="playSong(song.uri)"
                                style="cursor: pointer;"
                                :title="`재생: ${song.title} - ${song.artist}`"
                            />
                        </td>
                        <td class="song-title" @click="playSong(song.uri)">{{ song.title }}</td>
                        <td class="song-artist">{{ song.artist }}</td>
                    </tr>
                </table>
            </div>
            <!-- International Rankings -->
            <div class="ranking" style="padding-top: 30px;">
                <h3 class="ranking-title">해외</h3>
                <table class="ranking-table">
                    <tr
                        class="ranking-tr"
                        v-for="(song, index) in internationalRankingPli.slice(0, 5)"
                        :key="song.uri"
                    >
                        <td style="width: 30px;">{{ index + 1 }}</td>
                        <td>
                            <img
                                :src="song.image"
                                class="album-cover"
                                @click="playSong(song.uri)"
                                style="cursor: pointer;"
                                :title="`재생: ${song.title} - ${song.artist}`"
                            />
                        </td>
                        <td class="song-title"  @click="playSong(song.uri)">{{ song.title }}</td>
                        <td class="song-artist">{{ song.artist }}</td>
                    </tr>
                </table>
            </div>
        </div>
    </section>

    <!-- Music Player Component -->
    <MusicPlayer />
    </div>
</template>

<script>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/auth";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";

export default {
    components: {
        MusicPlayer,
    },
    setup() {
        const authStore = useAuthStore();
        const recommendedPli = ref([]);
        const domesticRankingPli = ref([]);
        const internationalRankingPli = ref([]);

        const token = authStore.token; // Spotify API token from auth store
        console.log("token in music section : " + token);
        const recommendPliApiUrl =
            "https://api.spotify.com/v1/search?q=book&type=playlist&include_external=audio";
        const domesticRankingsApiUrl =
            "https://api.spotify.com/v1/search?q=melon+top100&type=playlist&include_external=audio";
        const internationalRankingsApiUrl =
            "https://api.spotify.com/v1/search?q=BillBoard+Hot+100&type=playlist";

        // Function to fetch recommended playlists
        const fetchRecommendedPlaylists = async () => {
            try {
                const response = await axios.get(recommendPliApiUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const playlists = response.data.playlists.items;

                // Map the playlists to include the URI
                recommendedPli.value = playlists
                    .filter((playlist) => playlist !== null && playlist !== undefined)
                    .map((playlist) => ({
                        id: playlist.id,
                        uri: playlist.uri, // 플레이리스트 URI 추가
                        title: playlist.name || "No Title",
                        artist: playlist.owner.display_name || "Unknown Artist",
                        image:
                            playlist.images[0]?.url ||
                            "https://via.placeholder.com/200x200.png?text=No+Image",
                    }));
            } catch (error) {
                console.error(
                    "Error fetching playlists:",
                    error.response ? error.response.data : error.message
                );
            }
        };

        // Domestic rankings data
        const fetchDomesticRanking = async () => {
            try {
                const response = await axios.get(domesticRankingsApiUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Get the first playlist
                const playlists = response.data.playlists.items;
                const firstPlaylistId = playlists[0]?.id;

                if (!firstPlaylistId) {
                    console.error("No playlist found.");
                    return;
                }

                // Fetch playlist details to get tracks
                const playlistDetailsUrl = `https://api.spotify.com/v1/playlists/${firstPlaylistId}`;
                const playlistResponse = await axios.get(playlistDetailsUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const tracks = playlistResponse.data.tracks.items;

                // Map the top 10 tracks to domesticRankings
                domesticRankingPli.value = tracks.slice(0, 10).map((track) => ({
                    title: track.track.name || "Unknown Title",
                    artist:
                        track.track.artists.map((artist) => artist.name).join(", ") ||
                        "Unknown Artist",
                    album: track.track.album.name || "Unknown Album",
                    image:
                        track.track.album.images[0]?.url ||
                        "https://via.placeholder.com/60x60.png?text=No+Image",
                    uri: track.track.uri,
                }));

                console.log("Top 10 Songs for Domestic Rankings:", domesticRankingPli.value);
            } catch (error) {
                console.error(
                    "Error fetching playlist or songs:",
                    error.response ? error.response.data : error.message
                );
            }
        };

        // International rankings data
        const fetchInternationalRanking = async () => {
            try {
                const responseint = await axios.get(internationalRankingsApiUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Get the first playlist
                const intplaylists = responseint.data.playlists.items;
                const intfirstPlaylistId = intplaylists[0]?.id;

                if (!intfirstPlaylistId) {
                    console.error("No playlist found.");
                    return;
                }

                // Fetch playlist details to get tracks
                const intplaylistDetailsUrl = `https://api.spotify.com/v1/playlists/${intfirstPlaylistId}`;
                const playlistResponse = await axios.get(intplaylistDetailsUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const tracks = playlistResponse.data.tracks.items;

                // Map the top 10 tracks to internationalRanking
                internationalRankingPli.value = tracks.slice(0, 10).map((track) => ({
                    title: track.track.name || "Unknown Title",
                    artist:
                        track.track.artists.map((artist) => artist.name).join(", ") ||
                        "Unknown Artist",
                    album: track.track.album.name || "Unknown Album",
                    image:
                        track.track.album.images[0]?.url ||
                        "https://via.placeholder.com/60x60.png?text=No+Image",
                    uri: track.track.uri,
                }));

                console.log(
                    "Top 10 Songs for International Rankings:",
                    internationalRankingPli.value
                );
            } catch (error) {
                console.error(
                    "Error fetching playlist or songs:",
                    error.response ? error.response.data : error.message
                );
            }
        };

        // Function to get active devices
        const getActiveDevices = async () => {
            try {
                const response = await axios.get("https://api.spotify.com/v1/me/player/devices", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                console.log("Active Devices:", response.data.devices);
                return response.data.devices;
            } catch (error) {
                console.error(
                    "Error fetching active devices:",
                    error.response ? error.response.data : error.message
                );
                return [];
            }
        };

        // Function to play a song given its URI
        const playSong = async (uri) => {
            const playUrl = "https://api.spotify.com/v1/me/player/play";

            try {
                const devices = await getActiveDevices();
                if (devices.length === 0) {
                    alert(
                        "활성화된 Spotify 기기가 없습니다. Spotify 앱을 열어 활성화된 기기를 만드세요."
                    );
                    return;
                }

                await axios.put(
                    playUrl,
                    {
                        uris: [uri],
                    },
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json",
                        },
                    }
                );

                console.log(`Playing song: ${uri}`);
            } catch (error) {
                console.error(
                    "Error playing song:",
                    error.response ? error.response.data : error.message
                );
                alert("노래를 재생할 수 없습니다. Spotify가 활성화되어 있는지 확인하세요.");
            }
        };

        // Function to play a playlist from the start using its URI
        const playPlaylist = async (playlistUri) => {
            const playUrl = "https://api.spotify.com/v1/me/player/play";

            try {
                const devices = await getActiveDevices();
                if (devices.length === 0) {
                    alert(
                        "활성화된 Spotify 기기가 없습니다. Spotify 앱을 열어 활성화된 기기를 만드세요."
                    );
                    return;
                }

                await axios.put(
                    playUrl,
                    {
                        context_uri: playlistUri,
                        offset: { position: 0 }, // 첫 번째 곡부터 재생
                    },
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json",
                        },
                    }
                );

                console.log(`Playing playlist: ${playlistUri}`);
            } catch (error) {
                console.error(
                    "Error playing playlist:",
                    error.response ? error.response.data : error.message
                );
                alert("플레이리스트를 재생할 수 없습니다. Spotify가 활성화되어 있는지 확인하세요.");
            }
        };

        // Fetch playlists on component mount
        onMounted(() => {
            fetchRecommendedPlaylists();
            fetchDomesticRanking();
            fetchInternationalRanking();
        });

        return {
            recommendedPli,
            domesticRankingPli,
            internationalRankingPli,
            playSong,
            playPlaylist,
        };
    },
};
</script>

<style scoped>
/* 기존 스타일 유지 */
body {
    font-family: Arial, sans-serif;
    background-color: #ffff;
}

.music-app {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0.5rem;
}

.recommendations {
    margin: 2rem 0;
}

.section-title {
    font-size: 3rem;
    margin-bottom: 1rem;
}

.pli-title {
    max-width: 170px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
}

.music-list {
    display: flex;
    gap: 1rem;
}

.music-item {
    margin: auto;
}

.music-image {
    width: 200px;
    height: 200px;
    object-fit: cover;
    margin-bottom: 0.5rem;
}

.image-container {
    position: relative;
}

.add-btn {
    background-color: #ffff;
    border: none;
    padding: 0.5rem;
    width: 32px;
    height: 32px;
    font-size: 18px;
    border-radius: 50%;
    cursor: pointer;
    position: absolute;
    bottom: 20px;
    right: 10px;
    z-index: 10;
}

.rankings-container {
    margin: auto;
    display: flex;
    justify-content: space-between;
}

.ranking {
    width: 45%;
}

.ranking-title {
    font-size: 1.7rem;
    margin-bottom: 1rem;
    vertical-align: middle;
}

.ranking-table {
    width: 100%;
    margin: auto;
    vertical-align: middle;
}

.ranking-tr {
    border-bottom: 1px solid black;
    height: 50px;
    vertical-align: middle;
}

.song-title {
    max-width: 300px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    vertical-align: middle;
    cursor: pointer;
    transition: transform 0.2s;
}
.song-artist {
    max-width: 100px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    vertical-align: middle;
}

.album-cover {
    width: 60px;
    height: 60px;
    cursor: pointer;
    transition: transform 0.2s;
}

.album-cover:hover {
    transform: scale(1.05);
}
</style>
