<template>
    <div v-if="modalStore.isModalVisible" class="modal-overlay">
      <div class="modal-container">
        <div class="modal-header">
          <span>{{ modalStore.modalActionMessage }}</span>
          <img src="@/assets/icons/close.png" @click="modalStore.closeModal" class="close-icon"/>
        </div>
        <div class="modal-body">
          <img class="warning-icon" :src="modalImage" alt="Modal Image" />
          <p class="modal-title" v-html="modalStore.questionMessage"></p>
          <p class="modal-subtitle">{{ modalStore.warningMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="delete-button" @click="handleConfirmAction">{{ modalStore.cofirmButton }}</button>
          <button class="cancel-button" @click="modalStore.closeModal">취소</button>
        </div>
      </div>
    </div>
  </template>
  
<script setup>
import { useConfirmModalStore } from '@/stores/utilModalStore';
import { computed } from 'vue';
const modalStore = useConfirmModalStore();

// 확인 버튼에 따른 동작 처리 함수
const handleConfirmAction = ()  => {
  if (modalStore.confirmCallback) {
    modalStore.confirmCallback(); // 콜백 함수 실행
  }
  modalStore.closeModal(); // 모달 닫기
};

// 상태에 따라 이미지 경로를 동적으로 설정
const modalImage = computed(() => {
  switch (modalStore.type) {
    case "already-exist":
      return new URL('@/assets/modal/already-exist.gif', import.meta.url).href;
    default:
      return new URL('@/assets/modal/warning.gif', import.meta.url).href;
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
    margin-bottom: 20px;
    background-color: #FFFDF1;
    border-radius: 10px;
    padding: 10px 12px;
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
  }
  
  /* 경고 아이콘 */
  .warning-icon {
    display: block;
    margin: 0 auto;
    width: 60px;
    height: 60px;
  }
  
  /* 모달 제목 및 부제목 */
  .modal-title {
    text-align: center;
    font-size: 17px;
    font-weight: bold;
    color: #555555;
    margin: 10px 0;
  }
  
  .modal-subtitle {
    text-align: center;
    font-size: 14px;
    color: #f55858;
    margin: 10px 0;
  }
  
  /* 버튼 영역 */
  .modal-footer {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 23px;
  }
  
  .delete-button {
    background: #222521;
    color: #ffffff;
    border: none;
    border-radius: 20px;
    height: 35px;
    padding: 0px 20px;
    font-size: 12px;
    cursor: pointer;
  }
  
  .cancel-button {
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
  </style>
  