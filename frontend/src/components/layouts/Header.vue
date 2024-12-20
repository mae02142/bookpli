<template>
  <header class="header">
    <div class="left-section">
      <img src="@/assets/logos/logo.png" @click="goHome" class="logo">
      <nav class="nav">
        <router-link to="/miniroom/minihome">
          <div class="header-grid nav-item">
            <img src="@/assets/icons/header_home.png">
            <span>미니룸</span>
          </div>
        </router-link>
        <router-link to="/bookclub">
          <div class="header-grid nav-item">
            <img src="@/assets/icons/header_club.png">
            <span>북적북적</span>
          </div>
        </router-link>
        <router-link to="/mypage/mypli">
          <div class="header-grid nav-item">
            <img src="@/assets/icons/music/header_note.png">
            <span>내 플리</span>
          </div>
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
        @keyup.enter="handleEnterKey"
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
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useUserStore } from "@/stores/user";
import { useUtilModalStore } from "@/stores/utilModalStore";

const query = ref("");
const searchType = ref("book");

const authStore = useAuthStore();
const router = useRouter();
const isAuthenticated = computed(() => authStore.isAuthenticated);
const userStore = useUserStore();
const utilModalStore = useUtilModalStore();

defineProps({
  query: {
    type: String,
    required: false,
    default: "",
  },
});

watch(() => query, (newQuery) => {
  console.log("Query updated:", newQuery);
});

function goHome() {
  router.push({ path: "/main" });
}

const handleLogout = () => {
  authStore.clearAuthData();
  userStore.clearAccessToken();
  router.push({ path: "/main" });
};

const resetSearch = () => {
  query.value = ""; // 검색창 초기화
};

const submitSearch = () => {
  const searchQuery = query.value.trim();
  if (searchQuery) {
    const routePath = searchType.value === "book" ? "/search-book" : "/search-music";
    router.replace({ path: routePath, query: { q: searchQuery, type: searchType.value } });
    resetSearch();
  }
};

const handleEnterKey = () => {
  const searchQuery = query.value.trim();
  if (!searchQuery) {
    return;
  }
  submitSearch(); // 검색 실행
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
  align-items: center;
}

.nav-item {
  font-size: 15px;
  color: #000000;
  font-weight: bold;
}

.nav-item:hover {
  border-bottom: 2px solid black;
}

.search-bar {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-icon {
  height: auto;
  cursor: pointer;
}

.search-input {
  flex: 1;
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 20px;
  padding: 0 15px;
  font-size: 16px;
  outline: none;
  width: 230px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
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
  background-color: #3a3a3a;
  border: none;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
</style>
