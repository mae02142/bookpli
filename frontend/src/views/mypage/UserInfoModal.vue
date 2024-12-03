<template>
    <div class="modal-container">
      <div class="modal-overlay" @click="closeModal"></div>
      <div class="modal-content">
        <img class="user-avatar" src="@/assets/images/default_image.png" alt="User Avatar" />
        <h2 class="modal-title">회원 정보 확인</h2>
        <div class="input-grid">

          <div class="info-section">
            <label class="info-label">이름</label>
            <div class="info-box">
              <input type="text" class="info-input" v-model="userName" readonly />
            </div>
          </div>
    
          <div class="info-section">
            <label class="info-label">이메일</label>
            <div class="info-box">
              <input type="text" class="info-input" v-model="userEmail" readonly />
            </div>
          </div>
    
          <div class="info-section">
            <label class="info-label">Spotify User ID</label>
            <div class="info-box">
              <input type="text" class="info-input" v-model="spotifyUserId" readonly />
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const userName = ref('USER12345');
  const userEmail = ref('USER@aaa.com');
  const spotifyUserId = ref('aaaaaaaaa');
  
  const closeModal = () => {
    // 모달 닫기 로직을 여기에 추가하세요
    console.log('Modal Closed');
  };
  </script>
  
  <style scoped>
  .modal-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.6);
    z-index: 1000;
  }
  
  .modal-overlay {
    position: absolute;
    width: 100%;
    height: 100%;
    background: transparent;
  }
  
  .modal-content {
    position: relative;
    background: #ffffff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
    z-index: 1001;
    max-width: 500px;
    width: 90%;
  }
  
  .user-avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    display: block;
    margin: 0 auto 20px;
  }
  
  .modal-title {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
  }
  
  .info-section {
    margin-bottom: 15px;
  }
  
  .info-label {
    font-size: 18px;
    font-weight: 500;
    color: #000;
    margin-bottom: 5px;
    display: block;
  }
  
  .info-box {
    position: relative;
  }
  
  .info-input {
    width: 100%;
    padding: 10px;
    border: 1px solid #555553;
    border-radius: 5px;
    font-size: 16px;
    color: #555553;
  }

  .input-grid {
    display: flex;
    flex-direction: column;
    width: 80%;
    place-self: center;

  }
  </style>
  