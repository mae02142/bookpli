<template>
  <header class="header">
    <div class="left-section">
      <div class="logo" @click="goHome">bookpli</div>
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
      <input
        type="text"
        class="search-input"
        placeholder="검색어 입력"
        @keypress.enter="fetchSearchBooks"
        v-model="searchQuery"
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

  <!-- Display Search Results -->
  <div v-if="searchBooks.length" class="search-results">
    <h2>검색 결과:</h2>
    <ul>
      <li v-for="(book, index) in searchBooks" :key="index">
        <img :src="book.cover" alt="Book Cover" v-if="book.cover" />
        <div>
          <strong>{{ book.title }}</strong><br />
          <em>{{ book.author }}</em>
        </div>
      </li>
    </ul>
  </div>
</template>
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
        <img class="search-icon" src="@/assets/icons/search.png" alt="Search Icon" />
        <input type="text" class="search-input" placeholder="Q">
      </div>
      <router-link v-if="!isAuthenticated" to="/auth/login">
        <div class="log-button">
          <img src="@/assets/icons/logout.png" alt="로그인 아이콘">
          <span>LOGIN</span>
        </div>
      </router-link>
      <div v-else @click="handleLogout" class="log-button">
        <img src="@/assets/icons/login.png" alt="로그아웃 아이콘">
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

function goHome() {
  router.push({ path: "/main" });
}

const isAuthenticated = computed(() => authStore.isAuthenticated);

const handleLogout = () => {
  authStore.clearAuthData();
  router.push({ path: "/main" });
};

const searchBooks = ref([]);
const searchQuery = ref("");
const apiUrl = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx"; // Use HTTPS if supported
const ttbKey = "ttbyoungjae.bae1809001";

// Search Books using JSONP
const fetchSearchBooksJSONP = (query) => {
  return new Promise((resolve, reject) => {
    if (!query.trim()) {
      alert("검색어를 입력하세요.");
      resolve([]); // Resolve with empty results to avoid errors
      return;
    }

    const callbackName = `jsonpCallback_${Date.now()}`;
    const script = document.createElement("script");

    // Construct the API URL without line breaks
    const url = `${apiUrl}?ttbkey=${ttbKey}&QueryType=Keyword&Query=${encodeURIComponent(query)}&MaxResults=5&start=1&SearchTarget=Book&output=js&Version=20131101&callback=${callbackName}`;
    console.log("Fetching JSONP URL:", url); // Debug log
    script.src = url;

    // Define the global callback
    window[callbackName] = (response) => {
      if (!response) {
        console.warn("No response received from the API.");
        reject(new Error("Empty API response"));
      } else {
        console.log("API Response:", response);
        resolve(response);
      }
      delete window[callbackName];
      document.body.removeChild(script);
    };

    // Append the script to the document
    document.body.appendChild(script);
  });
};

const fetchSearchBooks = async () => {
  try {
    const searchBooksData = await fetchSearchBooksJSONP(searchQuery.value);
    if (searchBooksData && searchBooksData.item) {
      searchBooks.value = searchBooksData.item.map((book) => ({
        title: book.title || "제목 없음",
        author: book.author || "저자 없음",
        cover: book.cover || "",
      }));
    } else {
      searchBooks.value = [];
      console.warn("No books found in the response:", searchBooksData);
    }
  } catch (error) {
    console.error("Error fetching Search Books data:", error);
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

/* Styles for Search Results */
.search-results {
  margin: 20px 25px;
}

.search-results ul {
  list-style: none;
  padding: 0;
}

.search-results li {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.search-results img {
  width: 50px;
  height: auto;
  margin-right: 15px;
}
</style>
