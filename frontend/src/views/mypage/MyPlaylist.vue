<template>
  <div class="playlist-container">
    <LeftSidebar />
    <div class="sidebar2">
      <div class="sidebar2-item">
        <img src="@/assets/icons/search.png" class="search-icon" />
        <input type="text" placeholder="검색" class="search-bar" v-model="searchPli"/>
        <img src="@/assets/icons/add_pli.png" class="add-icon" @click="toggleAddMode"/>
      </div>
      <div class="playlist-list">
        <!-- 입력창 -->
        <div v-if="addMode" class="add-playlist-box">
          <img src="@/assets/sidebar/note.png" alt="icon" class="note-icon" />
          <div class="add-playlist-box-right">
            <input
              type="text"
              placeholder="제목을 입력하세요"
              v-model="newPlaylistName"
              class="add-playlist-input"
            />
            <div class="add-playlist-btns">
              <button @click="addPlaylist" class="add-playlist-button">추가</button>
              <button @click="toggleAddMode" class="cancel-button">취소</button>
            </div>
          </div>
        </div>
        <!--개인 플레이리스트-->
        <div>
        <div class="playlist-header" @click="toggleMyPlaylists">
          <img v-if="myPlaylistOpen" src="@/assets/sidebar/close_list.png" alt="Open Icon" class="sidebar-list-icon"/>
          <img v-else src="@/assets/sidebar/open_list.png" alt="Closed Icon" class="sidebar-list-icon"/>
          <span class="playlist-label">내 플레이리스트</span>
        </div>
        <div v-if="myPlaylistOpen">
          <div
            class="playlist-item"
            v-for="(playlist, index) in filteredMyPlaylists"
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
        <!-- 다른 사람의 플레이리스트 -->
        <div>
        <div class="playlist-header other-list" @click="toggleRecommendedPlaylists">
          <img v-if="recommendedPlaylistOpen" src="@/assets/sidebar/close_list.png" alt="Open Icon" class="sidebar-list-icon"/>
          <img v-else src="@/assets/sidebar/open_list.png" alt="Closed Icon" class="sidebar-list-icon"/>
          <span class="playlist-label">추천 플레이리스트</span>
        </div>
        <div v-if="recommendedPlaylistOpen">
          <div
            class="playlist-item"
            v-for="(playlist, index) in filteredRecommendedPlaylists"
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
      </div>
    </div>
    <div class="content">
      <!-- 안내 메시지 -->
      <div v-if="!selectedPlaylist" class="playlist-placeholder">
        <p>플레이리스트를 선택 또는 추가해주세요</p>
      </div>
      <div class="song-list" v-if="selectedPlaylist">
        <div>
          <!-- 타이틀 변경 input -->
          <div v-if="isEditingTitle" class="edit-title-container">
            <input
              v-model="editedTitle"
              type="text"
              class="edit-title-input"
              placeholder="새 타이틀 입력"
            />
            <button @click="updateTitle" class="save-title-button">수정</button>
            <button @click="cancelTitle" class="cancel-title-button">취소</button>
          </div>
          <div v-else>
            <h1 class="pli-header">{{ selectedPlaylist.name }}</h1>
          </div>
          <div class="option-grid">
            <img
              src="@/assets/icons/option.png"
              alt="option-icon"
              class="option-icon"
              @click="toggleOptionMenu"
            />

            <!-- 옵션 메뉴 -->
            <div v-if="showOptionMenu" class="option-menu" @click.self="closeOptionMenu">
              <button v-if="selectedPlaylist.owner === authStore.user.spotifyId" @click="enableEditTitle" class="option-button">
                타이틀 변경
              </button>
              <button @click="confirmDeletePlaylist(selectedPlaylist.id)" class="option-button delete">삭제하기</button>
            </div>

          </div>
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
              <th v-if="selectedPlaylist.owner === authStore.user.spotifyId">
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
              <td>
                <div class="song-info">
                  <img
                    :src="song.albumCover"
                    alt="Album Cover"
                    class="album-cover"
                    @click="openSongDetail(song)"
                  />
                  <div class="song-details"
                      @mouseover="hoveredIndex = index"
                      @mouseleave="hoveredIndex = null">
                    <p class="song-title" @click="playSong(song.id)">
                      {{ song.name }}
                    </p>
                    <p class="song-artist">{{ song.artists }}</p>
                  </div>
                </div>
              </td>
              <td class="song-album" @click="openSongDetail(song)">{{ song.album }}</td>
              <td class="song-duration">{{ handleFormat(song.duration) }}</td>
              <td class="delete-button-container" v-if="selectedPlaylist.owner === authStore.user.spotifyId">
                <button class="delete-button" @click="removeMusic(selectedPlaylist.id, song.id)">
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
      @update-playlist="getUserPlaylist"
      @update-tracks="refreshPlaylistTracks"
    />
  </div>
  <MusicPlayer/>
</template>

<script setup>
import { ref, onMounted, computed  } from 'vue';
import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
import { useAuthStore } from '@/stores/auth';
import { useModalStore } from '@/stores/modalState';
import { useUserStore } from "@/stores/user";
import SongDetailModal from "@/components/playlist/MusicDetailModal.vue"
import apiClient from '@/api/axiosInstance';
import axios from "axios";
import { useConfirmModalStore } from '@/stores/utilModalStore';
import { useUtilModalStore } from '@/stores/utilModalStore';
import { getPlaylistTracks, deleteSongFromPlaylist, formatDuration } from "@/utils/spotifyUtils";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";

// 상태 관리
const authStore = useAuthStore();
const modalStore = useModalStore();
const userStore = useUserStore();
const utilModalStore = useUtilModalStore();
const hoveredIndex = ref(null);
const playlists = ref([]); // 플레이리스트 목록
const myPlaylists = ref([]);
const recommendedPlaylists = ref([]);
const myPlaylistOpen = ref(true);
const recommendedPlaylistOpen = ref(true);
const selectedPlaylist = ref(null); // 선택된 플레이리스트
const selectedSong = ref(null); // 선택된 노래
const songs = ref([]); // 선택된 플레이리스트의 곡
const addMode = ref(false); // 입력창 표시 여부
const newPlaylistName = ref(""); // 새 플레이리스트 이름
const token = userStore.accessToken;
const searchPli = ref("");

// 옵션 메뉴 상태
const showOptionMenu = ref(false);
const isEditingTitle = ref(false);
const editedTitle = ref("");

// 옵션 메뉴 토글
const toggleOptionMenu = () => {
  showOptionMenu.value = !showOptionMenu.value;
};

// 옵션 메뉴 토글
const closeOptionMenu = () => {
  showOptionMenu.value = !showOptionMenu.value;
};


// 토글 함수
const toggleMyPlaylists = () => {
  myPlaylistOpen.value = !myPlaylistOpen.value;
};

const toggleRecommendedPlaylists = () => {
  recommendedPlaylistOpen.value = !recommendedPlaylistOpen.value;
};

// 타이틀 변경 모드 활성화
const enableEditTitle = () => {
  isEditingTitle.value = true;
  editedTitle.value = selectedPlaylist.value.name;
  showOptionMenu.value = false; // 옵션 메뉴 닫기
};

// 타이틀 업데이트
const updateTitle = async () => {
  if (!editedTitle.value.trim()) {
    const utilModalStore = useUtilModalStore();
    utilModalStore.showModal(
      '플레이리스트 제목 변경',
      '제목은 1글자 이상이어야 합니다.',
      'alert'
    );
    return;
  }
  try {
    await apiClient.put(`/api/mypli/playlist/${selectedPlaylist.value.id}`, {
      name: editedTitle.value,
    });

    selectedPlaylist.value.name = editedTitle.value;
    isEditingTitle.value = false;
  } catch (error) {
    console.error("타이틀 변경 실패:", error);
  }
};

//타이틀 변경 취소
const cancelTitle = () => {
  showOptionMenu.value = false;
  editedTitle.value = selectedPlaylist.value.name;
  isEditingTitle.value = false;
}

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

// 필터링된 내 플레이리스트
const filteredMyPlaylists = computed(() => {
  if (!searchPli.value.trim()) return myPlaylists.value; // 검색어 없으면 전체 반환
  return myPlaylists.value.filter((playlist) =>
    playlist.name.toLowerCase().includes(searchPli.value.toLowerCase())
  );
});

// 필터링된 추천 플레이리스트
const filteredRecommendedPlaylists = computed(() => {
  if (!searchPli.value.trim()) return recommendedPlaylists.value; // 검색어 없으면 전체 반환
  return recommendedPlaylists.value.filter((playlist) =>
    playlist.name.toLowerCase().includes(searchPli.value.toLowerCase())
  );
});

// 플레이리스트 전체 재생
const playPlaylist = async (playlistId) => {
  try {
    // 먼저 해당 플레이리스트의 곡을 로드
    const tracks = await getPlaylistTracks(playlistId);
    const trackUris = tracks.map((track) => `spotify:track:${track.id}`); // 모든 곡의 URI 생성

    if (trackUris.length === 0) {
      utilModalStore.showModal("플레이리스트 재생", "재생할 곡이 없습니다.", "warning");
      return;
    }

    const devices = await getActiveDevices(); // 활성화된 디바이스 확인

    await axios.put(
      "https://api.spotify.com/v1/me/player/play",
      {
        uris: trackUris, // URI 배열 전송
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );
  } catch (error) {
    console.error("플레이리스트 전체 재생 중 오류:", error.response?.data || error.message);
    utilModalStore.showModal("플레이리스트 재생", "플레이리스트 재생에 실패하였습니다.", "warning");
  }
};

const playSong = async (songId) => {
  try {
    if (!selectedPlaylist.value) {
      utilModalStore.showModal("재생 오류", "플레이리스트를 선택해주세요.", "warning");
      return;
    }

    // Play a specific song within the playlist context
    const contextUri = `spotify:playlist:${selectedPlaylist.value.id}`;
    await axios.put(
      "https://api.spotify.com/v1/me/player/play",
      {
        context_uri: contextUri, // Use playlist context
        offset: { uri: `spotify:track:${songId}` }, // Start from the specific track
      },
      {
        headers: {
          Authorization: `Bearer ${userStore.accessToken}`,
        },
      }
    );
    console.log("플레이리스트 내에서 곡 재생 시작:", contextUri, songId);
  } catch (error) {
    console.error("곡 재생 오류:", error.response?.data || error.message);
    utilModalStore.showModal(
      "재생 오류",
      "곡을 재생하는 도중 문제가 발생했습니다. 다시 시도해주세요.",
      "warning"
    );
  }
};


// 플레이리스트 삭제
const confirmDeletePlaylist = (playlistId) => {
    const confirmModalStore = useConfirmModalStore();
    confirmModalStore.showModal(
    '플레이리스트 삭제',
    '선택하신 플레이리스트를 삭제하시겠습니까?',
    '삭제 후 복원할 수 없습니다.',
    '삭제하기',
    '',
    () => deletePlaylist(playlistId)
    )
  };
  
  const deletePlaylist = async(playlistId) => {
    try {
      await apiClient.delete(`/api/mypli/playlist/${playlistId}`);
      getUserPlaylist();
      selectedPlaylist.value=null;
      showOptionMenu.value = false;
    } catch (error) {
      console.log(error);
    }
}

// 유틸 함수: 곡 길이 포맷
const handleFormat = (ms) => {
  return formatDuration(ms);
};

// 노래 세부 정보 열기
const openSongDetail = (song) => {
  selectedSong.value = song;
  modalStore.openModal("SongDetailModal");
};

// 기존 loadTracks에서 playPlaylist와 연계
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
    playlists.value = response.data.data.items.map((item) => ({
      id: item.id,
      name: item.name,
      count: item.tracks.total,
      owner: item.owner.id, // 소유자 ID
    }));

    // 분류
    myPlaylists.value = playlists.value.filter(
      (playlist) => playlist.owner === authStore.user.spotifyId
    );
    recommendedPlaylists.value = playlists.value.filter(
      (playlist) => playlist.owner !== authStore.user.spotifyId
    );
  } catch (error) {
    console.error("플레이리스트 가져오기 실패:", error);
  }
};

// 노래 삭제
const removeMusic = async (playlistId, songId) => {
  try {
    await deleteSongFromPlaylist(playlistId, songId);
    songs.value = songs.value.filter((song) => song.id !== songId);
  } catch (error) {
    console.error("노래 삭제 중 오류:", error);
  }
};

const toggleAddMode = () => {
  addMode.value = !addMode.value; // 입력창 표시 여부 토글
  if (!addMode.value) {
    newPlaylistName.value = ""; // 취소 시 초기화
  }
};

const addPlaylist = async () => {
  if (!newPlaylistName.value.trim()) {
    const utilModalStore = useUtilModalStore();
    utilModalStore.showModal(
      '플레이리스트 제목 변경',
      '제목은 1글자 이상이어야 합니다.',
      'alert'
    );
    return;
  }

  try {
    const response = await apiClient.post(`/api/mypli/${authStore.user.spotifyId}`, {
      name: newPlaylistName.value,
    });
    myPlaylists.value.push({
      id: response.data.data.id,
      name: response.data.data.name,
      count: 0,
    });

    addMode.value = false;
    newPlaylistName.value = "";
  } catch (error) {
    console.error("플레이리스트 추가 실패:", error);
  }
};

const refreshPlaylistTracks = async () => {
  if (selectedPlaylist.value?.id) {
    songs.value = await getPlaylistTracks(selectedPlaylist.value.id);
  } else {
    console.error("선택된 플레이리스트가 없습니다.");
  }
};

// 페이지 로드 시 실행
onMounted(() => {
  getUserPlaylist(); // 플레이리스트 가져오기
});
</script>

<style scoped>
.playlist-container {
  height: 100vh;
  display: grid;
  grid-auto-flow: column;
  grid-template-columns: 0fr 0fr 1fr;
}

.sidebar2 {
  width: 250px;
  margin-left: 20px;
  margin-top: 10px;
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
  cursor: pointer;
}

.playlist-item:hover{
  color: #1db954;
  }

.note-icon {
  width: 25px;
  height: 25px;
  margin-right: 13px;
  margin-left: 5px;
}

.playlist-details {
  display: flex;
  flex-direction: column;
  gap : 4px;
}

.playlist-name {
  font-size: 16px;
  font-weight: bold;
  max-width: 170px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.playlist-count {
  font-size: 13px;
  color: #4c4c4c;
}

.content {
  width: 60%;
  margin-top: 10px;
  /* margin-right: auto; */
  margin-left: 10%;
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
  white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 180px;
}

.song-details:hover {
    transform: scale(1.02);
    color: #1db954;
}

.song-artist {
  font-size: 13px;
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

.search-icon {
  width: 20px;
  height: 20px;
}

.add-icon {
  width: 20px;
  height: 20px;
}

.add-icon:hover {
  cursor: pointer;
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
    text-align: center;
}

.add-playlist-box {
  display: flex;
    align-items: center;
    justify-content: space-between;
    border: 1px solid #ddd;
    border-radius: 8px;
    height: 70px;
}

.add-playlist-input {
  flex: 1;
    border: none;
    font-size: 14px;
}

.add-playlist-button {
  background-color: #252525;
    color: white;
    border: none;
    border-radius: 30px;
    padding: 5px 10px;
    cursor: pointer;
    margin-right: 5px;
    font-size: 12px;
}

.cancel-button {
  background-color: #ffffff;
    border: none;
    border-radius: 30px;
    padding: 5px 10px;
    cursor: pointer;
    font-size: 12px;
    border: 1px solid #ababab;
}

.add-playlist-box-right {
  display: flex;
    flex-direction: column;
    width: 100%;
    gap: 5px;
}

.add-playlist-btns {
  align-self: end;
  margin-right: 10px;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
}

.option-icon {
  cursor: pointer;
  margin-bottom: 10px;
  width: 21px;
  height: 17px;
}

.option-menu {
  background-color: white;
  border: 1px solid #ddd;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  z-index: 1000;
  margin-left: 12px;
  margin-bottom: 5px;
}

.option-button {
  display: block;
  width: 100%;
  padding: 10px;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.option-button:hover {
  font-weight: bold;
}

.option-button.delete {
  color: red;
}

.edit-title-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;

}

.edit-title-input {
  font-size: 16px;
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.save-title-button:hover {
  background-color: #218838;
}

.option-grid {
  display: flex;
  align-items: end;
  min-height: 85px;
}

.save-title-button {
  background-color: #252525;
    color: white;
    border: none;
    border-radius: 30px;
    padding: 5px 10px;
    cursor: pointer;
    font-size: 12px;
}

.cancel-title-button {
  background-color: #ffffff;
    border-radius: 30px;
    padding: 5px 10px;
    cursor: pointer;
    font-size: 12px;
    border: 1px solid #ababab;
}

.playlist-header {
  margin-bottom: 10px;
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.sidebar-list-icon {
  cursor: pointer;
  width: 16px;
}

.playlist-label {
  margin-left: 5px;
  font-size: 15px;
  font-weight: bold;
}

.other-list {
  margin-top: 30px;
}

.playlist-placeholder {
  display: flex;
  justify-content: center;
  margin-top: 50px;
}
</style>