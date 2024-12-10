<template>
    <div v-if="isActive" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-content-grid">
          <div class="modal-header">
              <img src="@/assets/icons/close.png" class="close-button" alt="Close" @click="closeModal"/>
          </div>
          <div class="album-grid">
              <img src="@/assets/icons/music/album.png" class="album-img">
              <div class="album-grid-right">
                  <p class="album-grid-title">LOVE LOVE LOVE</p>
                  <p class="album-grid-artist">에픽하이</p>
                  <img src="@/assets/icons/music/add_playlist.png" class="add-playlist-icon">
              </div>
            </div>
          <div class="album-title-grid">
              <p>앨범</p>
              <p>Remapping the Human Soul</p>
          </div>
          <div class="modal-body">
            <div class="song-info">
              <table class="song-detail-table">
                  <thead class="song-detail-thead">
                  <tr>
                      <th class="column-index">#</th>
                      <th class="column-title">제목</th>
                      <th class="column-duration">
                      <img src="@/assets/icons/time.png" alt="Duration Icon" class="icon-duration" />
                      </th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="(song, index) in songs" :key="song.id">
                      <td class="song-index">{{ index + 1 }}</td>
                      <td class="song-title">{{ song.name }}</td>
                      <td class="song-artist">{{ song.artist }}</td>
                      <td class="song-duration">{{ song.duration }}</td>
                  </tr>
                  </tbody>
              </table>
            </div>
            <div class="modal-actions">
              <button class="play-button" @click="playSong">
                <img src="@/assets/icons/music/album_play.png" alt="Play" />
                재생
              </button>
              <button class="save-button" @click="saveSong">
                <img src="@/assets/icons/music/add_playlist.png" alt="Save" />
                저장하기
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { computed, ref } from "vue";
  import { useModalStore } from "@/stores/modalState";
  
  // Pinia store
  const modalStore = useModalStore();
  
  // 현재 모달이 활성화 상태인지 확인
  const isActive = computed(() => modalStore.activeModal === "SongDetailModal");
  
  // 부모에서 전달받는 `props`
  const props = defineProps({
    songs: {
      type: Object,
      required: false,
      default: () => ({
        name: "",
        album: "",
        duration: 0,
        albumCover: "",
      }),
    },
  });
  
  // 로컬 데이터로 변환
  const songData = ref(props.song);
  
  // Pinia를 활용한 모달 닫기
  const closeModal = () => {
    modalStore.closeModal();
  };
  
  // 노래 재생 함수
  const playSong = () => {
    console.log(`Playing song: ${songData.value.name}`);
  };
  
  // 노래 저장 함수
  const saveSong = () => {
    console.log(`Saving song: ${songData.value.name}`);
  };
  
  // 시간을 포맷팅하는 함수
  const formatDuration = (ms) => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, "0");
    return `${minutes}:${seconds}`;
  };
  </script>
  
  <style scoped>
  .modal-overlay {
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
  
  .modal-content {
    background: white;
    border-radius: 10px;
    width: 400px;
    padding: 20px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    text-align: center;
    position: relative;
    display: grid;
    justify-content: center;
  }
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .close-button {
    background: none;
    border: none;
    cursor: pointer;
  }
  
  .album-cover {
    width: 200px;
    height: 200px;
    object-fit: cover;
    margin-bottom: 20px;
  }

  .song-detail-table {
    width: 100%;
    border-collapse: collapse;
  }

  .song-detail-table th {
    border-bottom: 1px solid #e0e0e0;
    padding: 10px 0px;
    font-size: 14px;
  }
  
  .song-info {
    text-align: left;
    margin-bottom: 20px;
    display: flex;
    justify-content: center;
  }
  
  .song-info p {
    margin: 5px 0;
  }
  
  .modal-actions {
    display: flex;
    justify-content: space-around;
  }
  
  .play-button,
  .save-button {
    background: #007bff;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .play-button:hover,
  .save-button:hover {
    background: #0056b3;
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
    width: 16px;
    height: 17px;
    margin-top: 5px;
  }

  .album-grid-title {
    font-size: 16px;
    font-weight: bold;
  }

  .album-grid-artist {
    font-size: 13px;
  }

  .album-img {
    width: 180px;
  }

  .music-modal-contents {
    display: grid;
    justify-content: center;
  }

  .album-title-grid{
    display: grid;
    margin-top: 15px;
    justify-items: flex-start;
  }

  .album-title-grid p:first-child{
    font-size: 13px;
  }
  
  .album-title-grid p:last-child{
    font-size: 20px;
    font-weight: bold;
  }
  </style>
  