<template>
    <div class="playlist-container">
        <LeftSidebar/>
        <div class="sidebar2">
            <div class="sidebar2-item">
                <img src="@/assets/icons/search.png" class="search-icon">
                <input type="text" placeholder="검색" class="search-bar" />
                <img src="@/assets/icons/add2.png" class="add-icon">
            </div>
            <div class="playlist-list">
                <div class="playlist-item" v-for="playlist in playlists" :key="playlist.id">
                <img src="@/assets/sidebar/note.png" alt="icon" class="note-icon" />
                <div class="playlist-details">
                    <p class="playlist-name">{{ playlist.name }}</p>
                    <p class="playlist-count">{{ playlist.count }}곡</p>
                </div>
                </div>
            </div>
        </div>
        <div>

        </div class="content">
        <div class="song-list">
            <h1 class="pli-header">플레이리스트</h1>
            <table class="song-table">
            <thead>
                <tr>
                <th>#</th>
                <th>제목</th>
                <th>앨범</th>
                <th><img src="@/assets/icons/time.png" class="time-icon"></th>
                <th><img src="@/assets/icons/delete.png" class="delete-icon"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(song, index) in songs" :key="song.id">
                <td class="song-index">
                    <img v-if="hoveredIndex === index" src="@/assets/icons/play.png" alt="Play Icon" class="play-icon" />
                    <span v-else>{{ index + 1 }}</span>
                </td>
                <td @mouseover="hoveredIndex = index" @mouseleave="hoveredIndex = null">
                    <div class="song-info">
                    <img :src="`/src/assets/sidebar/${song.albumCover}`" alt="Album Cover" class="album-cover" />
                    <div class="song-details">
                        <p class="song-title">{{ song.title }}</p>
                        <p class="song-artist">{{ song.artist }}</p>
                    </div>
                    </div>
                </td>
                <td class="song-album" @mouseover="hoveredIndex = index" @mouseleave="hoveredIndex = null">{{ song.album }}</td>
                <td class="song-duration" @mouseover="hoveredIndex = index" @mouseleave="hoveredIndex = null">{{ song.duration }}</td>
                <td class="delete-button-container">
                    <button class="delete-button" @click="deleteSong(song.id)">삭제</button>
                </td>
                </tr>
            </tbody>
            </table>
      </div>
    </div>
  </template>


<script setup>
import { ref } from 'vue';
import LeftSidebar from '@/components/layouts/LeftSidebar.vue';

const playlists = ref([
  { id: 1, name: '내 플레이리스트 #1', count: 30 },
  { id: 2, name: '노동요 #2', count: 30 },
]);

const songs = ref([
  { id: 1, title: 'LOVE LOVE LOVE', artist: '에픽하이', album: 'Remapping the Human Soul', duration: '3:51', albumCover: 'album1.jpeg' },
  { id: 2, title: 'LOVE LOVE LOVE', artist: '에픽하이', album: 'Remapping the Human Soul', duration: '3:51', albumCover: 'album1.jpeg' },
  { id: 3, title: 'LOVE LOVE LOVE', artist: '에픽하이', album: 'Remapping the Human Soul', duration: '3:51', albumCover: 'album1.jpeg' },
  { id: 4, title: 'LOVE LOVE LOVE', artist: '에픽하이', album: 'Remapping the Human Soul', duration: '3:51', albumCover: 'album1.jpeg' },
  { id: 5, title: 'LOVE LOVE LOVE', artist: '에픽하이', album: 'Remapping the Human Soul', duration: '3:51', albumCover: 'album1.jpeg' },
]);

// 호버된 행의 인덱스를 추적하는 상태 추가
const hoveredIndex = ref(null);

const deleteSong = (id) => {
  songs.value = songs.value.filter(song => song.id !== id);
};
</script>

<style scoped>
.playlist-container {
  display: flex;
  height: 100vh;
}

.sidebar2 {
  width: 20%;
  padding: 10px 20px;
}

.sidebar2-item {
  display: inline-flex;
  align-items: center;
}

.search-bar {
  width: 100%;
  padding: 10px;
  border: none;
  font-size: 18px;
  color: #4c4c4c;
  font-size: 16px;
}

.playlist-list {
  margin-top: 20px;
}

.playlist-item {
  display: flex;
  align-items: center;
  height: 50px;
  transition: background-color 0.3s ease;
}

.playlist-item:hover {
  cursor: pointer;
  background-color: rgba(214, 214, 214, 0.6);
  border-radius: 5px;
  }

.note-icon {
  width: 25px;
  height: 25px;
  margin-right: 13px;
}

.playlist-details {
  display: flex;
  flex-direction: column;
  gap : 4px;
}

.playlist-name {
  font-size: 16px;
  font-weight: bold;
}

.playlist-count {
  font-size: 13px;
  color: #4c4c4c;
}

.content {
  width: 60%;
  margin-top: 10px;
}

.header {
  margin-bottom: 20px;
}

.title {
  font-size: 28px;
  margin-bottom: 10px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #000;
  padding-bottom: 10px;
}

.header-title {
  width: 20%;
  font-size: 20px;
}

.song-list {
width: 60%;
margin-top: 20px;
}

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
  font-size: 16px;
  font-weight: bold;
}

.song-artist {
  font-size: 14px;
  color: #4c4c4c;
}

.delete-button {
  background: #343434;
  color: #ffffff;
  border: none;
  padding: 5px 15px;
  border-radius: 17px;
  cursor: pointer;
}

.delete-button:hover {
  background: #000000;
}

.search-icon {
  width: 20px;
  height: 20px;
}

.add-icon {
  width: 20px;
  height: 20px;
}

.time-icon{
  width: 20px;
  margin-left: 5px;
}

.delete-icon{
  width: 20px;
  margin-left: 15px;
}

.pli-header {
    font-weight: bold;
    font-size: 25px;
    margin-bottom: 30px;
    text-align: center;
}
</style>