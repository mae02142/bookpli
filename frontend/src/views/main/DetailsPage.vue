<template>
    <div class="details-page">
    <h1>{{ categoryTitle }}</h1>
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="items.length === 0" class="no-results">검색 결과가 없습니다.</div>
    <div v-else class="results-grid">
        <div v-for="item in items" :key="item.id" class="result-item">
        <img :src="getImage(item)" alt="이미지" />
        <p>{{ item.name }}</p>
        <p v-if="item.artists">{{ getArtists(item.artists) }}</p>
        </div>
    </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const userStore = useUserStore();

const category = route.params.category; // 카테고리 정보
const query = route.query.q || ""; // 검색어
const items = ref([]); // 출력할 데이터
const loading = ref(false);
const error = ref(null);

const placeholderImage = "https://via.placeholder.com/150";

// 카테고리별 제목 설정
const categoryTitle = {
    songs: "트랙",
    artists: "아티스트",
    albums: "앨범",
    playlists: "플레이리스트",
}[category] || "검색 결과";

// 데이터 가져오기
const fetchDetails = async () => {
    if (!query || !category) return;

    loading.value = true;
    error.value = null;

    try {
    const response = await axios.get("https://api.spotify.com/v1/search", {
        headers: {
        Authorization: `Bearer ${userStore.accessToken}`,
        },
        params: {
        q: query,
        type: category.slice(0, -1), // "songs" -> "song"
        limit: 50,
        },
    });

    items.value = response.data[`${category.slice(0, -1)}s`]?.items || [];
    } catch (err) {
    console.error(err);
    error.value = "데이터를 불러오는 중 오류가 발생했습니다.";
    } finally {
    loading.value = false;
    }
};

// 아티스트 목록 출력
const getArtists = (artists) => artists.map((artist) => artist.name).join(", ");

// 이미지 가져오기
const getImage = (item) => {
    if (item.images?.length) return item.images[0].url;
    if (item.album?.images?.length) return item.album.images[0].url;
    return placeholderImage;
};

onMounted(fetchDetails);
</script>

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
