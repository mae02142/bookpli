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
                        <div v-for="song in songs" :key="song.id" class="result-item">
                            <img :src="song.album.images[0]?.url || placeholderImage" alt="앨범 커버"/>
                            <p class="track-title">{{ song.name }}</p>
                            <p>{{ song.artists.map(artist => artist.name).join(", ") }}</p>
                        </div>
                    </div>
                </section>

                <section class="search-section" v-if="artists.length">
                    <div class="section-header">
                        <h1 class="section-title">아티스트</h1>
                        <button class="view-more-button" @click="goToDetails('artists')">더보기</button>
                    </div>
                    <hr>
                    <div class="results-grid horizontal-scroll">
                        <div v-for="artist in artists" :key="artist.id" class="result-item">
                            <img :src="artist.images[0]?.url || placeholderImage" alt="아티스트 이미지" />
                            <p class="track-title">{{ artist.name }}</p>
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
                        <div v-for="album in albums" :key="album.id" class="result-item">
                            <img :src="album.images[0]?.url || placeholderImage" alt="앨범 커버" />
                            <p class="track-title">{{ album.name }}</p>
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
                        <div 
                            v-for="playlist in playlists.filter(p => p && p.images && p.images.length > 0 && p.name)" 
                            :key="playlist.id" 
                            class="result-item"
                        >
                            <img :src="playlist.images[0]?.url || placeholderImage" alt="플레이리스트 이미지" />
                            <p class="track-title">{{ playlist.name }}</p>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
    <MusicPlayer/>
</template>


<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";

const route = useRoute();
const router = useRouter(); // useRouter를 사용하여 라우터 조작

const query = route.query.q || "";
const type = route.query.type || "music";

const songs = ref([]);
const artists = ref([]);
const albums = ref([]);
const playlists = ref([]);
const loading = ref(false);
const error = ref(null);

const placeholderImage = "https://via.placeholder.com/150";
const userStore = useUserStore();

const searchMusic = async () => {
    if (!query) return;
    loading.value = true;
    error.value = null;

    try {
        const response = await axios.get("https://api.spotify.com/v1/search", {
            headers: {
                Authorization: `Bearer ${userStore.accessToken}`,
            },
            params: {
                q: query,
                type: "track,artist,album,playlist",
                limit: 10,
            },
        });
        console.log(response.data);
        songs.value = response.data.tracks.items;
        artists.value = response.data.artists.items;
        albums.value = response.data.albums.items;
        playlists.value = response.data.playlists.items;
    } catch (err) {
        error.value = "검색 중 오류가 발생했습니다.";
    } finally {
        loading.value = false;
    }
};

// goToDetails 함수 수정
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
    padding-bottom: 10px;
}

.horizontal-scroll::-webkit-scrollbar {
    height: 8px;
    background-color: #f1f1f1;
}

.horizontal-scroll::-webkit-scrollbar-thumb {
    background: #cfcfcf;
    border-radius: 4px;
}

.horizontal-scroll::-webkit-scrollbar-thumb:hover {
    background: #a0a0a0;
}

.result-item {
    flex: 0 0 auto;
    width: 18.5%;
    text-align: center;
    padding: 0.3rem;
}

.result-item img {
    width: 100%;
    height: 200px;
    object-fit: cover;
}

.result-item p {
    margin: 10px 0 5px 0;
}

.track-title {
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
    background-color: transparent;
    font-size: 14px;
    cursor: pointer;
    margin-left: auto;
    padding-right: 20px;
    vertical-align: baseline;
}

.view-more-button:hover {
    text-decoration: underline;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>