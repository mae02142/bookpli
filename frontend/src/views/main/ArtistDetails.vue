<template>
    <section class="artist-info">
        <div 
            class="artist-background"
            :style="{ backgroundImage: `linear-gradient(to bottom, rgba(0, 0, 0, 0) 50%, rgba(255, 255, 255, 0) 70%, rgba(255, 255, 255, 1) 100%), url(${cover || placeholderImage})` }"
        >
        </div>
        <div class="artist-content">
            <h1 class="artist-name">{{ artist.name }}</h1>
            <p>{{ artist.biography || "아티스트 소개 정보가 없습니다." }}</p>
            <button @click="playArtistTopTracks" class="play-button">모든 곡 재생</button>
        </div>
    </section>
    <div class="artist-details">
        <div v-if="loading" class="loading">로딩 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else>
            <!-- 모든 앨범 -->
            <section v-if="albums.length" class="albums-section">
                <h2 class="artist-albums">모든 앨범</h2>
                <div class="albums-grid">
                    <div 
                        v-for="album in albums" 
                        :key="album.id" 
                        class="album-item" 
                        @click="playAlbum(album.id)"
                    >
                        <img :src="album.images?.[0]?.url || placeholderImage" alt="앨범 커버" />
                        <p>{{ album.name }}</p>
                    </div>
                </div>
            </section>

            <!-- 인기 트랙 -->
            <div class="tracks-list">
                <h2 class="artist-tracks">인기 트랙</h2>
                <div 
                v-for="track in tracks" 
                :key="track.id" 
                class="track-item" 
                @click="playTrack(track.uri)">
                    <!-- 이미지 -->
                    <img :src="track.album.images?.[0]?.url || placeholderImage" alt="앨범 커버" class="track-image" />
                        <!-- 곡 정보 -->
                        <p class="track-name">{{ track.name }}</p>
                        <p class="track-artist">{{ track.artists.map(artist => artist.name).join(", ") }}</p>
                        <!-- 앨범 -->
                        <p class="track-album">{{ track.album.name }}</p>
                        <p class="track-options" @click.stop="openPlaylistModal(track.uri)">⋮</p>
                </div>
                <div v-if="showPlaylistModal" class="artist-modal-overlay" @click.self="closeModal">
                    <div class="artist-modal-content">
                        <h3 class="artist-modal-title">내 플레이리스트에 추가</h3>
                        <ul>
                            <li 
                                v-for="playlist in playlists" 
                                :key="playlist.id" 
                                @click="addToPlaylist(playlist.id)"
                                class="artist-playlist-item"
                            >
                                {{ playlist.name }}
                            </li>
                        </ul>
                        <button class="artist-close-button" @click="closeModal">닫기</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <MusicPlayer/>
</template>


<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";
import { useUtilModalStore } from "@/stores/utilModalStore";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";

const route = useRoute();
const userStore = useUserStore();
const artistId = route.params.id;
const utilModalStore = useUtilModalStore();

const artist = ref({});
const albums = ref([]);
const tracks = ref([]);
const cover = ref(""); // 이미지 URL 상태
const loading = ref(false);
const error = ref(null);

const placeholderImage = "https://via.placeholder.com/150";

const showPlaylistModal = ref(false); // 모달 표시 여부
const playlists = ref([]); // 사용자 플레이리스트 목록
const selectedTrackUri = ref(null); // 선택된 트랙 URI

// 모달 열기
const openPlaylistModal = async (trackUri) => {
    selectedTrackUri.value = trackUri; // 선택된 트랙 저장
    await fetchUserPlaylists();
    showPlaylistModal.value = true;
};

// 모달 닫기
const closeModal = () => {
    showPlaylistModal.value = false;
    selectedTrackUri.value = null;
};

// 사용자 플레이리스트 불러오기
const fetchUserPlaylists = async () => {
    try {
        const response = await axios.get("https://api.spotify.com/v1/me/playlists", {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });

        if (!userStore.userProfile || !userStore.userProfile.id) {
            await fetchUserProfile();
        }
        // 현재 사용자 ID 가져오기
        const currentUserId = userStore.userProfile.id; // 또는 미리 사용자 ID를 가져온 변수 사용

        // 내가 생성한 플레이리스트만 필터링
        playlists.value = response.data.items.filter(
            (playlist) => playlist.owner.id === currentUserId
        );
    } catch (error) {
        console.log(error)
        utilModalStore.showModal(
            "플리에 추가하기",
            "내 플리를 불러오는데 실패하였습니다.",
            "warning"
        );
    }
};

// 선택된 플레이리스트에 트랙 추가
const addToPlaylist = async (playlistId) => {
    try {
        // 1. 선택된 플레이리스트의 트랙 목록 가져오기
        const response = await axios.get(
            `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
            {
                headers: { Authorization: `Bearer ${userStore.accessToken}` },
            }
        );

        // 2. 중복 확인
        const existingTracks = response.data.items.map((item) => item.track.uri);
        if (existingTracks.includes(selectedTrackUri.value)) {
            utilModalStore.showModal(
                "플리에 추가하기",
                "이미 플레이리스트에 추가된 곡입니다.",
                "already-exist"
            );
            closeModal();
            return;
        }

        // 3. 중복이 아니라면 추가
        await axios.post(
            `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
            { uris: [selectedTrackUri.value] },
            { headers: { Authorization: `Bearer ${userStore.accessToken}` } }
        );

        utilModalStore.showModal(
            "플리에 추가하기",
            "내 플레이리스트에 성공적으로 추가되었습니다.",
            "add-pli"
        );
        closeModal();
    } catch (error) {
        utilModalStore.showModal(
            "플리에 추가하기",
            "플레이리스트에 추가하는 중 오류가 발생했습니다.",
            "warning"
        );
    }
};


// 아티스트 정보, 앨범, 인기 트랙 가져오기
const fetchArtistDetails = async () => {
    loading.value = true;
    try {
        // 아티스트 정보
        const artistResponse = await axios.get(`https://api.spotify.com/v1/artists/${artistId}`, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });
        artist.value = artistResponse.data;

        // 안전하게 cover 이미지 설정
        cover.value = artist.value.images?.[0]?.url || placeholderImage;

        // 아티스트 앨범
        const albumsResponse = await axios.get(`https://api.spotify.com/v1/artists/${artistId}/albums`, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
            params: { include_groups: "album,single", limit: 20 },
        });
        albums.value = albumsResponse.data.items;

        // 아티스트 인기 트랙
        const tracksResponse = await axios.get(`https://api.spotify.com/v1/artists/${artistId}/top-tracks`, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
            params: { market: "US" },
        });
        tracks.value = tracksResponse.data.tracks;
    } catch (err) {
        utilModalStore.showModal("플리에 추가하기", `데이터를 불러오는 중 오류가 발생했습니다.`, "warning");
        error.value = "데이터를 불러오는 중 오류가 발생했습니다.";
    } finally {
        loading.value = false;
    }
};

// 트랙 재생
const playTrack = async (trackUri) => {
    if (!trackUri) return;
    try {
        await axios.put(
            "https://api.spotify.com/v1/me/player/play",
            { uris: [trackUri] },
            { headers: { Authorization: `Bearer ${userStore.accessToken}` } }
        );
    } catch (error) {
        utilModalStore.showModal("플리에 추가하기", `트랙 재생 오류`, "warning");
    }
};

// 앨범 전체 재생
const playAlbum = async (albumId) => {
    try {
        await axios.put(
            "https://api.spotify.com/v1/me/player/play",
            { context_uri: `spotify:album:${albumId}` },
            { headers: { Authorization: `Bearer ${userStore.accessToken}` } }
        );
    } catch (error) {
        utilModalStore.showModal("플리에 추가하기", `트랙 재생 오류`, "warning");
    }
};

const fetchUserProfile = async () => {
    try {
        const response = await axios.get("https://api.spotify.com/v1/me", {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });
        userStore.userProfile = response.data; // 사용자 정보 저장
    } catch (error) {
        console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        utilModalStore.showModal(
            "사용자 정보 오류",
            "사용자 정보를 불러올 수 없습니다.",
            "warning"
        );
    }
};

// 아티스트 모든 곡 재생
const playArtistTopTracks = async () => {
    const trackUris = tracks.value.map(track => track.uri);
    if (!trackUris.length) return;

    try {
        await axios.put(
            "https://api.spotify.com/v1/me/player/play",
            { uris: trackUris },
            { headers: { Authorization: `Bearer ${userStore.accessToken}` } }
        );
    } catch (error) {
        utilModalStore.showModal("플리에 추가하기", `트랙 재생 오류`, "warning");
    }
};

onMounted(async () => {
    await fetchUserProfile(); // 사용자 프로필 미리 불러오기
    fetchArtistDetails(); // 아티스트 정보 불러오기
});
</script>


<style scoped>
.artist-info {
    position: relative;
    text-align: center;
    height: 1000px; /* 섹션 높이 설정 */
    color: white;
    overflow: hidden; /* 백그라운드가 영역을 벗어나지 않도록 */
}

.tracks-list {
    display: flex;
    flex-direction: column;
}

.track-item {
    display: flex;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #333;
    cursor: pointer;
    transition: background-color 0.3s;
}

.track-image{
    padding-right: 20px;
    vertical-align: middle;
}

.track-name {
    width:33%;
    vertical-align: middle;
}

.track-item:hover{
    transition: 0.15s;
    color: #1db954;
    cursor: pointer;
}

.track-artist {
    width:33%;
    vertical-align: middle;
}

.track-album {
    width:33%;
    vertical-align: middle;
}

.artist-details {
    max-width: 1200px;
    margin: auto;
    padding: 20px;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    position: relative;
    font-weight: bolder;
}

.artist-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    z-index: -1; /* 콘텐츠 뒤로 이동 */
}

.artist-content {
    position: relative;
    z-index: 1; /* 콘텐츠가 백그라운드 위에 표시되도록 */
    padding: 20px;
    padding-top: 700px;
    text-align: left;
}

.artist-name {
    font-size: 3rem;
    font-weight: bolder;
}

.artist-albums {
    padding-bottom: 20px;
    font-size: 2rem;
    font-weight: bolder;
}

.track-image {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 4px;
}

.artist-tracks {
    color: black;
    padding-top: 50px;
    padding-bottom: 20px;
    font-size: 2rem;
    font-weight: bolder;
}

.albums-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    padding-left:20px;
}

.album-item img {
    width: 100%;
    height: 150px;
    border-radius: 8px;
    object-fit: cover;
}

.album-item {
    width: 150px;
    cursor: pointer;
    text-align: center;
}

.play-button {
    margin-top: 20px;
    padding: 10px 20px;
    background-color: #1db954;
    color: white;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 1rem;
}

.play-button:hover {
    background-color: #1ed760;
}

.artist-modal-overlay {
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

.artist-modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 300px;
    text-align: center;
}

.artist-modal-title {
    padding-bottom: 20px;
    font-size: 1.5rem;
    font-weight: bolder;
}

.artist-playlist-item {
    list-style: none;
    padding: 10px;
    cursor: pointer;
    border-bottom: 1px solid #ddd;
}

.artist-playlist-item:hover {
    background-color: #f0f0f0;
}

.artist-close-button {
    margin-top: 10px;
    padding: 5px 10px;
    background-color: #1db954;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.artist-close-button:hover {
    background-color: #1ed760;
}
</style>
