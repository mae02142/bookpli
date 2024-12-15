<template>
    <div class="left-sidebar">
      <router-link to="/mypage/mylibrary">
        <img class="sidebar-icon" src="@/assets/sidebar/bookshelf.png">
      </router-link>  
      <router-link to="/mypage/mypli">
        <img class="sidebar-icon" src="@/assets/sidebar/playlist.png">
      </router-link>  
      <router-link to="/bookclub">
        <img class="sidebar-icon" src="@/assets/sidebar/bookclub.png">
      </router-link>
        <img class="sidebar-icon" src="@/assets/sidebar/usericon.png" @click="openModal">
        <UserInfoModal   v-if="modalStore.activeModal === 'UserInfoModal'" 
        @close-modal="closeModal" />
    </div>
</template>

<script setup>
import { useModalStore } from "@/stores/modalState";
import UserInfoModal from "@/views/mypage/UserInfoModal.vue";

const modalStore = useModalStore();

const openModal = () => {
  modalStore.openModal("UserInfoModal");
};

const closeModal = () => {
  modalStore.closeModal();
};

</script>

<style scoped>
.left-sidebar {
  width: 50px;
  background: #fffade;
  padding: 10px;
  display: flex;
  flex-flow: column;
  gap: 25px;
  place-items: center;
}

.sidebar-icon {
    width: 45px;
}

.sidebar-icon:hover {
    cursor: pointer;
}

</style>