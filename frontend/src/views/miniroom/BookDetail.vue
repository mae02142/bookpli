<template>
    <div class="book-detail-container">
        <!-- 기존 템플릿 구조 유지 -->
        <div class="book-details">
            <!-- 책 커버 섹션 -->
            <div class="book-cover-section">
                <img class="book-cover" :src="book.cover"/>
            </div>

            <!-- 책 정보 섹션 -->
            <div class="book-info-section">
                <div class="book-info-contents" >
                    <div class="title-and-author">
                        <h1 class="book-title">{{ book.title }}</h1>
                        <span>지은이: {{ book.author }}</span>
                    </div>

                    <div class="book-intro-grid">
                        <p class="book-intro-header">책소개</p>
                        <p class="book-intro" v-html="book.description"></p>
                    </div>

                    <div class="book-detail-grid">
                        <div class="book-detail-grid-first">
                            <span class="book-meta">출판일: {{ book.pubdate }}</span>
                            <span class="book-meta">쪽수: {{ book.startindex }}쪽</span>
                            <span class="book-meta">ISBN: {{ book.isbn13 }}</span>
                        </div>
                        <div>
                            <span class="book-meta">출판사: {{ book.publisher }}</span>
                        </div>
                    </div>
                </div>

                <!-- 도서 상태 관리 섹션 -->
                <div class="book-status-grid">
                    <div class="book-status-goal" @click="openModal">
                        <img src="@/assets/icons/book_option.png" class="register-status-icon">
                        <span v-if="bookInLibrary?.status === 'reading'">독서 상태 편집</span>
                        <span v-else-if="bookInLibrary?.status === 'wished'">바로 독서 설정</span>
                        <span v-else>내 서재에 담고 바로 독서 설정</span>
                    </div>
                    <div class="book-status-wish">
                        <img src="@/assets/icons/add_book_shelf.png">
                        <span v-if="isInLibrary" @click="handleDeleteBook">내 서재에서 삭제하기</span>
                        <span v-else @click="handleAddBook">내 서재에 담기</span>
                    </div>
                    <div class="book-status-like">
                        <img
                            :src="isLiked ? likeImage : dislikeImage"
                            class="detail-icons"
                            @click="likeAndToggle(book)"
                        />
                    </div>
                </div>

                <!-- 모달 -->
                <ReadGoalModal
                    :visible="showModal"
                    :rbook="bookStore.rbook"
                    @close="handleModalClose"
                    @updateRbook="updateBookInStore"
                />
            </div>
        </div>

        </div>


        <!-- 추천 도서 및 리뷰 섹션 -->
        <div class="recommendations">
            <div class="line-separator"></div>
            <div class="tabs">
                <button class="tab" @click="setActiveTab('recommend')">추천도서</button>
                <button class="tab" @click="setActiveTab('review')">리뷰</button>
            </div>
            <div class="line-separator"></div>

            <div class="recommendation-covers" v-if="activeTab === 'recommend'">
                <Recommend
                    :recommendations="recommendations"
                    @recomBook="recomBookClick"
                />
            </div>
            <BookReview
                v-if="activeTab === 'review'"
                :isbn13="book.isbn13"
            />
        </div>

        <!-- 음악 플레이어 -->
        <div class="music-player">
            <MusicPlayer/>
        </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import BookReview from "@/components/review/BookReview.vue";
import ReadGoalModal from "@/components/readGoal/ReadGoalModal.vue";
import apiClient from "@/api/axiosInstance";
import { useAuthStore } from "@/stores/auth";
import { useUtilModalStore } from "@/stores/utilModalStore";
import { useBookStore } from "@/stores/bookStore";

import Recommend from "@/components/recommBooks/Recommend.vue";
import { addBookLike, removeBookLike } from "@/utils/likeUtils";
import dislikeImage from '@/assets/icons/dislike_lightgray.png';
import likeImage from '@/assets/icons/like.png';
import MusicPlayer from '@/components/layouts/musicPlayer.vue';

// 상태 관리
const route = useRoute();
const authStore = useAuthStore();
const utilModalStore = useUtilModalStore();
const isbn13 = ref(route.params.isbn13);

const book = ref({});
const bookInLibrary = ref({});
const isInLibrary = ref(false);
const libraryId = ref("");
const activeTab = ref("recommend");
const bookLikedId = ref(null);
const isLiked = ref(false);
const recommendations = ref();

const bookStore= useBookStore();

const recomBookClick = async (recomBook) => {
    isbn13.value = recomBook.isbn13; 
    await loadBookDetail(); 
};


const setActiveTab= (tab) => {
    activeTab.value= tab;
};

//모달 
const showModal = ref(false);

const openModal = async () => {
    try {
        await checkLibraryStatus(); // 데이터 로딩 완료 후 실행

        if (bookInLibrary.value && bookInLibrary.value.isbn13) {
            // Pinia 상태 업데이트
            bookStore.setbook({ ...bookInLibrary.value });

            console.log("Pinia 저장된 rbook:", bookStore.rbook.data);
            showModal.value = true;
        } else {
            console.error("도서정보가 없습니다.", bookInLibrary.value);
        }
    } catch (error) {
        console.error("openModal 실행 중 오류:", error);
    }
};

const closeModal = (updatedBook) => {
    if (updatedBook) {
        // 상위 컴포넌트 데이터 동기화
        Object.assign(book.value, updatedBook);
    }
    showModal.value = false; // 모달 닫기
};

const updateBookInStore = (updatedBook) => {
    // Pinia 상태 업데이트
    bookStore.setbook(updatedBook);
};

// 도서 상세 정보 로드
const loadBookDetail = async () => {
    try {
        const response = await apiClient.get(`/api/book/${isbn13.value}`);
        book.value = response.data.data;
        await checkLibraryStatus();
    } catch (error) {
        console.error("도서 상세 정보를 불러오는 중 오류 발생:", error);
        book.value = {};
    }
};

const checkLibraryStatus = async () => {
    try {
        const response = await apiClient.get('/api/library');

        // 응답이 배열인지, 객체인지 처리
        const libraryItems = Array.isArray(response.data.data)
            ? response.data.data
            : [response.data.data]; // 객체인 경우 배열로 변환

        // bookInLibrary 값 설정
        const existingBook = libraryItems.find(
            (item) => item.isbn13 === book.value.isbn13
        );

        if (existingBook) {
            isInLibrary.value = true; // 서재 등록 상태 true
            libraryId.value = existingBook.libraryId; // 라이브러리 ID 저장
            bookInLibrary.value = existingBook; // 도서 정보 저장
        } else {
            isInLibrary.value = false;
            libraryId.value = null;
            bookInLibrary.value = {
                isbn13: book.value.isbn13, 
                title: book.value.title,
                cover: book.value.cover,
                author: book.value.author,
                startindex: book.value.startindex,
            }; 
        }
    } catch (error) {
        console.error("내 서재 상태 확인 오류:", error);
        isInLibrary.value = false;
        libraryId.value = null;
        bookInLibrary.value = {};
    }
};

// 도서 추가
const handleAddBook = async () => {
    try {
        const response = await apiClient.post(`/api/library/${isbn13.value}`);
        bookInLibrary.value = response.data.data;
        isInLibrary.value = true;
        libraryId.value = bookInLibrary.value.libraryId;

        utilModalStore.showModal(
            '도서 담기',
            `내 서재에 ${book.value.title}<br>도서가 저장되었습니다.`,
            'add-book'
        );
    } catch (error) {
        utilModalStore.showModal('오류', '이미 담은 책입니다.', 'error');
    }
};

// 도서 삭제
const handleDeleteBook = async () => {
    if (!libraryId.value) return;

    try {
        await apiClient.delete(`/api/library/${libraryId.value}`);
        isInLibrary.value = false;
        libraryId.value = null;
        bookInLibrary.value = {};
        book.value.status = '';
    } catch (error) {
        utilModalStore.showModal('오류', '도서 삭제 중 오류가 발생했습니다.', 'error');
    }
};

// 좋아요 관련 함수들
const likeordislike = async () => {
    try {
        const response = await apiClient.get(`/api/library/book/${isbn13.value}`);
        const likedId = response.data.data;
        bookLikedId.value = likedId;
        isLiked.value = !!likedId;
    } catch (error) {
        console.error("찜한 도서 확인 중 오류 발생:", error.message || error);
    }
};

const likeAndToggle = async (book) => {
    try {
        if (isLiked.value) {
            const isRemoved = await removeBookLike(bookLikedId.value);
            if (isRemoved) {
                bookLikedId.value = null;
                isLiked.value = false;
            }
        } else {
            const likedId = await addBookLike(book.isbn13);
            bookLikedId.value = likedId;
            isLiked.value = true;
        }
    } catch (error) {
        console.error("좋아요 토글 중 오류 발생:", error.message || error);
    }
};

const handleModalClose = (updatedBook) => {
    if (updatedBook) {
        Object.assign(book.value, updatedBook);
    }
    closeModal();
};


// 도서 상태 로드
const loadUserGoalExist = async () => {
    try {
        const response = await apiClient.get(`/api/library/${authStore.user.userId}/${book.value.isbn13}`);
        bookInLibrary.value = response.data.data || {};
        book.value.status = bookInLibrary.value?.status || "";
    } catch (error) {
        console.error("목록에서 책 상태 가져오기 오류:", error);
        bookInLibrary.value = {};
    }
};

watch(() => isbn13.value, async (newIsbn) => {
    if (newIsbn) {
        await loadBookDetail(); 
        await likeordislike();
        await loadUserGoalExist();
    }
});


// 컴포넌트 마운트
onMounted(async () => {
    try {
        await loadBookDetail();
        if (!book.value.isbn13) return; 
        await Promise.all([likeordislike(), loadUserGoalExist()]);
    } catch (error) {
        console.error("초기 데이터 로드 중 오류:", error);
    }

    await likeordislike();
    await loadUserGoalExist();
});


</script>

<style>
* {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

body {
    background: #ffffff;
    font-family: "Inter", sans-serif;
}

.book-details {
    display: flex;
    flex-direction: row;
    gap: 20px;
    margin: 20px auto;
    width: 90%;
    max-width: 1200px;
    align-items: flex-start;
    align-items: center;
}

.book-cover-section {
    background: rgba(245, 245, 220, 0.6);
    border-radius: 35px;
    padding: 20px;
    flex: 0.7;
    display: flex;
    align-items: center;
    justify-content: center;
}

.book-cover {
    width: 200px;
    height: 280px;
    object-fit: cover;
}

.book-info-section {
    position: relative;
    background: #ffffff;
    border: 1px solid #cccccc;
    border-radius: 8px;
    flex: 2;
    min-height: 310px;
}

.book-title {
    font-size: 22px;
    font-weight: 700;
    color: #000000;
}

.book-subtitle {
    font-family: "Inter-Bold", sans-serif;
    font-size: 20px;
    font-weight: 700;
    color: #000000;
    margin-top: 20px;
}

.book-meta {
    display: flex; 
    flex-wrap: wrap; 
    gap: 10px; 
    font-size: 14px;
    color: #000000;
}
.book-intro-grid {
    display: grid;
    gap: 3px;
}

.book-intro-header {
    font-size: 13px;
    margin-top: 20px;
}

.book-intro {
    font-size: 14px;
    font-weight: 400;
    color: #000000;
    margin-bottom: 20px;
    line-height: 1.4;
}

.recommendations {
    margin: 40px auto;
    width: 90%;
    max-width: 1200px;
}

.recommendations-title {
    font-size: 24px;
    font-weight: 400;
    color: #000000;
    margin-bottom: 20px;
}

.recommendation-covers {
    display: flex;
    justify-content: space-between;
}

.line-separator {
    border-top: 1px solid #000000;
    width: 100%;
}

.reviews {
    margin: 20px auto;
    text-align: center;
}

.reviews-title {
    font-size: 24px;
    font-weight: 400;
    color: #000000;
    margin-bottom: 10px;
}

.review-button {
    display: inline-block;
    font-family: "Inter-Regular", sans-serif;
    font-size: 20px;
    font-weight: 400;
    color: #3e322a;
    text-align: center;
    border: 1px solid #3e322a;
    border-radius: 5px;
    padding: 10px 20px;
    cursor: pointer;
    margin-top: 20px;
}

.tabs {
    display: flex;
    justify-content: flex-start;
    margin: 20px auto;
    gap: 20px;
    margin-left: 100px;
}

.tab {
    background: none;
    border: none;
    font-size: 18px;
    font-weight: bold;
    padding: 5px 10px;
    cursor: pointer;
}

.btn-read {
    position: absolute;
    bottom: 10px; 
    right: 10px; 
    background-color: #fffdf1;
    color: #000000;
    border: none;
    border-radius: 5px;
    padding: 10px 20px;
    cursor: pointer;
}

.detail-icons{
    width: 25px;
    cursor: pointer;
}

.title-and-author {
    display: grid;
    gap: 8px;
}

.icons-container {
    display: flex; 
    gap: 10px; 
}
.book-status-grid {
    display: flex;
    height: 55px;
    text-align: center;
    height: 55px;
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
}

.book-status-goal {
    width: 45%;
    height: 100%;
    background-color: #2e2e2e;
    color: white;
    border-bottom-left-radius: 5px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
}

.book-status-wish {
    width: 45%;
    height: 100%;
    border-top: 1px solid #bbbbbb;
    border-right: 1px solid #bbbbbb;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
}

.book-status-like {
    width: 10%;
    display: flex;
    align-items: center;
    justify-content: center;
    border-top: 1px solid #bbbbbb;
}

.book-info-contents {
    padding: 30px;
}

.book-detail-grid {
    display: grid;
    gap: 5px;
}

.book-detail-grid-first {
    display: flex;
    gap: 20px;
}

.book-detail-container {
    width: 75%;
    margin-left: auto;
    margin-right: auto;
}

.register-status-icon {
    width: 20px;
}

.book-status-wish:hover {
    font-weight: bold;
}
</style>