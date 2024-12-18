<template>
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
          <option value="book">도서</option>
          <option value="music">음악</option>
        </select>
        <input
        type="text"
        class="search-input"
        placeholder="Search.."
        v-model="query"
        @keyup.enter="submitSearch"
        />
      <div class="search-icon-grid">
        <img
          class="search-icon"
          src="@/assets/icons/search_white.png"
          alt="Search Icon"
          @click="submitSearch"
        />
      </div>
    </div>
    <router-link v-if="!isAuthenticated" to="/login">
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
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useUserStore } from "@/stores/user";

const query = ref("");
const searchType = ref("book");

const authStore = useAuthStore();
const router = useRouter();
const isAuthenticated = computed(() => authStore.isAuthenticated);
const userStore = useUserStore();
const token = userStore.accessToken;

function goHome() {
  router.push({ path: "/main" });
}

const handleLogout = () => {
  authStore.clearAuthData();
  userStore.clearAccessToken();
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

.logo:hover {
  cursor: pointer;
}

.nav {
  display: flex;
  gap: 30px;
  margin-left: 60px;
  margin-top: 12px;
}

.nav-item {
  font-size: 16px;
  color: #000000;
}

.nav-item:hover {
  border-bottom: 2px solid black;
}

.search-bar {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex; /* 검색창과 돋보기를 나란히 배치 */
  align-items: center;
  gap: 10px; /* 검색창과 돋보기 사이 간격 */
}

.search-icon {
  height: auto;
  cursor: pointer;
}

.search-input {
  flex: 1; /* 검색창이 남은 공간을 차지 */
  height: 40px; /* 검색창 높이 */
  border: 1px solid #ccc;
  border-radius: 20px; /* 둥근 모서리 */
  padding: 0 15px; /* 텍스트 좌우 패딩 */
  font-size: 16px;
  outline: none;
  width: 230px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); /* 약간의 그림자 */
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

.search-icon-grid {
  width: 40px;
  height: 40px;
  background-color: #3a3a3a; /* 돋보기 버튼 배경색 */
  border: none;
  border-radius: 50%; /* 동그란 모양 */
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); /* 약간의 그림자 */
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
