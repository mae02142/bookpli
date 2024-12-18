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
                            <td class="song-details" style="min-width: 20px; text-align: center; position: relative;" @click="toggleOptionMenu1(index)">
                                ⋮
                                <div v-show="showOptionMenu1[index]" class="option-menu">
                                    <span style="padding-bottom: 5px;" @click="playSong(song.uri)">재생하기</span>
                                    <span style="padding-bottom: 5px;" @click="togglePlaylistModal(song.uri, $event)">내 플리에 추가하기</span>
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
                            <td class="song-details" style="min-width: 20px; text-align: center; position: relative;" @click="toggleOptionMenu2(index)">
                                ⋮
                                <div v-show="showOptionMenu2[index]" class="option-menu">
                                    <span style="padding-bottom: 5px;" @click="playSong(song.uri)">재생하기</span>
                                    <span style="padding-bottom: 5px;" @click="togglePlaylistModal(song.uri, $event)">내 플리에 추가하기</span>
                                    <span style="padding-bottom: 5px;" @click="openSongDetail(song)">앨범 정보 보기</span>
                                </div>
                            </td>
                        </tr>
                    </table>
                    <!-- 플레이리스트 추가 모달 -->
                    <div v-if="showPlaylistModal" class="modal-overlay" @click.self="closeModal">
                        <div class="modal-content">
                            <h3 class="modal-title">내 플레이리스트에 추가</h3>
                            <ul>
                                <li 
                                    v-for="playlist in playlists" 
                                    :key="playlist.id" 
                                    @click="addToPlaylist(playlist.id)"
                                    class="playlist-item"
                                >
                                    {{ playlist.name }}
                                </li>
                            </ul>
                            <button class="close-button" @click="closeModal">닫기</button>
                        </div>
                    </div>
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
import { useAuthStore } from "@/stores/auth";
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
        const selectedTrack = ref(null); // 선택된 트랙 ID 저장
        const playlistModalPosition = ref({}); // 모달 위치
        const utilModalStore = useUtilModalStore();

        const token = userStore.accessToken;

        const showOptionMenu1 = ref([]);
        const showOptionMenu2 = ref([]);
        const playlists = ref([]);


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

        const getMyPlaylist = async () => {
            const authStore = useAuthStore(); 
            try {
                const response = await apiClient.get("api/mypli");
                playlists.value = response.data.data.items
                .filter((item) => item.owner.id === authStore.user.spotifyId)
                .map((item) => ({
                    id: item.id,
                    name: item.name,
                }));
                console.log(playlists.value);
            } catch (error) {
                console.log(error);
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
                    utilModalStore.showModal("플리에 추가하기", `이미 팔로우하고 있는 플리입니다.`, "already-exist");
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

                utilModalStore.showModal("플리에 추가하기", `내 플리에 추가되었습니다.`, "add-playlist");
            } catch (error) {
                console.error(
                    "Error adding playlist:",
                    error.response ? error.response.data : error.message
                );
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

        const togglePlaylistModal = (trackUri, event) => {
            selectedTrack.value = trackUri; // Assign the passed trackUri to selectedTrack
            showPlaylistModal.value = !showPlaylistModal.value;

            if (showPlaylistModal.value) {
                getMyPlaylist(); // Fetch playlist data
                const rect = event.target.getBoundingClientRect()
                playlistModalPosition.value = {
                    top: `${rect.top + window.scrollY + 30}px`,
                    left: `${rect.left + window.scrollX + 20}px`,
                };
            }
        };

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
                utilModalStore.showModal("트랙 재생", `트랙 재생에 실패하였습니다.`, "warnig");
            }
        };

        // Function to play a playlist from the start using its URI
        const playPlaylist = async (playlistUri) => {
            const playUrl = "https://api.spotify.com/v1/me/player/play";

            try {
                const devices = await getActiveDevices();

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
            } catch (error) {
                console.error(
                    "Error playing playlist:",
                    error.response ? error.response.data : error.message
                );
                utilModalStore.showModal("플리 재생 실패", `플리를 재생하는데 실패하였습니다.`, "warning");
            }
        };

        // Function to add a song to the user's playlist
        const addToPlaylist = async (playlistId) => {
            console.log(token)
            console.log(selectedTrack.value)
            if (!selectedTrack.value) {
                utilModalStore.showModal("플리에 추가하기", `플레이리스트를 선택하세요.`, "double-check");
                return;
            }
            if (!token) {
                modalStore.showModal(
                    '로그인 페이지로 이동',
                    '로그인 후 이용해주세요.',
                    {
                        onConfirm: () => {
                        window.location.href = "/login";
                        },
                        onCancel: () => {
                        isMusicSection.value = false;
                        },
                    }
                );
            }

            try {
                await axios.post(
                    `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
                    { uris: [selectedTrack.value] }, // Use selectedTrack here
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json",
                        },
                    }
                );
                utilModalStore.showModal("플리에 추가하기", `내 플리에 추가되었습니다.`, "add-pli");
                showPlaylistModal.value = false; // Close the modal
            } catch (error) {
                console.error("Error adding to playlist:", error);
                utilModalStore.showModal("플리에 추가하기", `내 플리에 추가하는데 실패했습니다.`, "warning");
            }
        };

        // Function to view album information
        const viewAlbumInfo = (albumId) => {
            // Implement the actual logic to view album information, possibly opening a modal
            closeAllMenus1();
            closeAllMenus2();
        };

        const closeModal = () => {
            showPlaylistModal.value = false; // 모달 상태를 false로 설정
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
            fetchUserProfile();
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
            userProfile,
            togglePlaylistModal,
            showPlaylistModal,
            getMyPlaylist,
            playlists,
            playlistModalPosition,
            closeModal,
        };
    },
};
</script>
<style scoped>
/* 기존 스타일 유지 */
body {
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

.playlist-option {
    background-color: white;
    border: 1px solid #ddd;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    z-index: 1001;
    flex-direction: column;
    width: max-content;
    top: 32%;
    left: 20px;
    display: flex;
}

.option-menu span {
    padding: 10px;
    cursor: pointer;
    width: 100%;
    text-align: left;
}

.option-menu span:hover {
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
    color: #1db954;
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

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 300px;
    text-align: center;
}

.modal-title {
    padding-bottom: 20px;
    font-size: 1.5rem;
    font-weight: bolder;
}

.playlist-item {
    list-style: none;
    padding: 10px;
    cursor: pointer;
    border-bottom: 1px solid #ddd;
}

.playlist-item:hover {
    background-color: #f0f0f0;
}

.close-button {
    margin-top: 10px;
    padding: 5px 10px;
    background-color: #1db954;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.close-button:hover {
    background-color: #1ed760;
}
</style>
