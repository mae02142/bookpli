<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";
import { usePlayerStore } from "@/stores/playerStore";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";
import MusicDetailModal from "@/components/playlist/MusicDetailModal.vue";

const route = useRoute();
const userStore = useUserStore();
const playerStore = usePlayerStore();

const category = route.params.category;
const query = route.query.q || "";
const items = ref([]);
const loading = ref(false);
const error = ref(null);

const placeholderImage = "https://via.placeholder.com/150";

// Modal states
const showAlbumModal = ref(false);
const selectedAlbum = ref(null);

const spotifyTypeMap = {
    songs: "track",
    artists: "artist",
    albums: "album",
    playlists: "playlist",
};

const spotifyType = spotifyTypeMap[category];

const filteredItems = computed(() => items.value.filter((item) => item));

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

// Handle album click to play
const handleAlbumClick = async (album) => {
    const albumId = album.id;
    const playUrl = "https://api.spotify.com/v1/me/player/play";

    try {
        // Play the album using Spotify API
        await axios.put(
            playUrl,
            {
                context_uri: `spotify:album:${albumId}`, // Spotify album URI
            },
            {
                headers: {
                    Authorization: `Bearer ${userStore.accessToken}`,
                    "Content-Type": "application/json",
                },
            }
        );
        console.log("앨범 재생 시작:", albumId);
        alert(`앨범 "${album.name}"가 재생됩니다.`);
    } catch (error) {
        if (error.response?.status === 403) {
            if (error.response.data.error.reason === "PREMIUM_REQUIRED") {
                alert("Spotify Premium 계정이 필요합니다.");
            } else {
                alert("Spotify에서 요청을 실행할 수 없습니다. 활성 기기 또는 계정을 확인하세요.");
            }
        } else {
            console.error("앨범 재생 중 오류:", error.response?.data || error.message);
            alert("앨범 재생 중 문제가 발생했습니다.");
        }
    }
};

const getArtists = (artists) => artists.map((artist) => artist.name).join(", ");

const getImage = (item) => {
    if (item.images?.length) return item.images[0].url;
    if (item.album?.images?.length) return item.album.images[0].url;
    return placeholderImage;
};

onMounted(fetchDetails);
</script>

<template>
    <div class="details-page">
        <h1>{{ categoryTitle }}</h1>
        <div v-if="loading" class="loading">로딩 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else-if="filteredItems.length === 0" class="no-results">검색 결과가 없습니다.</div>
        <div v-else class="results-grid">
            <div
                v-for="item in filteredItems"
                :key="item.id"
                class="result-item"
                @click="handleAlbumClick(item)"
            >
                <img :src="getImage(item)" alt="이미지" />
                <p>{{ item.name }}</p>
                <p v-if="item.artists">{{ getArtists(item.artists) }}</p>
            </div>
        </div>
    </div>
    <MusicPlayer />
</template>

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
}

.result-item p {
    margin: 10px 0 5px 0;
}
</style>
