<template>
    <div class="search-container">
    <!-- Search Results -->
    <div class="search-results" v-if="searchBooks.length > 0">
        <div
        v-for="book in searchBooks"
        :key="book.isbn"
        class="book-item"
        @click="openModal(book)"
        >
        <img :src="book.thumbnail" alt="Book Cover" class="book-cover" />
        <div>
            <p class="book-title">{{ book.title }}</p>
            <p class="book-author">{{ book.authors.join(', ') }}</p>
        </div>
        </div>
    </div>
    <div class="search-results" v-else="searchBooks.legnth = 0">
        <p class="no-results">검색 결과가 없습니다</p>
    </div>

    <!-- Pagination Controls -->
    <div class="pagination" v-if="totalPages > 1 && searchPerformed">
        <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1">
        이전
        </button>
        <span>페이지 {{ currentPage }} / {{ totalPages }}</span>
        <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages">
        다음
        </button>
    </div>

    <!-- Modal for Book Details -->
    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
        <div class="modal-content">
        <span class="close-button" @click="closeModal">&times;</span>
        <img :src="selectedBook.thumbnail" alt="Book Cover" class="modal-cover" />
        <h2>{{ selectedBook.title }}</h2>
        <p><strong>저자:</strong> {{ selectedBook.authors.join(', ') }}</p>
        <p><strong>ISBN:</strong> {{ selectedBook.isbn }}</p>
        <p><strong>출판사:</strong> {{ selectedBook.publisher }}</p>
        <p><strong>출판일:</strong> {{ selectedBook.datetime }}</p>
        <p><strong>가격:</strong> {{ selectedBook.sale_price }}원</p>
        <p><strong>줄거리:</strong> {{ selectedBook.contents }}</p>
        <a :href="selectedBook.url" target="_blank" style="color: blue;">자세히 보기</a>
        </div>
    </div>
    </div>
    <MusicPlayer/>
</template>

<script>
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import MusicPlayer from "@/components/layouts/musicPlayer.vue";

export default {
    setup() {
    // 라우트에서 쿼리 파라미터 가져오기
    const route = useRoute();
    const query = ref(route.query.q || "");

    // 상태 관리
    const searchBooks = ref([]);
    const searchPerformed = ref(false);
    const loading = ref(false);
    const errorMessage = ref("");
    const showModal = ref(false);
    const selectedBook = ref({});

    // 페이징 관련 상태
    const currentPage = ref(1);
    const totalCount = ref(0);
    const totalPages = ref(0);

    // Kakao REST API 키 (보안 상 노출에 유의하세요)
    const REST_API_KEY = "22cf152145b49b1dc175b440509f3e1a"; // 실제 REST API 키로 교체하세요.

    // Kakao Search API 설정
    const apiUrl = "https://dapi.kakao.com/v3/search/book";

    // 데이터 처리 함수
    const fetchSearchBooks = async (searchQuery, page = 1) => {
        if (!searchQuery.trim()) {
        alert("검색어를 입력하세요.");
        searchBooks.value = [];
        searchPerformed.value = false;
        return;
        }

        loading.value = true;
        errorMessage.value = "";

        try {
        console.log(`Fetching books for query: "${searchQuery}", page: ${page}`);
        const url = `${apiUrl}?query=${encodeURIComponent(searchQuery)}&sort=accuracy&page=${page}&size=10&target=title`;

        const response = await fetch(url, {
            method: "GET",
            headers: {
            "Authorization": `KakaoAK ${REST_API_KEY}`,
            "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        searchPerformed.value = true;

        if (data && data.documents) {
            searchBooks.value = data.documents.map((book) => ({
            title: book.title || "제목 없음",
            authors: book.authors || ["저자 없음"],
            thumbnail: book.thumbnail || "https://via.placeholder.com/50x75?text=No+Cover",
            isbn: book.isbn || "정보 없음",
            publisher: book.publisher || "정보 없음",
            datetime: book.datetime || "정보 없음",
            sale_price: book.sale_price || "정보 없음",
            contents: book.contents || "설명이 없습니다.",
            url: book.url || "#",
            }));

            // 페이징 정보 업데이트
            totalCount.value = data.meta.total_count;
            totalPages.value = Math.ceil(totalCount.value / 10);
            currentPage.value = page;

            console.log("Search Books:", searchBooks.value);
            console.log(`Total Count: ${totalCount.value}, Total Pages: ${totalPages.value}`);
        } else {
            searchBooks.value = [];
            console.warn("No books found in the response:", data);
        }
        } catch (error) {
        console.error("Error fetching Search Books data:", error);
        errorMessage.value = "검색 중 오류가 발생했습니다.";
        searchBooks.value = [];
        } finally {
        loading.value = false;
        }
    };

    // 페이지 변경 함수
    const goToPage = (page) => {
        if (page < 1 || page > totalPages.value) return;
        fetchSearchBooks(query.value, page);
    };

    // Watcher 설정: 라우트 쿼리 변경 시 검색 실행
    watch(
        () => route.query.q,
        (newQuery) => {
        if (newQuery) {
            fetchSearchBooks(newQuery, 1); // 새로운 검색 시 첫 페이지로 설정
            console.log(`New Query: "${newQuery}"`);
        } else {
            searchBooks.value = [];
            searchPerformed.value = false;
            currentPage.value = 1;
            totalCount.value = 0;
            totalPages.value = 0;
        }
        },
        { immediate: true }
    );

    // onMounted 훅 추가: 컴포넌트가 마운트될 때 초기 검색 수행
    onMounted(() => {
        if (query.value) {
        fetchSearchBooks(query.value, 1);
        }
    });

    // Modal 제어 함수
    const openModal = (book) => {
        selectedBook.value = book;
        showModal.value = true;
    };

    const closeModal = () => {
        showModal.value = false;
    };

    return {
        searchBooks,
        loading,
        errorMessage,
        searchPerformed,
        showModal,
        selectedBook,
        openModal,
        closeModal,
        currentPage,
        totalPages,
        goToPage,
    };
    },
};
</script>

<style scoped>
.search-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0.5rem;
}

.search-results {
    margin: 20px 25px;
}

.book-item {
    display: flex;
    align-items: center;
    padding: 20px;
    border-top: 1px solid #eaeaea;
    border-bottom: 1px solid #eaeaea;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.book-item:hover {
    background-color: #f9f9f9;
}

.book-cover {
    width: 100px;
    height: 150px;
    object-fit: cover;
    margin-right: 15px;
    border-radius: 4px;
}

.book-title {
    font-size: 20px;
    font-weight: bold;
    margin: 0;
}

.book-author {
    font-size: 17px;
    color: gray;
    margin: 5px 0 0 0;
}

.no-results {
    margin: 20px 25px;
    text-align: center;
    font-size: 18px;
    color: gray;
}


/* Pagination Controls */
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 20px 0;
}

.pagination button {
    background-color: #007bff;
    color: white;
    border: none;
    padding: 8px 12px;
    margin: 0 10px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.pagination button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
    background-color: #0056b3;
}

/* Modal Styles */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 100;
}

.modal-content {
    background-color: #ffffff;
    padding: 20px;
    border-radius: 8px;
    width: 500px;
    max-height: 80vh;
    overflow-y: auto;
    position: relative;
}

.close-button {
    position: absolute;
    top: 10px;
    right: 15px;
    font-size: 24px;
    cursor: pointer;
}

.modal-cover {
    width: 150px;
    height: auto;
    object-fit: cover;
    display: block;
    margin: 0 auto 20px auto;
}

.modal-content h2 {
    text-align: center;
    margin-bottom: 10px;
}

.modal-content p {
    margin: 5px 0;
}
</style>
