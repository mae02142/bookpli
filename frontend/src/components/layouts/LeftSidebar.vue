<template>
    <div class="sidebar">
        <img class="bookshelf-icon" src="@/assets/sidebar/bookshelf.png">
        <img class="playlist-icon" src="@/assets/sidebar/playlist.png">
        <img class="user-icon" src="@/assets/sidebar/usericon.png" @click="openModal">
        <UserInfoModal v-if="modalState.isUserInfoModalOpen" @close="closeModal" />
    </div>
</template>

<script setup>
import { modalState, openUserInfoModal, closeUserInfoModal } from "@/stores/modalState.js";
import UserInfoModal from "@/views/mypage/UserInfoModal.vue";

const openModal = () => {
  openUserInfoModal();
};

const closeModal = () => {
  closeUserInfoModal();
};
</script>

<style scoped>
.sidebar {
  width: 45px;
  background: #fffdf1;
  padding: 10px;
  display: flex;
  flex-flow: column;
  gap: 25px;
}

.bookshelf-icon, .playlist-icon {
    width: 45px;
}

.bookshelf-icon:hover {
    cursor: pointer;
}

.playlist-icon:hover {
    cursor: pointer;
}

</style>