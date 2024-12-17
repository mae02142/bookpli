<template>
    <div class="login-container">
      <div class="left-side-thumbnail">
        <img class="login-thumbnail" src="@/assets/images/thumbnail3.png" />
      </div>
      <div class="content-wrapper">
        <div class="login-header"  @click="goHome">
          <img src="@/assets/icons/left_arrow.png" class="left-arrow"/>
          <img class="book-image" src="@/assets/logos/mascot.png" />
          <span class="home-text">홈으로</span>
        </div>
        <div class="login-main-content">
          <p class="login-title">플레이리스트와 독서 추천을 한번에</p>
          <p class="subtitle">북플리에서 인생책과 노래를 찾아보세요:)</p>
          <div class="spotify-login-box" @click="handleLogin">
            <img class="spotify-logo" src="@/assets/logos/spotify_logo.png" />
            <div class="spotify-login">Spotify 로그인</div>
          </div>
          <div class="login-note">*스포티파이 회원만 음악 감상이 가능합니다</div>
        </div>
      </div>
    </div>
  </template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from 'axios';

const router = useRouter();
const spotifyLoginUrl = ref('');

function goHome() {
  router.push({ path: '/' });
}

const handleLogin = async () => {
  console.log("click!!!");
  const response = await apiClient.get('/api/auth/login');
  spotifyLoginUrl.value = response.data;
  window.location.href = spotifyLoginUrl.value;
};
</script>
  
  <style>
  .login-container {
    display: flex;
    align-content: stretch;
    height: 100vh;
    width: 100%;
  }
  .book-image {
    width: 60px;
    height: auto;
    object-fit: cover;
  }
  .brand-name {
    color: #000000;
    font-size: 60px;
    font-weight: 400;
    margin-top: -50px;
  }
  .content-wrapper {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-start;
    width: 45%;
    background: #ffffff;
    padding: 10px;
  }
  .login-header {
    display: flex;
    align-items: center;
    cursor: pointer;
  }
  .home-text {
    color: #000;
    font-size: 20px;
    font-weight: 400;
  }

  .login-main-content {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    height: 100%;
    justify-content: center;
    align-self: center;
  }
  .login-title {
    color: #000000;
    font-size: 25px;
    font-weight: 600;
  }
  .subtitle {
    color: #5a5a5a;
    font-size: 15px;
    font-weight: 400;
  }
  .spotify-login-box {
    display: flex;
    align-items: center;
    background: #fffdf1;
    border-radius: 5px;
    padding: 15px;
    gap: 10px;
    width: 100%;
    justify-content: center;
  }
  .spotify-logo {
    width: 35px;
    height: 35px;
    object-fit: cover;
  }
  .spotify-login {
    color: #000000;
    font-size: 24px;
    font-weight: 500;
  }
  .login-note {
    color: #d000ff;
    font-size: 15px;
    font-weight: 400;
    align-self: center;
  }
  .left-arrow {
    width: 20px;
    height: 20px;
  }

  .spotify-login-box:hover{
    cursor: pointer;
  }

  .login-thumbnail {
    height: 100%;
    width: fit-content;
  }

  .left-side-thumbnail {
    width: 65%;
  }
  </style>
