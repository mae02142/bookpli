<template>
  <header class="header">
    <div class="left-section">
      <div class="logo" @click="goHome">bookpli</div>
      <nav class="nav">
        <router-link to="/miniroom/minihome">
          <span class="nav-item">미니룸</span>
        </router-link>
        <router-link to="/bookclub">
          <span class="nav-item">북적북적</span>
        </router-link>
      </nav>
    </div>
    <div class="search-bar">
      <img
        class="search-icon"
        src="@/assets/icons/search.png"
        alt="Search Icon"
        @click="submitSearch"
      />
      <input
        type="text"
        class="search-input"
        placeholder="Q"
        v-model="searchQuery"
        @keyup.enter="submitSearch"
      />
    </div>
    <router-link v-if="!isAuthenticated" to="/auth/login">
      <div class="log-button">
        <img src="@/assets/icons/logout.png" alt="로그인 아이콘" />
        <span>LOGIN</span>
      </div>
    </router-link>
    <div v-else @click="handleLogout" class="log-button">
      <img src="@/assets/icons/login.png" alt="로그아웃 아이콘" />
      <span>LOGOUT</span>
    </div>
  </header>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const router = useRouter();

// Navigation Methods
function goHome() {
  router.push({ path: "/main" });
}

// Authentication State
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Logout Handler
const handleLogout = () => {
  authStore.clearAuthData();
  router.push({ path: "/main" });
};

// Search State
const searchQuery = ref("");

// Search Submission Handler
const submitSearch = () => {
  const query = searchQuery.value.trim();
  if (query) {
    router.push({ path: "/search", query: { q: query } });
  } else {
    alert("검색어를 입력하세요.");
  }
};
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  height: 75px;
  margin: 0 25px;
  box-sizing: border-box;
  position: relative;
}

.left-section {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 35px;
  color: #000000;
  margin-right: 20px;
}

.logo:hover {
  cursor: pointer;
}

.nav {
  display: flex;
  gap: 20px;
  margin-left: 30px;
  align-self: flex-end;
}

.nav-item {
  font-size: 18px;
  color: #000000;
  font-weight: bold;
}

.search-bar {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 400px;
  height: 40px;
  display: flex;
  align-items: center;
  background: #eaeaea;
  border-radius: 50px;
  padding: 0 10px;
}

.search-icon {
  height: auto;
  margin-left: 15px;
  cursor: pointer;
}

.search-input {
  width: 100%;
  height: 40px;
  border: none;
  background: transparent;
  font-size: 17px;
  outline: none;
  padding: 0px 10px;
}

.log-button {
  display: flex;
  font-size: 16px;
  cursor: pointer;
  align-items: center;
  gap: 5px;
  font-weight: bold;
}
</style>
