<!-- src/components/layouts/MusicPlayer.vue -->
<template>
    <div class="music-player">
        <div class="album-cover">
            <img :src="currentTrack.albumCover" alt="Album Cover" class="cover-image" />
        </div>
        <div class="track-info">
            <p class="track-title">{{ currentTrack.title }}</p>
            <p class="track-artist">{{ currentTrack.artist }}</p>
            <input 
                type="range" 
                class="progress-bar" 
                min="0" 
                :max="currentTrack.duration" 
                v-model="currentTime" 
            />
        </div>
        <div class="controls">
            <button class="prev-btn" @click="prevTrack">⏮</button>
            <button class="play-btn" @click="togglePlay">{{ isPlaying ? "⏸" : "▶" }}</button>
            <button class="next-btn" @click="nextTrack">⏭</button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from "vue";
defineOptions({
    name: 'MusicPlayer'
});

// 현재 재생 중인 트랙 인덱스
const currentTrackIndex = ref(0);

// 재생 상태
const isPlaying = ref(false);

// 현재 진행 시간
const currentTime = ref(0);

// 트랙 목록
const tracks = ref([
    {
        title: "Song Title 1",
        artist: "Artist 1",
        albumCover: "https://via.placeholder.com/80",
        duration: 240, // In seconds
    },
    {
        title: "Song Title 2",
        artist: "Artist 2",
        albumCover: "https://via.placeholder.com/80",
        duration: 200,
    },
    {
        title: "Song Title 3",
        artist: "Artist 3",
        albumCover: "https://via.placeholder.com/80",
        duration: 300,
    },
]);

// 현재 재생 중인 트랙 정보
const currentTrack = computed(() => tracks.value[currentTrackIndex.value]);

// 재생/일시정지 토글
const togglePlay = () => {
    isPlaying.value = !isPlaying.value;
};

// 다음 곡으로 이동
const nextTrack = () => {
    currentTrackIndex.value =
        (currentTrackIndex.value + 1) % tracks.value.length;
};

// 이전 곡으로 이동
const prevTrack = () => {
    currentTrackIndex.value =
        (currentTrackIndex.value - 1 + tracks.value.length) % tracks.value.length;
};
</script>

<style scoped>
.music-player {
    position: fixed;
    bottom: 60px;
    right: 40px;
    display: flex;
    flex-direction: column; /* 레이아웃을 세로로 변경 */
    align-items: center; /* 중앙 정렬 */
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    background-color: #ffffff;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    width: 250px; /* 너비 조정 */
    /* height: 400px; */
}

.album-cover {
    margin-bottom: 15px; /* 아래로 간격 추가 */
}

.cover-image {
    width: 230px;
    height: 230px;
    border-radius: 10px;
    object-fit: cover;
}

.track-info {
    width: 100%;
    text-align: center; /* 중앙 정렬 */
    margin-bottom: 15px; /* 아래로 간격 추가 */
}

.track-title {
    font-size: 16px;
    font-weight: bold;
    margin: 5px 0;
}

.track-artist {
    font-size: 14px;
    color: gray;
    margin: 5px 0;
}

.progress-bar {
    width: 100%;
    margin-top: 10px;
}

.controls {
    display: flex;
    justify-content: center; /* 중앙 정렬 */
    gap: 10px;
}

button {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
}

button:hover {
    color: #007bff;
}
</style>
