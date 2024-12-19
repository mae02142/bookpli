<template>
  <div v-if="isActive" class="music-detail-modal-overlay" @click.self="closeModal">
    <div class="music-detail-modal-content">
      <div>
        <div class="music-detail-modal-header">
          <img src="@/assets/icons/close.png" class="close-button" alt="Close" @click="closeModal" />
        </div>
        <div class="music-detail-modal-content-grid">
          <div class="album-grid">
            <img :src="songData.albumCover|| songData.image " class="album-img" alt="Album Cover" />
              <div class="album-grid-right">
              <p class="album-grid-title">{{ songData.name }}</p>
              <p class="album-grid-artist">{{ songData.artists }}</p>
              <div class="add-to-playlist-block" ref="playlistBlock">
                <img
                  src="@/assets/icons/music/add_playlist.png"
                  class="add-playlist-icon"
                  @click="togglePlaylistModal"
                />
                <span>플레이리스트에 추가</span>
                <!-- 플레이리스트 선택창 -->
                <div v-if="showPlaylistModal" class="add-music-playlist-modal">
                    <p
                      v-for="(playlist) in playlists"
                      :key="playlist.id"
                      @click="selectPlaylist(playlist.id)"
                    >
                      {{ playlist.name }}
                    </p>
                </div>
              </div>
            </div>
          </div>
          <div class="album-title-grid">
            <div>
              <p>앨범</p>
              <p>{{ songData.album }}</p>
            </div>
            <img
              @click="playAlbum(songData.albumId)"
              src="@/assets/icons/music/album_play.png"
              alt="Play"
              class="play-button"
            />
          </div>
          <div class="table-body">
            <div class="song-table">
              <table class="song-detail-table">
                <thead class="song-detail-thead">
                  <tr>
                    <th class="column-index">#</th>
                    <th class="column-title">제목</th>
                    <th class="column-duration">
                      <img
                        src="@/assets/icons/time.png"
                        alt="Duration Icon"
                        class="icon-duration"
                      />
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(track, index) in tracks" :key="track.id" @click="selectSong(track)">
                    <td class="song-index">{{ index + 1 }}</td>
                    <td>
                      <div class="song-info">
                        <span class="song-title">{{ track.name }}</span>
                        <span class="song-artists">{{ track.artists }}</span>
                      </div>
                    </td>
                    <td class="song-duration">{{ track.duration }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showNotification" class="notification-box">
    {{ notificationMessage }}
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useModalStore } from "@/stores/modalState";
import apiClient from "@/api/axiosInstance";
import { useUtilModalStore } from "@/stores/utilModalStore";
import { useAuthStore } from "@/stores/auth";
import { useUserStore } from "@/stores/user";
import axios from "axios";

// Pinia store
const modalStore = useModalStore();
const utilModalStore = useUtilModalStore();

// 현재 모달이 활성화 상태인지 확인
const isActive = computed(() => modalStore.activeModal === "SongDetailModal");

const userStore = useUserStore();
const token = userStore.accessToken;

// 부모에서 전달받는 `props`
const props = defineProps({
  song: {
    type: Object,
    required: false,
    default: () => ({
      id: "",
      name: "",
      album: "",
      albumId: "",
      artists: "",
    }),
  },
});

const emit = defineEmits(["update-tracks", "update-playlist"]);
const songData = ref(props.song);
const tracks = ref([]);
const playlists = ref([]);
const showPlaylistModal = ref(false);
const notificationMessage = ref(""); // 알림 메시지
const showNotification = ref(false); // 알림 표시 여부

const selectSong = (track) => {
  songData.value = {
    ...songData.value, // 기존 데이터 유지
    id: track.id,
    name: track.name,
    album: songData.value.album, // 기존 앨범명 유지
    albumId: songData.value.albumId, // 기존 앨범 ID 유지
    artists: track.artists, // 트랙의 아티스트 정보
  };
  console.log(songData.value);
};

const displayNotification = (message) => {
  notificationMessage.value = message;
  showNotification.value = true;

  // 3초 후 알림 숨김 처리
  setTimeout(() => {
    showNotification.value = false;
  }, 2000);
};

// Pinia를 활용한 모달 닫기
const closeModal = () => {
  modalStore.closeModal();
};

// 시간을 포맷팅하는 함수
const formatDuration = (ms) => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, "0");
  return `${minutes}:${seconds}`;
};

// 앨범 트랙 가져오기
const getAlbumTracks = async () => {
  try {
    const response = await apiClient.get(`/api/music/album/${songData.value.albumId}`);
    tracks.value = response.data.data.items.map((track) => ({
      id: track.id,
      name: track.name,
      artists: track.artists.map((artist) => artist.name).join(", "),
      duration: formatDuration(track.duration_ms),
    }));
  } catch (error) {
    console.log(error);
  }
};

const getActiveDevices = async () => {
  try {
      const response = await axios.get("https://api.spotify.com/v1/me/player/devices", {
      headers: {
          Authorization: `Bearer ${token}`,
      },
      });
      return response.data.devices;
  } catch (error) {
      console.error(
      "Error fetching active devices:",
      error.response ? error.response.data : error.message
      );
      return [];
  }
};

const playAlbum = async (albumId) => {
  const playUrl = "https://api.spotify.com/v1/me/player/play";
  const albumDetailsUrl = `https://api.spotify.com/v1/albums/${albumId}`;

  try {
    // Fetch the album details to get the URI
    const albumResponse = await axios.get(albumDetailsUrl, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const albumUri = albumResponse.data.uri; // Album URI
    console.log(`Album URI fetched: ${albumUri}`);

    // Get active devices
    const devices = await getActiveDevices();
    if (devices.length === 0) {
      alert("활성화된 Spotify 기기가 없습니다. Spotify 앱을 열어 활성화된 기기를 만드세요.");
      return;
    }
    console.log(devices);

    // Play the album using its URI
    await axios.put(
      playUrl,
      {
        context_uri: albumUri, // Use the fetched album URI
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );
  } catch (error) {
    if (error.response?.status === 403) {
      if (error.response.data.error.reason === "PREMIUM_REQUIRED") {
        alert("Spotify Premium 계정이 필요합니다.");
      } else {
        alert("Spotify에서 이 요청을 실행할 수 없습니다. 활성 기기 또는 계정을 확인하세요.");
      }
    } else {
      console.error("Error playing album:", error.response?.data || error.message);
      alert("앨범 재생 중 문제가 발생했습니다.");
    }
  }
};

// 플레이리스트 가져오기
const getMyPlaylist = async () => {
  const authStore = useAuthStore(); 
  try {
    const response = await apiClient.get("api/mypli");
    playlists.value = response.data.data.items
      .filter((item) => item.owner.id === authStore.user.spotifyId)
      .map((item) => ({
        id: item.id,
        name: item.name,
      }));
  } catch (error) {
    console.log(error);
  }
};

// 플레이리스트 모달 토글
const togglePlaylistModal = async () => {
  showPlaylistModal.value = !showPlaylistModal.value;
  getMyPlaylist();
};

// 플레이리스트 선택
const selectPlaylist = async (playlistId) => {
  try {
    // 1. 선택한 플레이리스트에 곡 존재 여부 확인
    const response = await apiClient.get(`/api/mypli/playlist/${playlistId}`);

    // 2. 곡이 이미 존재하면 확인 창 표시
    const utilModalStore = useUtilModalStore();
    const tracks = response.data.data.items.map((item) => item.track.id);
    
    if (tracks.includes(songData.value.id)) {
      utilModalStore.showModal(
        '플레이리스트 담기',
        `"${playlists.value.find((p) => p.id === playlistId)?.name}"에 이미 존재하는 곡입니다.`,
        'already-exist'
      );
    } else {
      await addMusicToPlaylist(playlistId);
    }
  } catch (error) {
    console.log(error);
  }
  showPlaylistModal.value = false; // 선택 후 모달 닫기
};

const addMusicToPlaylist = async(playlistId) => {
  const songUri = `spotify:track:${songData.value.id}`;
  try {
    await apiClient.post(`/api/mypli/playlist/${playlistId}`, {
        uris: [songUri],
      });

    const playlist = playlists.value.find((p) => p.id === playlistId);
    displayNotification(`"${playlist.name}"에 추가되었습니다.`);
    emit('update-playlist');
    emit('update-tracks');
  } catch (error) {
    utilModalStore.showModal("플리에 추가하기", `추가할 노래를 선택하세요.`, "warning");
    console.log(error);
  }
}


onMounted(() => {
  getAlbumTracks();
});
</script>

  <style scoped>
  .music-detail-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .music-detail-modal-content {
    background: white;
    border-radius: 10px;
    width: 450px;
    padding: 20px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    text-align: center;
    position: relative;
  }
  
  .music-detail-modal-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
  }
  
  .close-button {
    background: none;
    border: none;
    cursor: pointer;
    width: 13px;
  }
  
  .album-cover {
    width: 200px;
    height: 200px;
    object-fit: cover;
    margin-bottom: 20px;
  }

  td {
    vertical-align: middle;
    padding: 10px 7px;
    font-size: 13px;
  }

  .song-detail-table {
    width: 100%;
    border-collapse: collapse;
  }

  .song-detail-table th {
    border-bottom: 1px solid #e0e0e0;
    padding: 10px 7px;
    font-size: 14px;
  }
  
  .song-table {
    max-height: 400px; /* 테이블 높이 제한 */
    overflow-y: auto; /* 세로 스크롤 활성화 */
  }

  .song-artists {
    font-size: 13px;
  }

  /* 스크롤바 스타일 */
.song-table::-webkit-scrollbar {
  width: 8px;
}

.song-table::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
  width: 400px;
}

.song-table::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.song-table::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

/* 스크롤바 기본 스타일 */
.song-table {
  scrollbar-width: thin; /* Firefox: 스크롤바 얇게 설정 */
}


  
  .song-info {
    text-align: left;
    display: flex;
    justify-content: center;
    flex-direction: column;
    gap: 3px;
  }
  
  .song-info p {
    margin: 5px 0;
  }

  
  .play-button {
    cursor: pointer;
    width: 36px;
  }
  

  .album-grid {
    display: flex;
  }

  .album-grid-right {
    gap: 3px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin-left: 12px;
  }

  .add-playlist-icon {
    width: 14px;
    height: 15px;
    cursor: pointer;
  }

  .album-grid-title {
    font-size: 16px;
    font-weight: bold;
    text-align: start;
  }

  .album-grid-artist {
    font-size: 13px;
    text-align: start;
  }

  .album-img {
    width: 180px;
  }

  .music-detail-music-modal-contents {
    display: grid;
    justify-content: center;
  }

  .album-title-grid{
    margin-top: 15px;
    margin-bottom: 28px;
    display: flex;
    align-items: center;
    text-align: start;
    width: 100%;
    justify-content: space-between;
  }

  .album-title-grid p:first-child{
    font-size: 13px;
  }
  
  .album-title-grid p:last-child{
    font-size: 19px;
    font-weight: bold;
    margin-top: 3px;
  }

  .table-body {
    text-align-last: start;
  }

  .icon-duration {
    width: 18px;
    height: 18px;
  }

  .music-detail-modal-content-grid {
    width: 380px;
    display: grid;
    place-self: center;
  }

  .song-title {
    font-size: 14px;
    font-weight: bold;
  }

  .add-to-playlist-block {
    display: flex;
    margin-top: 10px;
    font-size: 13px;
    align-items: center;
    gap: 5px;
    position: relative;
  }

  /* .playlist-modal {
    display: flex;
    flex-flow: column;
  } */

  .add-music-playlist-modal {
    position: absolute;
    background: white;
    border: 1px solid #ccc;
    border-radius: 3px;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    top: -2px;
    left: 20px;
    display: flex;
    min-width: 166px;
;
    flex-direction: column;
    width: max-content;
}

.add-music-playlist-modal p {
  padding: 10px;
    font-size: 14px;
    color: #000000;
    cursor: pointer;
    border-radius: 5px;
    text-align: start;
}

.add-music-playlist-modal p:hover {
  background: #f0f0f0;
}

.notification-box {
  position: absolute;
  bottom: 70px;
  left: 50%; /* 화면 가로의 중앙 */
  transform: translateX(-50%); /* 가로 중앙 정렬 */
  padding: 20px;
  border-radius: 10px;
  background-color: #000000;
  color: white;
}

.song-info:hover {
  cursor: pointer;
  color: #1db954; /* Spotify 녹색 강조 */
}
  </style>
  