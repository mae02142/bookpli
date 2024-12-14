<template>
  <div class="spotify-search">
    <header class="header">
      <div class="left-section">
        <img src="@/assets/logos/logo.png" @click="goHome" class="logo">
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
        <select v-model="searchType" class="search-type-selector">
          <option value="book">책</option>
          <option value="music">음악</option>
        </select>
        <img class="search-icon" src="@/assets/icons/search.png" alt="Search Icon" @click="submitSearch"/>
        <input
          type="text"
          v-model="query"
          @keyup.enter="submitSearch"
          placeholder="검색어를 입력하세요"
          class="search-input"
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
    </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const query = ref("");
const searchType = ref("book");

const authStore = useAuthStore();
const router = useRouter();
const isAuthenticated = computed(() => authStore.isAuthenticated);

function goHome() {
  router.push({ path: "/main" });
}

const handleLogout = () => {
  authStore.clearAuthData();
  router.push({ path: "/main" });
};

const submitSearch = () => {
  const searchQuery = query.value.trim();
  if (searchQuery) {
    if (searchType.value === "book") {
      router.push({ path: "/search-book", query: { q: searchQuery, type: "book" } });
    } else if (searchType.value === "music") {
      router.push({ path: "/search-music", query: { q: searchQuery, type: "music" } });
    }
  } else {
    alert("검색어를 입력하세요.");
  }
};
</script>

<style scoped>
.spotify-search {
  font-family: Arial, sans-serif;
}

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
  cursor: pointer;
}

.nav {
  display: flex;
  gap: 30px;
  margin-left: 60px;
  margin-top: 12px;
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

.search-type-selector {
  margin-right: 10px;
  padding: 5px;
  border: none;
  background: transparent;
  font-size: 17px;
}

.log-button {
  display: flex;
  font-size: 16px;
  cursor: pointer;
  align-items: center;
  gap: 5px;
  font-weight: bold;
}

.error {
  color: red;
}

h2 {
  margin-top: 20px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin: 5px 0;
}
</style>
