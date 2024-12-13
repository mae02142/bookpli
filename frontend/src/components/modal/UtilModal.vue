<template>
    <div v-if="modalStore.isModalVisible" class="modal-overlay">
      <div class="modal-container">
        <div class="modal-header">
          <span>{{ modalStore.modalAction }}</span>
          <img src="@/assets/icons/close.png" @click="modalStore.closeModal" class="close-icon"/>
        </div>
        <div class="modal-center">
          <div class="modal-body">
            <img class="warning-icon" :src="modalImage" alt="Modal Image" />
            <p class="warning-message" v-html="modalStore.message"></p>
          </div>
          <div class="modal-footer">
            <button class="close-button" @click="modalStore.closeModal">확인</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
<script setup>
import { useUtilModalStore } from '@/stores/utilModalStore';
import { computed } from 'vue';
const modalStore = useUtilModalStore();

// 상태에 따라 이미지 경로를 동적으로 설정
const modalImage = computed(() => {
  switch (modalStore.type) {
    case "already-exist":
      return new URL('@/assets/modal/already-exist.gif', import.meta.url).href;
    case "alert":
      return new URL('@/assets/modal/warning.gif', import.meta.url).href;
    case "add-book":
      return new URL('@/assets/modal/success_add_book.gif', import.meta.url).href;
    default:
      return new URL('@/assets/modal/success.gif', import.meta.url).href;
  }
});
</script>
  
  <style scoped>
  /* 모달 전체 영역 */
  .modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  /* 모달 컨테이너 */
  .modal-container {
    background: #ffffff;
    border-radius: 10px 10px 0px 0px;
    width: 400px;
    height: 250px;
    max-width: 90%;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  
  /* 모달 헤더 */
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #FFFDF1;
    border-radius: 10px;
    padding: 10px 12px;
    height: 15%;
  }
  
  .modal-header span {
    font-size: 13px;
    color: black;
  }
  
  .close-button {
    background: none;
    border: none;
    font-size: 18px;
    cursor: pointer;
    color: #555555;
    margin-top: 10px;
  }
  
  /* 경고 아이콘 */
  .warning-icon {
    display: block;
    margin: 0 auto;
    width: 50px;
    height: 50px;
  }
  
  .warning-message {
    text-align: center;
    font-size: 15px;
    font-weight: bold;
    color: #555555;
    margin: 10px 0;
  }
  
  /* 버튼 영역 */
  .modal-footer {
    display: flex;
    justify-content: center;
    gap: 10px;
  }
  
 .close-button {
    background: #ffffff;
    color: #555353;
    border: 1px solid #555353;
    border-radius: 20px;
    height: 35px;
    padding: 0px 20px;
    font-size: 12px;
    cursor: pointer;
  }

  .close-icon {
    width: 9px;
    height: 9px;
    cursor: pointer;
  }

  .modal-center {
    display: grid;
    height: 83%;
    align-content: center;
  }

  .modal-body {
    display: flex;
    flex-flow: column;
    align-items: center;
  }
  </style>
  