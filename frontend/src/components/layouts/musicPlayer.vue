<template>
  <div class="music-player">
    <!-- Fold Button -->
    <button @click="toggleFold" class="fold-button">
      {{ fold ? "▲ 펼치기" : "▼ 접기" }}
    </button>

    <!-- Expanded Content -->
    <div v-show="!fold" class="expanded-content">
      <div v-if="playerStore.currentTrack">
        <img :src="playerStore.currentTrack.albumCover" alt="Album Cover" class="cover-image" />
        <p class="track-title">{{ playerStore.currentTrack.title }}</p>
        <p class="track-artist">{{ playerStore.currentTrack.artist }}</p>
        <input 
          type="range" 
          class="music-progress-bar" 
          min="0" 
          :max="playerStore.currentTrack.duration" 
          v-model="playerStore.currentTime" 
        />
        <div class="time-info">
          <span>{{ formatTime(playerStore.currentTime) }}</span> /
          <span>{{ formatTime(playerStore.currentTrack.duration) }}</span>
        </div>
      </div>
      <div v-else>
        <p>현재 재생 중인 곡이 없습니다.</p>
      </div>
    </div>

    <!-- Folded Content -->
    <div v-show="fold" class="folded-content">
      <div v-if="playerStore.currentTrack">
        <p class="track-title">{{ playerStore.currentTrack.title }}</p>
      </div>
      <div v-else>
        <p>현재 재생 중인 곡이 없습니다.</p>
      </div>
    </div>

    <!-- Controls -->
    <div v-if="playerStore.currentTrack" class="controls">
      <button @click="playerStore.previousTrack">⏮</button>
      <button class="play-btn" @click="playerStore.togglePlay">
        {{ playerStore.isPlaying ? "⏸" : "▶" }}
      </button>
      <button @click="playerStore.nextTrack">⏭</button>
    </div>

    <!-- Volume Control -->
    <div v-show="!fold && playerStore.player" class="volume-control">
      <label for="volume">Volume:</label>
      <input 
        type="range" 
        id="volume" 
        min="0" 
        max="1" 
        step="0.01" 
        v-model="playerStore.volume" 
        @input="onVolumeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { usePlayerStore } from "@/stores/playerStore.js";

const userStore = useUserStore();
const playerStore = usePlayerStore();
const fold = ref(false);

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs < 10 ? "0" : ""}${secs}`;
};

const onVolumeChange = () => {
  playerStore.setVolume(playerStore.volume);
};

const toggleFold = () => {
  fold.value = !fold.value; // 상태 토글
};

onMounted(() => {
  if (userStore.accessToken) {
    playerStore.initPlayer(userStore.token);
  }
});
</script>

<style scoped>
.music-player {
  position: fixed;
  bottom: 50px;
  right: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 250px;
  z-index: 10;
}

.fold-button {
  text-align: left;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-bottom: 10px;
}

.track-title {
  font-size: 16px;
  font-weight: bold;
  margin: 5px 0;
}

.cover-image {
  width: 200px;
  height: 200px;
  border-radius: 10px;
  object-fit: cover;
}

.music-progress-bar {
  width: 100%;
  margin-top: 10px;
}

.time-info {
  font-size: 12px;
  color: gray;
  margin-top: 5px;
}

.controls {
  display: flex;
  justify-content: center;
  gap: 10px;
}

button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

button:hover {
  color: #007bff;
}

.volume-control {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.volume-control label {
  margin-right: 10px;
}

.expanded-content,
.folded-content {
  text-align: center;
}
</style>