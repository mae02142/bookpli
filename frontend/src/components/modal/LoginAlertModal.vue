<template>
  <div v-if="modalStore.isModalVisible" class="login-modal-overlay">
    <div class="login-modal-container">
      <div class="login-modal-header">
        <span>{{ modalStore.modalActionMessage }}</span>
        <img src="@/assets/icons/close.png" @click="modalStore.cancel" class="close-icon" />
      </div>
      <div class="login-modal-body">
        <img class="warning-icon" src="@/assets/modal/need_login.gif" alt="Modal Image" />
        <p class="login-modal-title">{{ modalStore.message }}</p>
      </div>
      <div class="login-modal-footer">
        <button @click="modalStore.confirm" class="login-button">로그인 하기</button>
        <button @click="modalStore.cancel" class="reset-button">
          돌아가기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useLoginAlertModalStore } from "@/stores/utilModalStore";
const modalStore = useLoginAlertModalStore();

// 확인 버튼에 따른 동작 처리 함수
const handleConfirmAction = () => {
  if (modalStore.confirmCallback) {
    modalStore.confirmCallback(); // 확인 콜백 함수 실행
  }
  modalStore.closeModal(); // 모달 닫기
};

// 취소 버튼에 따른 동작 처리 함수
const handleCancelAction = () => {
  if (modalStore.cancelCallback) {
    modalStore.cancelCallback(); // 취소 콜백 함수 실행
  }
  modalStore.closeModal(); // 모달 닫기
};

// 모달 닫기 동작
const handleCloseModal = () => {
  modalStore.closeModal();
};
</script>
  
  <style scoped>
  /* 모달 전체 영역 */
  .login-modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  /* 모달 컨테이너 */
  .login-modal-container {
    background: #ffffff;
    border-radius: 10px 10px 0px 0px;
    width: 400px;
    height: 250px;
    max-width: 90%;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  
  /* 모달 헤더 */
  .login-modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    background-color: #FFFDF1;
    border-radius: 10px;
    padding: 15px 12px;
  }
  
  .login-modal-header span {
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
  .login-modal-title {
    text-align: center;
    font-size: 17px;
    font-weight: bold;
    color: #555555;
    margin: 10px 0;
  }
 
  /* 버튼 영역 */
  .login-modal-footer {
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

  .login-button {
    background: #222521;
    color: #ffffff;
    border: none;
    border-radius: 20px;
    height: 35px;
    padding: 0px 20px;
    font-size: 12px;
    cursor: pointer;
  }

  .reset-button {
    background: #ffffff;
    color: #555353;
    border: 1px solid #555353;
    border-radius: 20px;
    height: 35px;
    padding: 0px 20px;
    font-size: 12px;
    cursor: pointer;
  }
  </style>
  