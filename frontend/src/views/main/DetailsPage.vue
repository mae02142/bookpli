<template>
    <div class="details-page">
        <h1>{{ categoryTitle }}</h1>
        <div v-if="loading" class="loading">로딩 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else-if="filteredItems.length === 0" class="no-results">검색 결과가 없습니다.</div>
        <div v-else class="results-grid">
            <div v-for="item in filteredItems" :key="item.id" class="result-item" @click="handleItemClick(item)">
                <div class="image-container">
                    <img :src="getImage(item)" alt="이미지" />
                    <!-- Floating Button -->
                    <button v-if="item.album" class="add-btn" @click.stop="handleAddButtonClick(item)">
                        +
                    </button>
                </div>
                <p>{{ item.name }}</p>
                <p v-if="item.artists">{{ getArtists(item.artists) }}</p>
            </div>

            <!-- 내 플레이리스트 선택 모달 -->
            <div v-if="showPlaylistModal" class="modal-overlay">
                <div class="modal-content">
                    <h3 class="modal-title">내 플레이리스트에 추가</h3>
                    <ul>
                        <li v-for="playlist in playlists" :key="playlist.id" @click="addToPlaylist(playlist.id)" class="playlist-item">
                            {{ playlist.name }}
                        </li>
                    </ul>
                    <button @click="closeModal">닫기</button>
                </div>
            </div>
        </div>
    </div>
    <MusicPlayer />
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";
import { usePlayerStore } from "@/stores/playerStore";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";
import { useUtilModalStore } from "@/stores/utilModalStore";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const playerStore = usePlayerStore();
const utilModalStore = useUtilModalStore();

const category = route.params.category;
const query = route.query.q || "";
const items = ref([]);
const loading = ref(false);
const error = ref(null);
const playlists = ref([]);
const showPlaylistModal = ref(false);
const showDetailModal = ref(false);
const selectedAlbum = ref(null);
const selectedTrackUri = ref(null); // 추가된 부분: selectedTrackUri 선언
const userProfile = ref(null);

const placeholderImage = "https://via.placeholder.com/150";

const spotifyTypeMap = {
    songs: "track",
    artists: "artist",
    albums: "album",
    playlists: "playlist",
};

const spotifyType = spotifyTypeMap[category];

const filteredItems = computed(() => items.value.filter((item) => item));

const fetchUserProfile = async () => {
    try {
        const response = await axios.get("https://api.spotify.com/v1/me", {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });
        userProfile.value = response.data;
        console.log("User Profile:", userProfile.value);
    } catch (error) {
        console.error("Error fetching user profile:", error);
    }
};

// Fetch details for the selected category and query
const fetchDetails = async () => {
    if (!query || !spotifyType) {
        error.value = "잘못된 검색 요청입니다.";
        return;
    }

    loading.value = true;
    error.value = null;

    try {
        const response = await axios.get("https://api.spotify.com/v1/search", {
            headers: {
                Authorization: `Bearer ${userStore.accessToken}`,
            },
            params: {
                q: query,
                type: spotifyType,
                limit: 50,
            },
        });

        items.value = response.data[`${spotifyType}s`]?.items || [];
    } catch (err) {
        console.error(err);
        error.value = "데이터를 불러오는 중 오류가 발생했습니다.";
    } finally {
        loading.value = false;
    }
};

// Handle item click for tracks, albums, playlists, and artists
const handleItemClick = async (item) => {
    if (spotifyType === "track") {
        // Play the selected track
        const trackUri = item.uri;
        if (trackUri) {
            await playTrack(trackUri);
        }
    } else if (spotifyType === "album") {
        // Play the selected album
        const albumId = item.id;
        if (albumId) {
            await playAlbum(albumId);
        }
    } else if (spotifyType === "artist") {
        // Fetch and display artist's albums
        const artistId = item.id;
        if (artistId) {
            await goToArtistDetail(artistId);
        }
    } else {
        // Play content for playlists
        const uri = item.uri;
        if (uri) {
            playerStore.playContent(uri);
        }
    }
};

// Play the selected track
const playTrack = async (trackUri) => {
    const playUrl = "https://api.spotify.com/v1/me/player/play";
    try {
        await axios.put(
            playUrl,
            {
                uris: [trackUri],
            },
            {
                headers: {
                    Authorization: `Bearer ${userStore.accessToken}`,
                    "Content-Type": "application/json",
                },
            }
        );
        console.log("트랙 재생 시작:", trackUri);
    } catch (error) {
        console.error("트랙 재생 중 오류:", error.response?.data || error.message);
        utilModalStore.showModal("트랙 재생하기", `트랙을 재생하는데 오류가 발생하였습니다.`, "warning");
    }
};

// Play the selected album
const playAlbum = async (albumId) => {
    const playUrl = "https://api.spotify.com/v1/me/player/play";

    try {
        await axios.put(
            playUrl,
            {
                context_uri: `spotify:album:${albumId}`,
            },
            {
                headers: {
                    Authorization: `Bearer ${userStore.accessToken}`,
                    "Content-Type": "application/json",
                },
            }
        );
        console.log("앨범 재생 시작:", albumId);
    } catch (error) {
        if (error.response?.status === 403) {
            if (error.response.data.error.reason === "PREMIUM_REQUIRED") {
                console.log(error.response);
            } else {
                console.log(error.response);
            }
        } else {
            console.error("앨범 재생 중 오류:", error.response?.data || error.message);
            utilModalStore.showModal("앨범 재생하기", `앨범을 재생할때 오류가 발생하였습니다.`, "warning");
        }
    }
};

// Fetch the artist's albums
const fetchArtistAlbums = async (artistId) => {
    loading.value = true;
    error.value = null;

    try {
        const response = await axios.get(
            `https://api.spotify.com/v1/artists/${artistId}/albums`,
            {
                headers: {
                    Authorization: `Bearer ${userStore.accessToken}`,
                },
                params: {
                    include_groups: "album",
                    market: "US",
                    limit: 50,
                },
            }
        );

        items.value = response.data.items || [];
        console.log("Fetched albums:", items.value);
    } catch (error) {
        console.error("Error fetching artist's albums:", error.response?.data || error.message);
        error.value = "아티스트의 앨범을 불러오는 중 오류가 발생했습니다.";
    } finally {
        loading.value = false;
    }
};

const getArtists = (artists) => artists.map((artist) => artist.name).join(", ");

const getImage = (item) => {
    if (item.images?.length) return item.images[0].url;
    if (item.album?.images?.length) return item.album.images[0].url;
    return placeholderImage;
};

const handleAddButtonClick = async (item) => {
    if (!userProfile.value) {
        await fetchUserProfile(); // Fetch the user profile if not loaded
    }

    if (spotifyType === "track") {
        selectedTrackUri.value = item.uri;
        await fetchUserPlaylists();
        showPlaylistModal.value = true;
    } else if (spotifyType === "playlist") {
        await followPlaylist(item.id);
    }
};

// 내 플레이리스트 불러오기
const fetchUserPlaylists = async () => {
    if (!userProfile.value) {
        console.error("User profile not loaded.");
        return;
    }

    try {
        const userId = userProfile.value.id; // 내 Spotify ID
        const response = await axios.get("https://api.spotify.com/v1/me/playlists", {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });

        // 내가 만든 플레이리스트만 필터링
        playlists.value = response.data.items.filter((playlist) => playlist.owner.id === userId);
    } catch (error) {
        console.error("플레이리스트를 불러오는 중 오류 발생:", error);
    }
};

// 트랙을 플레이리스트에 추가하기
const addToPlaylist = async (playlistId) => {
    try {
        await axios.post(
            `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
            { uris: [selectedTrackUri.value] },
            { headers: { Authorization: `Bearer ${userStore.accessToken}` } }
        );
        utilModalStore.showModal("플리에 추가하기", `내 플리에 추가되었습니다.`, "warning");
        closeModal();
    } catch (error) {
        console.error("트랙 추가 오류:", error);
        utilModalStore.showModal("플리에 추가하기", `플리에 추가하는데 오류가 발생하였습니다.`, "warning");
    }
};

// 플레이리스트 팔로우하기
const followPlaylist = async (playlistId) => {
    try {
        await axios.put(
            `https://api.spotify.com/v1/playlists/${playlistId}/followers`,
            {},
            { headers: { Authorization: `Bearer ${userStore.accessToken}` } }
        );
        utilModalStore.showModal("플리 팔로우하기", `플리를 팔로우합니다.`, "warning");
    } catch (error) {
        console.error("플레이리스트 팔로우 오류:", error);
        utilModalStore.showModal("플리 팔로우하기", `플리에 팔로우하는데 오류가 발생하였습니다.`, "warning");
    }
};

const goToArtistDetail = (artistId) => {
    if (!artistId) {
        console.warn("Invalid artistId:", artistId);
        return;
    }
    router.push({ path: `/artist/${artistId}` });
};

// 모달 닫기
const closeModal = () => {
    showPlaylistModal.value = false;
    selectedTrackUri.value = null;
};

onMounted(async () => {
    await fetchUserProfile(); // Fetch user profile first
    fetchDetails();
});</script>

<style scoped>
.details-page {
    max-width: 1200px;
    margin: auto;
    padding: 1rem;
}

.loading,
.error,
.no-results {
    text-align: center;
    font-size: 18px;
    color: gray;
}

.results-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}

.result-item {
    width: 200px;
    text-align: center;
    cursor: pointer;
}

.result-item img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    border-radius: 5px;
}

.result-item p {
    margin: 10px 0 5px 0;
}

.image-container {
    position: relative;
    display: inline-block;
}

.add-btn {
    position: absolute;
    bottom: 10px;
    right: 10px;
    background-color: rgba(0, 0, 0, 0.6);
    color: #fff;
    border: none;
    border-radius: 50%;
    width: 25px;
    height: 35px;
    font-size: 20px;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s;
}

.add-btn:hover {
    background-color: rgba(0, 0, 0, 0.8);
}

.result-item img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    border-radius: 5px;
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
