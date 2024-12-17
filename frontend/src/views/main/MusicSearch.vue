<template>
    <div class="music-search">
        <div v-if="loading" class="loading">검색 중...</div>
        <div v-else>
            <div v-if="error" class="error">{{ error }}</div>
            <div v-else>
                <section class="search-section" v-if="songs.length">
                    <div class="section-header">
                        <h1 class="section-title">트랙</h1>
                        <button class="view-more-button" @click="goToDetails('songs')">더보기</button>
                    </div>
                    <hr>
                    <div class="results-grid horizontal-scroll">
                        <div v-for="song in songs" :key="song.id" class="result-item" @click="playTrack(song.uri)">
                            <img :src="song.album.images[0]?.url || placeholderImage" alt="앨범 커버" />
                            <p class="track-title">{{ song.name }}</p>
                            <p>{{ song.artists.map(artist => artist.name).join(", ") }}</p>
                        </div>
                    </div>
                </section>

                <section class="search-section" v-if="artists.length">
                    <div class="section-header">
                        <h2 class="section-title">아티스트</h2>
                        <button class="view-more-button" @click="goToDetails('artists')">더보기</button>
                    </div>
                    <hr>
                    <div class="results-grid horizontal-scroll">
                        <div v-for="artist in artists" :key="artist.id" class="result-item" @click="fetchArtistAlbums(artist.id)">
                            <img :src="artist.images[0]?.url || placeholderImage" alt="아티스트 이미지" />
                            <p class="artist-title">{{ artist.name }}</p>
                        </div>
                    </div>
                </section>

                <section class="search-section" v-if="albums.length">
                    <div class="section-header">
                        <h1 class="section-title">앨범</h1>
                        <button class="view-more-button" @click="goToDetails('albums')">더보기</button>
                    </div>
                    <hr>
                    <div class="results-grid horizontal-scroll">
                        <div v-for="album in albums" :key="album.id" class="result-item" @click="playAlbum(album.id)">
                            <img :src="album.images[0]?.url || placeholderImage" alt="앨범 커버" />
                            <p class="album-title">{{ album.name }}</p>
                            <p>{{ album.artists.map(artist => artist.name).join(", ") }}</p>
                        </div>
                    </div>
                </section>

                <section class="search-section" v-if="playlists.length">
                    <div class="section-header">
                        <h1 class="section-title">플레이리스트</h1>
                        <button class="view-more-button" @click="goToDetails('playlists')">더보기</button>
                    </div>
                    <hr>
                    <div class="results-grid horizontal-scroll">
                        <div v-for="playlist in playlists" :key="playlist.id" class="result-item" @click="playPlaylist(playlist.uri)">
                            <img :src="playlist.images[0]?.url || placeholderImage" alt="플레이리스트 이미지" />
                            <p class="playlist-title">{{ playlist.name }}</p>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
    <MusicPlayer />
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const query = route.query.q || "";
const type = route.query.type || "music";

const songs = ref([]);
const artists = ref([]);
const albums = ref([]);
const playlists = ref([]);
const loading = ref(false);
const error = ref(null);

const placeholderImage = "https://via.placeholder.com/150";

// Spotify API search
const filterValidItems = (items) => {
    return items?.filter((item) => item && item.id) || [];
};

// Spotify API 데이터 필터링
const searchMusic = async () => {
    if (!query) return;
    loading.value = true;
    error.value = null;

    try {
        const response = await axios.get("https://api.spotify.com/v1/search", {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
            params: { q: query, type: "track,artist,album,playlist", limit: 10 },
        });

        // 필터링된 데이터 저장
        songs.value = filterValidItems(response.data.tracks.items);
        artists.value = filterValidItems(response.data.artists.items);
        albums.value = filterValidItems(response.data.albums.items);
        playlists.value = filterValidItems(response.data.playlists.items);
    } catch (err) {
        error.value = "검색 중 오류가 발생했습니다.";
    } finally {
        loading.value = false;
    }
};

// Track 재생
const playTrack = async (trackUri) => {
    try {
        await axios.put("https://api.spotify.com/v1/me/player/play", { uris: [trackUri] }, {
            headers: { Authorization: `Bearer ${userStore.accessToken}`, "Content-Type": "application/json" },
        });
        console.log("트랙 재생 시작:", trackUri);
    } catch (error) {
        console.error("Track 재생 오류:", error.message);
    }
};

// Album 재생
const playAlbum = async (albumId) => {
    try {
        await axios.put("https://api.spotify.com/v1/me/player/play", { context_uri: `spotify:album:${albumId}` }, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });
        console.log("앨범 재생 시작:", albumId);
    } catch (error) {
        console.error("Album 재생 오류:", error.message);
    }
};

// Playlist 재생
const playPlaylist = async (playlistUri) => {
    try {
        await axios.put("https://api.spotify.com/v1/me/player/play", { context_uri: playlistUri }, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });
        console.log("플레이리스트 재생 시작:", playlistUri);
    } catch (error) {
        console.error("Playlist 재생 오류:", error.message);
    }
};

// Artist's 앨범 조회
const fetchArtistAlbums = async (artistId) => {
    try {
        const response = await axios.get(`https://api.spotify.com/v1/artists/${artistId}/albums`, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
            params: { include_groups: "album", market: "US", limit: 10 },
        });
        albums.value = response.data.items;
    } catch (error) {
        console.error("아티스트 앨범 조회 오류:", error.message);
    }
};

// 상세 페이지 이동
const goToDetails = (category) => {
    router.push({ path: `/details/${category}`, query: { q: query } });
};

onMounted(() => {
    if (type === "music") {
        searchMusic();
    }
});
</script>

<style scoped>
.music-search {
    max-width: 1200px;
    margin: auto;
    padding: 0.5rem;
}

.section-title {
    font-size: 3rem;
    margin-bottom: 1rem;
}

.search-section {
    padding: 1.5rem;
}

.loading {
    font-size: 18px;
    text-align: center;
}

.error {
    color: red;
    text-align: center;
}

.results-grid {
    display: flex;
    gap: 20px;
}

.horizontal-scroll {
    overflow-x: auto;
    flex-wrap: nowrap;
    -webkit-overflow-scrolling: touch;
}

.horizontal-scroll::-webkit-scrollbar {
    height: 8px;
    background-color: #ffff;
}

.horizontal-scroll::-webkit-scrollbar-thumb {
    background: #ffff;
    border-radius: 4px;
}

.horizontal-scroll::-webkit-scrollbar-thumb:hover {
    background: #ffff;
}

.result-item {
    flex: 0 0 auto;
    width: 18.5%;
    text-align: center;
}

.result-item:hover{
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

.track-title, .artist-title, .album-title, .playlist-title {
    font-size: 1.2rem;
    font-weight: bold;
}

.no-results {
    text-align: center;
    font-size: 18px;
    margin-top: 20px;
}

.view-more-button {
    border: none;
    background-color: #ffff;
    font-size: 14px;
    cursor: pointer;
    margin-left: auto;
    padding-right: 20px;
    vertical-align: baseline;
}

.view-more-button:hover {
    background-color: #ffff;
    text-decoration: underline;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
