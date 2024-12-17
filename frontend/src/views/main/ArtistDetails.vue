<template>
    <div class="artist-details">
        <h1>{{ artist.name }}</h1>
        <div v-if="loading">로딩 중...</div>
        <div v-else-if="error">{{ error }}</div>

        <section v-if="albums.length">
            <h2>앨범</h2>
            <div class="albums-grid">
                <div v-for="album in albums" :key="album.id" class="album-item">
                    <img :src="album.images[0]?.url || placeholderImage" alt="앨범 커버" />
                    <p>{{ album.name }}</p>
                </div>
            </div>
        </section>

        <section v-if="tracks.length">
            <h2>트랙</h2>
            <div class="tracks-list">
                <div v-for="track in tracks" :key="track.id" class="track-item">
                    <p>{{ track.name }}</p>
                </div>
            </div>
        </section>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const userStore = useUserStore();
const artistId = route.params.id;

const artist = ref({});
const albums = ref([]);
const tracks = ref([]);
const loading = ref(false);
const error = ref(null);

const placeholderImage = "https://via.placeholder.com/150";

const fetchArtistDetails = async () => {
    loading.value = true;
    try {
        // 아티스트 정보
        const artistResponse = await axios.get(`https://api.spotify.com/v1/artists/${artistId}`, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
        });
        artist.value = artistResponse.data;

        // 아티스트 앨범
        const albumsResponse = await axios.get(`https://api.spotify.com/v1/artists/${artistId}/albums`, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
            params: { include_groups: "album", limit: 10 },
        });
        albums.value = albumsResponse.data.items;

        // 아티스트 인기 트랙
        const tracksResponse = await axios.get(`https://api.spotify.com/v1/artists/${artistId}/top-tracks`, {
            headers: { Authorization: `Bearer ${userStore.accessToken}` },
            params: { market: "US" },
        });
        tracks.value = tracksResponse.data.tracks;
    } catch (err) {
        error.value = "데이터를 불러오는 중 오류가 발생했습니다.";
    } finally {
        loading.value = false;
    }
};

onMounted(fetchArtistDetails);
</script>

<style scoped>
.artist-details {
    max-width: 1200px;
    margin: auto;
    padding: 1rem;
    text-align: center;
}

.albums-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    justify-content: center;
}

.album-item img {
    width: 150px;
    height: 150px;
    object-fit: cover;
    margin-bottom: 10px;
}

.tracks-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
}

.track-item p {
    margin: 0;
    font-size: 1.2rem;
}
</style>
