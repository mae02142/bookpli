<template>
  <div class="playlist-container">
    <LeftSidebar />
    <div class="sidebar2">
      <div class="sidebar2-item">
        <img src="@/assets/icons/search.png" class="search-icon" />
        <input type="text" placeholder="검색" class="search-bar" />
        <img src="@/assets/icons/add2.png" class="add-icon" />
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
      <div class="song-list" v-if="selectedPlaylist">
        <h1 class="pli-header">{{ selectedPlaylist.name }}</h1>
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
              <td class="song-duration">{{ formatDuration(song.duration) }}</td>
              <td class="delete-button-container">
                <button class="delete-button" @click="deleteSongFromPlaylist(song.id)">
                  삭제
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <!-- 모달 컴포넌트 -->
    <SongDetailModal
      v-if="modalStore.activeModal === 'SongDetailModal'"
      :song="selectedSong"
      @close="modalStore.closeModal"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
import { useAuthStore } from '@/stores/auth';
import { useModalStore } from '@/stores/modalState';
import SongDetailModal from "@/components/playlist/MusicDetailModal.vue"
import apiClient from '@/api/axiosInstance';

// 상태 관리
const authStore = useAuthStore();
const modalStore = useModalStore();
const hoveredIndex = ref(null);
const playlists = ref([]); // 플레이리스트 목록
const selectedPlaylist = ref(null); // 선택된 플레이리스트
const selectedSong = ref(null); // 선택된 노래
const songs = ref([]); // 선택된 플레이리스트의 곡

// 유틸 함수: 곡 길이 포맷
const formatDuration = (ms) => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, "0");
  return `${minutes}:${seconds}`;
};

// 노래 세부 정보 열기
const openSongDetail = (song) => {
  selectedSong.value = song;
  modalStore.openModal("SongDetailModal");
};

// 플레이리스트 로드
const loadTracks = async (playlistId) => {
  try {
    songs.value = await getPlaylistTracks(playlistId); // 트랙 가져오기
    selectedPlaylist.value = playlists.value.find(
      (playlist) => playlist.id === playlistId
    );
  } catch (error) {
    console.error("플레이리스트 로드 실패:", error);
  }
};

// Spotify API: 플레이리스트 가져오기
const getUserPlaylist = async () => {
  try {
    const response = await apiClient.get("/api/mypli");
    console.log(">>>>>>>>.",response.data);
    playlists.value = response.data.data.items.map((item) => ({
      id: item.id,
      name: item.name,
      count: item.tracks.total,
    }));
  } catch (error) {
    console.error("플레이리스트 가져오기 실패:", error);
  }
};

// Spotify API: 특정 플레이리스트의 트랙 가져오기
const getPlaylistTracks = async (playlistId) => {
  try {
    const response = await apiClient.get(`/api/mypli/playlist/${playlistId}`);
    return response.data.data.items.map((item) => {
      const track = item.track;
      return {
        id: track.id,
        name: track.name,
        album: track.album.name,
        artists: track.artists.map((artist) => artist.name).join(", "),
        duration: track.duration_ms,
        albumCover: track.album.images[0]?.url || "",
      };
    });
  } catch (error) {
    console.error("트랙 가져오기 실패:", error);
    return [];
  }
};

const getUserInfo = async() => {
  try {
    const response = await apiClient.get("/api/auth/user-info");

    if (response.data.data) {
      const userInfo = {
        spotifyId: response.data.data.spotifyId,
        userId: response.data.data.userId,
      };

      authStore.setUser(userInfo);
    }
  } catch (error) {
    console.error("사용자 정보 요청 실패:", error);
  }
};

// 페이지 로드 시 실행
onMounted(() => {
  getUserInfo();
  getUserPlaylist(); // 플레이리스트 가져오기
});

// 노래 삭제
const deleteSongFromPlaylist = async (playlistId, songId) => {
  const songUri = `spotify:track:${songId}`; // Spotify 트랙 URI 형식

  try {
    await apiClient.delete(`/playlists/${playlistId}/tracks`, {
      data: {
        tracks: [{ uri: songUri }],
      },
    });
    songs.value = songs.value.filter((song) => song.id !== songId);
    console.log(`노래가 플레이리스트에서 삭제되었습니다. songId: ${songId}`);
  } catch (error) {
    console.error("플레이리스트에서 노래 삭제 실패:", error);
  }
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