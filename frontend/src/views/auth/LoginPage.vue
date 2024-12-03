<template>
    <div class="container">
      <div class="login-sidebar">
        <img class="book-image" src="@/assets/logos/mascot.png" />
        <div class="brand-name">Bookpli.</div>
      </div>
      <div class="content-wrapper">
        <div class="header">
          <img src="@/assets/icons/left_arrow.png" class="left-arrow"/>
          <span class="home-text" @click="goHome">홈으로</span>
        </div>
        <div class="main-content">
          <p class="title">플레이리스트와 독서 추천을 한번에</p>
          <p class="subtitle">인생책과 노래를 찾아보세요:)</p>
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
import { useRouter } from 'vue-router';

const router = useRouter();

function goHome() {
  router.push({ path: '/' });
}

const handleLogin = async () => {
  window.location.href = `${import.meta.env.VITE_API_URL}/api/auth/login`; // 서버가 리다이렉트를 처리하도록 직접 URL 이동
};
</script>
  
  <style>
  .container,
  .container * {
    box-sizing: border-box;
  }
  .container {
    display: flex;
    height: 100vh;
    width: 100%;
  }
  .login-sidebar {
    background: #fffdf1;
    width: 50%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  .book-image {
    width: 400px;
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
    width: 50%;
    padding: 30px;
    background: #ffffff;
  }
  .header {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .home-text {
    color: #000;
    font-size: 20px;
    font-weight: 400;
  }

  .home-text:hover{
    cursor: pointer;
  }

  .main-content {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    height: 100%;
    justify-content: center;
    align-self: center;
  }
  .title {
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
    width: 40px;
    height: 40px;
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
  </style>
