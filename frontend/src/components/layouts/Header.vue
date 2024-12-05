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
        <input type="text" class="search-input" placeholder="검색어 입력" onkeypress="fetchSearchBooksJSONP()">
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
import { computed } from 'vue';
import { ref,onMounted } from "vue";
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();

function goHome() {
  router.push({ path: '/' });
}

const isAuthenticated = computed(() => authStore.isAuthenticated);

const handleLogout = () => {
  authStore.clearAuth();
  router.push({ path: '/' });
};


const searchBooks = ref([]);

const apiUrl = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
const ttbKey = "ttbyoungjae.bae1809001";

// Search Books
const fetchSearchBooksJSONP = () => {
        return new Promise((resolve, reject) => {
            const callbackName = `jsonpCallback_${Date.now()}`;
            const script = document.createElement("script");

            script.src = `${apiUrl}?ttbkey=${ttbKey}&QueryType=Keyword&MaxResults=5&start=1&SearchTarget=Book&output=js&Version=20131101&callback=${callbackName}`;

            script.onerror = () => {
            reject(new Error("JSONP request failed"));
            document.body.removeChild(script);
            };

            window[callbackName] = (response) => {
            console.log("API Response:", response); // 추가된 로그
            resolve(response);
            delete window[callbackName];
            document.body.removeChild(script);
            };

            document.body.appendChild(script);
        });
    };

    const fetchSearchBooks = async () => {
        try {
            const searchBooksData = await fetchSearchBooksJSONP();
            searchBooks.value = searchBooksData.item.map((book) => ({
                title: book.title,
                author: book.author,
                cover: book.cover,
            }));
        } catch (error) {
            console.error("Error fetching Search Books data:", error);
        }
    };

    onMounted(async () => {
      await fetchSearchBooks();
      console.log("Search Books after fetch:", searchBooks.value)
    });
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
    position: relative; /* 부모 요소에서 정렬할 때 유용 */
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
    position: absolute; /* 부모 요소 기준 위치 */
    left: 50%;
    transform: translateX(-50%); /* 수평 중앙 정렬 */
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
  </style>
  