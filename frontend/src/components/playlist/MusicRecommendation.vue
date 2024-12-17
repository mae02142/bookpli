<template>
    <div>
        <h2>노래 추천</h2>
    </div>
<table class="song-table">
    <thead>
      <tr>
        <th>#</th>
        <th>제목</th>
        <th>앨범</th>
        <th>
          <img src="@/assets/icons/time.png" class="time-icon" />
        </th>
        <th>
          <img src="@/assets/icons/delete.png" class="delete-icon" />
        </th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(song, index) in songs" :key="song.id">
        <td class="song-index">
          <img
            v-if="hoveredIndex === index"
            src="@/assets/icons/play.png"
            alt="Play Icon"
            class="play-icon"
          />
          <span v-else>{{ index + 1 }}</span>
        </td>
        <td
          @mouseover="hoveredIndex = index"
          @mouseleave="hoveredIndex = null"
        >
          <div class="song-info">
            <img
              :src="song.albumCover"
              alt="Album Cover"
              class="album-cover"
            />
            <div class="song-details">
              <p class="song-title" @click="openSongDetail(song)">
                {{ song.name }}
              </p>
              <p class="song-artist">{{ song.artists }}</p>
            </div>
          </div>
        </td>
        <td class="song-album">{{ song.album }}</td>
        <td class="song-duration">{{ handleFormat(song.duration) }}</td>
        <td class="delete-button-container" v-if="selectedPlaylist.owner === authStore.user.spotifyId">
          <button class="delete-button" @click="addMusic(selectedPlaylist.id, song.id)">추가</button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useModalStore } from '@/stores/modalState';

const songs = ref([]); 
const hoveredIndex = ref(null);

// 노래 세부 정보 열기
const openSongDetail = (song) => {
  selectedSong.value = song;
  modalStore.openModal("SongDetailModal");
};
</script>


<style scoped>
.song-table {
width: 100%;
border-collapse: collapse;
text-align: center;
}

.song-table td:hover {
    cursor: pointer;
}

.song-table th, .song-table td {
  padding: 10px 0px;
  text-align: left; /* 기본적으로 좌측 정렬 */
  border-bottom: 1px solid #e0e0e0;
  vertical-align: middle; /* 수직 정렬을 가운데로 */
  font-size: 14px;
}

.song-table th {
  font-weight: bold;
  vertical-align: middle; /* 제목도 가운데 정렬 */
}

.song-index {
  width: 5%;
  vertical-align: middle; /* 가운데 정렬 */
}

.song-detail {
  width: 40%;
  vertical-align: middle; /* 가운데 정렬 */
}

.song-album {
  width: 35%;
  vertical-align: middle; /* 가운데 정렬 */
}

.song-duration {
  width: 10%;
  text-align: center; /* 가운데 정렬 */
  vertical-align: middle; /* 수직으로 가운데 정렬 */
}

.delete-button-container {
  width: 10%;
  text-align: center; /* 가운데 정렬 */
  vertical-align: middle; /* 수직으로 가운데 정렬 */
}

.song-info {
  display: flex;
  align-items: center; /* 이미지와 텍스트가 수평으로 가운데 정렬되도록 */
}

.album-cover {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}

.song-details {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.song-title {
  font-size: 14px;
  font-weight: bold;
}

.song-artist {
  font-size: 13px;
  color: #4c4c4c;
}

.delete-button {
  background: #343434;
  color: #ffffff;
  border: none;
  padding: 5px 15px;
  border-radius: 17px;
  cursor: pointer;
  min-width: 25px;
}

.delete-button:hover {
  background: #000000;
}
</style>