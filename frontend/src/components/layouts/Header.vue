<script setup lang="ts">
import router from '@/router';
</script>

<template>
    <header class="header">
      <div class="left-section">
        <div class="logo">bookpli</div>
        <nav class="nav">
            <router-link to="/mypage/mylibrary">
            <span class="nav-item">My서재</span>
            </router-link>
            <router-link to="/mypage/mypli">
                <span class="nav-item">My플리</span>
            </router-link>
        </nav>
      </div>
      <div class="search-bar">
        <img class="search-icon" src="@/assets/icons/search.png" alt="Search Icon" />
        <input type="text" class="search-input" placeholder="Q">
      </div>
      <router-link to="/auth/login">
          <button class="login-button">LOGIN</button>
      </router-link>
    </header>
  </template>
  
  <style scoped>
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #ffffff;
    height: 75px;
    padding: 0 25px;
    box-sizing: border-box;
    position: relative; /* 부모 요소에서 정렬할 때 유용 */
  }
  
  .left-section {
    display: flex;
    align-items: center;
  }
  
  .logo {
    font-family: "JacquesFrancois-Regular", sans-serif;
    font-size: 35px;
    color: #000000;
    margin-right: 20px;
  }
  
  .nav {
    display: flex;
    gap: 20px;
  }
  
  .nav-item {
    font-family: "Corbel-Regular", sans-serif;
    font-size: 18px;
    color: #000000;
    font-weight: bold;
  }
  
  .search-bar {
    position: absolute; /* 부모 요소 기준 위치 */
    left: 50%;
    transform: translateX(-50%); /* 수평 중앙 정렬 */
    width: 400px;
    height: 50px;
    display: flex;
    align-items: center;
    background: #eaeaea;
    border-radius: 50px;
  }
  
  .search-icon {
    height: auto;
    margin-left: 15px;
  }
  
  .search-input {
    width: 100%;
    height: 40px;
    border: none;
    background: transparent;
    font-size: 20px;
    outline: none;
    padding: 0px 10px;
  }
  
  .login-button {
    background: #f5f5dc;
    border: none;
    border-radius: 17px;
    width: 130px;
    height: 50px;
    font-family: "Corbel-Regular", sans-serif;
    font-size: 20px;
    color: #000000;
    cursor: pointer;
  }
  </style>
  