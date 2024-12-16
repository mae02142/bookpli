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
                        <!-- Add Button with Disabled State -->
                        <button
                            class="add-btn"
                            @click="addPlaylistToMyPlaylists(pli.id)"
                            :disabled="!userProfile"
                            :title="userProfile ? '플레이리스트 추가하기' : '사용자 정보를 불러오는 중입니다.'"
                        >
                            +
                        </button>
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
            <div class="rankings-container" style="width: 100%;">
                <!-- Domestic Rankings -->
                <div class="ranking" style="padding-top: 30px;">
                    <h3 class="ranking-title">국내</h3>
                    <table class="ranking-table">
                        <tr
                            class="ranking-tr"
                            v-for="(song, index) in domesticRankingPli.slice(0, 5)"
                            :key="song.uri"
                        >
                            <td style="min-width: 40px; text-align: center;">{{ index + 1 }}</td>
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
                            <td
                                class="song-details"
                                style="min-width: 20px; text-align: center; position: relative;"
                                @click="toggleOptionMenu1(index)"
                            >
                                ⋮
                                <div v-show="showOptionMenu1[index]" class="option-menu">
                                    <span style="padding-bottom: 5px;" @click="playSong(song.uri)">재생하기</span>
                                    <span style="padding-bottom: 5px;" @click="selectPlaylist">내 플리에 추가하기</span>
                                    <span style="padding-bottom: 5px;" @click="openSongDetail(song)">앨범 정보 보기</span>
                                </div>
                            </td>
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
                            <td style="min-width: 40px; text-align: center;">{{ index + 1 }}</td>
                            <td class="song-cover">
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
                            <td
                                class="song-details"
                                style="min-width: 20px; text-align: center; position: relative;"
                                @click="toggleOptionMenu2(index)"
                            >
                                ⋮
                                <div v-show="showOptionMenu2[index]" class="option-menu">
                                    <span style="padding-bottom: 5px;" @click="playSong(song.uri)">재생하기</span>
                                    <span style="padding-bottom: 5px;" @click="selectPlaylist">내 플리에 추가하기</span>
                                    <span style="padding-bottom: 5px;" @click="openSongDetail(song)">앨범 정보 보기</span>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </section>

        <!-- Music Player Component -->
        <MusicPlayer />
        <SongDetailModal
            v-if="modalStore.activeModal === 'SongDetailModal'"
            :song="selectedSong"
            @close="modalStore.closeModal"
            @update-playlist="getUserPlaylist"
            @update-tracks="refreshPlaylistTracks"
        />
    </div>
</template>
<script>
import { ref, onMounted } from "vue";
import axios from "axios";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";
import { useUserStore } from "@/stores/user";
import { useModalStore } from '@/stores/modalState';
import SongDetailModal from "@/components/playlist/MusicDetailModal.vue";
import { useUtilModalStore } from "@/stores/utilModalStore";
import apiClient from "@/api/axiosInstance";

export default {
    components: {
        MusicPlayer,
        SongDetailModal,
    },
    setup() {
        const recommendedPli = ref([]);
        const domesticRankingPli = ref([]);
        const internationalRankingPli = ref([]);
        const userStore = useUserStore();
        const modalStore = useModalStore();
        const selectedSong = ref(null);
        const userProfile = ref(null);
        const showPlaylistModal = ref(false);

        const token = userStore.accessToken;

        const showOptionMenu1 = ref([]);
        const showOptionMenu2 = ref([]);

        // Function to fetch current user's profile
        const fetchUserProfile = async () => {
            try {
                const response = await axios.get("https://api.spotify.com/v1/me", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                userProfile.value = response.data;
                console.log("User Profile:", userProfile.value);
            } catch (error) {
                console.error(
                    "Error fetching user profile:",
                    error.response ? error.response.data : error.message
                );
            }
        };

        const toggleOptionMenu1 = (index) => {
            showOptionMenu1.value[index] = !showOptionMenu1.value[index];
            for (let i = 0; i < showOptionMenu1.value.length; i++) {
                if (i !== index) {
                    showOptionMenu1.value[i] = null;
                }
            }
            for (let i = 0; i < showOptionMenu1.value.length; i++) {
                showOptionMenu2.value[i] = null;
            }
        };

        const toggleOptionMenu2 = (index) => {
            showOptionMenu2.value[index] = !showOptionMenu2.value[index];
            for (let i = 0; i < showOptionMenu2.value.length; i++) {
                if (i !== index) {
                    showOptionMenu2.value[i] = null;
                }
            }
            for (let i = 0; i < showOptionMenu2.value.length; i++) {
                showOptionMenu1.value[i] = null;
            }
        };

        const addPlaylistToMyPlaylists = async (playlistId) => {
            if (!userProfile.value || !userProfile.value.id) {
                alert("사용자 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
                return;
            }

            const userId = userProfile.value.id;
            const checkFollowUrl = `https://api.spotify.com/v1/playlists/${playlistId}/followers/contains?ids=${userId}`;
            const followUrl = `https://api.spotify.com/v1/playlists/${playlistId}/followers`;

            try {
                // Check if the user already follows the playlist
                const checkResponse = await axios.get(checkFollowUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (checkResponse.data[0]) {
                    alert("이미 이 플레이리스트를 팔로우하고 있습니다.");
                    return;
                }

                // Follow the playlist
                await axios.put(
                    followUrl,
                    {},
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json",
                        },
                    }
                );

                alert("플레이리스트가 성공적으로 추가되었습니다!");
            } catch (error) {
                console.error(
                    "Error adding playlist:",
                    error.response ? error.response.data : error.message
                );

                // Handle specific error responses
                if (error.response && error.response.status === 401) {
                    alert("인증에 실패했습니다. 다시 로그인해주세요.");
                } else if (error.response && error.response.status === 403) {
                    alert("이 플레이리스트를 팔로우할 권한이 없습니다.");
                } else {
                    alert("플레이리스트를 추가하는 데 실패했습니다. 다시 시도해주세요.");
                }
            }
        };

        const recommendPliApiUrl =
            "https://api.spotify.com/v1/search?q=playlist+for+reading&type=playlist&offset=0";
        const domesticRankingsApiUrl =
            "https://api.spotify.com/v1/search?q=melon+top100&type=playlist&include_external=audio";
        const internationalRankingsApiUrl =
            "https://api.spotify.com/v1/search?q=BillBoard+Hot+100&type=playlist";

        const openSongDetail = (song) => {
            selectedSong.value = song;
            console.log(selectedSong.value);
            modalStore.openModal("SongDetailModal");
        };


        const selectPlaylist = async (playlistId) => {
            try {
                // 1. 선택한 플레이리스트에 곡 존재 여부 확인
                const response = await apiClient.get(`/api/mypli/playlist/${playlistId}`);

                // 2. 곡이 이미 존재하면 확인 창 표시
                const utilModalStore = useUtilModalStore();
                const tracks = response.data.data.items.map((item) => item.track.id);

                if (tracks.includes(songData.value.id)) {
                utilModalStore.showModal(
                    '플레이리스트 담기',
                    `"${playlists.value.find((p) => p.id === playlistId)?.name}"에 이미 존재하는 곡입니다.`,
                    'already-exist'
                );
                } else {
                await addMusicToPlaylist(playlistId);
                }
            } catch (error) {
                console.log(error);
            }
            showPlaylistModal.value = false; // 선택 후 모달 닫기
        };

        const addMusicToPlaylist = async(playlistId) => {
            const songUri = `spotify:track:${songData.value.id}`;
            try {
                await apiClient.post(`/api/mypli/playlist/${playlistId}`, {
                    uris: [songUri],
                });

                const playlist = playlists.value.find((p) => p.id === playlistId);
                displayNotification(`"${playlist.name}"에 추가되었습니다.`);
                emit('update-playlist');
                emit('update-tracks');
            } catch (error) {
                window.alert("노래를 선택해주세요.");
                console.log(error);
            }
        }

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
                        uri: playlist.uri, // Playlist URI
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

        // Function to fetch domestic rankings
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
                    console.error("No domestic playlist found.");
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

                // Map the top 10 tracks to domesticRankingPli
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
                    albumId: track.track.album.id || "",
                }));

                console.log("Top 10 Songs for Domestic Rankings:", domesticRankingPli.value);
            } catch (error) {
                console.error(
                    "Error fetching domestic playlist or songs:",
                    error.response ? error.response.data : error.message
                );
            }
        };

        // Function to fetch international rankings
        const fetchInternationalRanking = async () => {
            try {
                const response = await axios.get(internationalRankingsApiUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Get the first playlist
                const playlists = response.data.playlists.items;
                const firstPlaylistId = playlists[0]?.id;

                if (!firstPlaylistId) {
                    console.error("No international playlist found.");
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

                // Map the top 10 tracks to internationalRankingPli
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
                    albumId: track.track.album.id || "",
                }));

                console.log(
                    "Top 10 Songs for International Rankings:",
                    internationalRankingPli.value
                );
            } catch (error) {
                console.error(
                    "Error fetching international playlist or songs:",
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
                        offset: { position: 0 },
                    },
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json",
                        },
                    }
                );

                console.log(`Playing playlist: ${playlistUri}`);
                alert("플레이리스트를 재생합니다.");
            } catch (error) {
                console.error(
                    "Error playing playlist:",
                    error.response ? error.response.data : error.message
                );
                alert("플레이리스트를 재생할 수 없습니다. Spotify가 활성화되어 있는지 확인하세요.");
            }
        };

        // Function to add a song to the user's playlist
        const addToPlaylist = (songUri) => {
            // Implement the actual logic to add the song to a user's playlist here
            alert(`노래 ${songUri}을(를) 내 플레이리스트에 추가했습니다.`);
            closeAllMenus1();
            closeAllMenus2();
        };

        // Function to view album information
        const viewAlbumInfo = (albumId) => {
            // Implement the actual logic to view album information, possibly opening a modal
            alert(`앨범 정보 보기: ${albumId}`);
            closeAllMenus1();
            closeAllMenus2();
        };

        // Function to close all option menus
        const closeAllMenus1 = () => {
            showOptionMenu1.value = [];
        };

        const closeAllMenus2 = () => {
            showOptionMenu2.value = [];
        };

        // Function to fetch user profile and get user ID
        // Already implemented above as fetchUserProfile

        // Fetch playlists and rankings on component mount
        onMounted(() => {
            fetchRecommendedPlaylists();
            fetchDomesticRanking();
            fetchInternationalRanking();
            fetchUserProfile(); // Fetch user profile on mount
        });

        return {
            recommendedPli,
            domesticRankingPli,
            internationalRankingPli,
            playSong,
            playPlaylist,
            addPlaylistToMyPlaylists,
            addToPlaylist,
            viewAlbumInfo,
            toggleOptionMenu1,
            toggleOptionMenu2,
            showOptionMenu1,
            showOptionMenu2,
            closeAllMenus1,
            closeAllMenus2,
            modalStore,
            openSongDetail,
            selectedSong,
            userProfile, // Expose userProfile
            selectPlaylist,
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

.option-menu {
    position: absolute;
    background-color: white;
    border: 1px solid #ddd;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    z-index: 1000;
    flex-direction: column;
    width: max-content;
    top: 32%;
    left: 20px;
    display: flex;
}

/* Option Menu 항목 스타일 */
.music-option-menu span {
    padding: 10px;
    cursor: pointer;
    width: 100%;
    text-align: left;
}

.music-option-menu span:hover {
    color: #67de86;
}

.toggle-button {
    cursor: pointer;
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
    transition: background-color 0.3s, color 0.3s;
}

.add-btn:disabled {
    background-color: #e0e0e0;
    color: #a0a0a0;
    cursor: not-allowed;
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
    padding-left:10px;
}

.song-title:hover {
    transform: scale(1.02);
}

.song-artist {
    max-width: 100px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    vertical-align: middle;
}

.song-details {
    cursor: pointer;
    text-align: center;
    position: relative;
}

.song-details:hover {
    cursor: pointer;
}

.song-cover {
    width: 63px;
}

.album-cover {
    width: max-content;
    height: 60px;
    cursor: pointer;
    transition: transform 0.2s;
}

.album-cover:hover {
    transform: scale(1.05);
}
</style>
