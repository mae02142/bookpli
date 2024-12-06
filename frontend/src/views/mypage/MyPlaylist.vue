<template>
  <div class="playlist-container">
    <LeftSidebar />
    <div class="sidebar2">
      <div class="sidebar2-item">
        <img src="@/assets/icons/search.png" class="search-icon">
        <input type="text" placeholder="검색" class="search-bar" />
        <img src="@/assets/icons/add2.png" class="add-icon">
      </div>
      <div class="playlist-list">
        <!-- 플레이리스트 목록 -->
        <div
          class="playlist-item"
          v-for="playlist in playlists"
          :key="playlist.id"
          @click="loadTracks(playlist.id)"
        >
          <img src="@/assets/sidebar/note.png" alt="icon" class="note-icon" />
          <div class="playlist-details">
            <p class="playlist-name">{{ playlist.name }}</p>
            <p class="playlist-count">{{ playlist.count }}곡</p>
          </div>
        </div>
      </div>
    </div>
    <div class="content">
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
                    <p class="song-title">{{ song.title }}</p>
                    <p class="song-artist">{{ song.artist }}</p>
                  </div>
                </div>
              </td>
              <td
                class="song-album"
                @mouseover="hoveredIndex = index"
                @mouseleave="hoveredIndex = null"
              >
                {{ song.album }}
              </td>
              <td
                class="song-duration"
                @mouseover="hoveredIndex = index"
                @mouseleave="hoveredIndex = null"
              >
                {{ formatDuration(song.duration) }}
              </td>
              <td class="delete-button-container">
                <button
                  class="delete-button"
                  @click="deleteSong(song.id)"
                >
                  삭제
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>



<script setup>
import { ref, onMounted } from 'vue';
import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
import { useAuthStore } from '@/stores/auth';
import spotifyApi from '@/api/axiosInstance';
import { jwtDecode } from 'jwt-decode';
const authStore = useAuthStore();
const hoveredIndex = ref(null);


const playlists = ref([]); // 플레이리스트 목록
const songs = ref([]); // 선택한 플레이리스트의 곡
const formatDuration = (ms) => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, '0');
  return `${minutes}:${seconds}`;
};

// 사용자 플레이리스트 가져오기
const getUserPlaylist = async () => {
  try {
    const response = await spotifyApi.get('/me/playlists'); // Spotify API 호출
    playlists.value = response.data.items.map((item) => ({
      id: item.id,
      name: item.name,
      count: item.tracks.total,
    }));
  } catch (error) {
    console.error('플레이리스트를 가져오는 중 오류 발생:', error);
  }
};

// 플레이리스트 클릭 시 해당 곡 로드
const loadTracks = async (playlistId) => {
  songs.value = await getPlaylistTracks(playlistId); // 곡 데이터 로드
};

// 특정 플레이리스트의 트랙 가져오기
const getPlaylistTracks = async (playlistId) => {
  try {
    const response = await spotifyApi.get(`/playlists/${playlistId}/tracks`);
    console.log("트랙 확인 >> " + response.data.items);
    return response.data.items.map((item) => {
      const track = item.track;
      return {
        id: track.id,
        name: track.name,
        album: track.album.name,
        artists: track.artists.map((artist) => artist.name).join(', '),
        duration: track.duration_ms,
        albumCover: track.album.images[0]?.url || '',
      };
    });
  } catch (error) {
    console.error('플레이리스트 트랙 가져오기 실패:', error);
    return [];
  }
};

// 페이지 로드 시 실행
onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const jwtToken = urlParams.get('token');

  if (jwtToken) {
    // URL에서 토큰 가져와 상태 저장
    const decodedToken = jwtDecode(jwtToken);
    const userInfo = {
      spotifyId: decodedToken.sub,
      userId: decodedToken.userId,
    };
    authStore.setUser(decodedToken.accessToken, userInfo);

    // URL에서 토큰 파라미터 제거
    window.history.replaceState({}, document.title, window.location.pathname);
  }

  getUserPlaylist(); // 플레이리스트 가져오기
});

// 노래 삭제 (임시 함수)
const deleteSong = (songId) => {
  songs.value = songs.value.filter((song) => song.id !== songId);
};


</script>

<style scoped>
.playlist-container {
  display: flex;
  height: 100vh;
}

.sidebar2 {
  width: 250px;
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
    margin-bottom: 40px;
    text-align: center;
}
</style>