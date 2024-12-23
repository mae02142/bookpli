<template>
<div class="book-detail-container">
    <div class="book-details">
        <div class="book-cover-section">
            <img class="book-cover" :src="book.cover"/>
        </div>
    <div class="book-info-section">
        <div class="book-info-contents">
            <div class="title-and-author">
                <h1 class="book-title">{{ book.title }}</h1>
                <span>지은이: {{ book.author }}</span>
            </div>
            <!--책 상세 부분 -->

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
        <div class="book-status-grid">
            <div class="book-status-goal" @click="openModal">
                <img src="@/assets/icons/book_option.png" class="register-status-icon">
                <span v-if="bookInLibrary.status === 'reading'">독서 설정 변경</span>
                <span v-else>바로 독서 설정</span>
            </div>
            <div class="book-status-wish" @click="toggleWishList">
                <img src="@/assets/icons/add_book_shelf.png">
                <span v-if="isInLibrary">내 서재에서 삭제하기</span>
                <span v-else>내 서재에 담기</span>
            </div>
            <div class="book-status-like">
                <img :src="isLiked ? likeImage : dislikeImage" class="detail-icons" @click="likeAndToggle(book)"/>
            </div>
        </div>
        <ReadGoalModal 
            :visible="showModal"
            @close="closeModal"
        />
    </div>
</div> 

<div class="recommendations">
    <div class="line-separator"></div>    
    <div class="tabs">
        <button class="tab" @click="setActiveTab('recommend')">추천도서</button>
        <button class="tab" @click="setActiveTab('review')">리뷰</button>
    </div>
    <div class="line-separator"></div>

    <!-- 추천도서 표시 -->
    <div class="recommendation-covers" v-if="activeTab ==='recommend'">
        <Recommend v-if="activeTab ==='recommend'" :recommendations="recommendations" @recomBook="recomBookClick" />
    </div>
    <BookReview v-if="activeTab==='review'" :isbn13="book.isbn13"/>
</div>
<div class="music-player">
    <MusicPlayer/>
</div>
</div>
</template>

<script setup>
import { ref, onMounted } from "vue";
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

const route= useRoute();
const authStore = useAuthStore();
const book= ref({});
const isbn13 = route.params.isbn13;
const utilModalStore = useUtilModalStore();
const isInLibrary = ref(false); // 내 서재 상태 관리
const libraryId = ref("");
const activeTab= ref("recommend");
const bookLikedId = ref(null); // bookLikeId 저장
const isLiked = ref(false); // 좋아요 여부 상태
const recommendations = ref();
const bookInLibrary = ref({});
const bookStore= useBookStore();


const recomBookClick= (recomBook) => {
    book.value=recomBook;
}

// 찜한 도서인지 확인하는 함수
const likeordislike = async () => {
  try {
    const response = await apiClient.get(
      `/api/library/${authStore.user.userId}/book/${isbn13}`
    );
    const likedId = response.data.data;

    // likedId 값에 따라 상태 업데이트
    bookLikedId.value = likedId;
    isLiked.value = !!likedId; // likedId가 존재하면 true, 아니면 false
  } catch (error) {
    console.error("찜한 도서 확인 중 오류 발생:", error.message || error);
  }
};

// 좋아요 추가 및 삭제를 토글하는 함수
const likeAndToggle = async (book) => {
  try {
    if (isLiked.value) {
      // 이미 좋아요 상태면 삭제
      const isRemoved = await removeBookLike(bookLikedId.value);
      if (isRemoved) {
        bookLikedId.value = null; // 좋아요 ID 초기화
        isLiked.value = false; // 빈 하트 상태로 변경
      }
    } else {
      // 좋아요 추가
      const likedId = await addBookLike(authStore.user.userId, book);
      bookLikedId.value = likedId; // 새로 생성된 bookLikeId 저장
      isLiked.value = true; // 꽉 찬 하트 상태로 변경
    }
  } catch (error) {
    console.error("좋아요 토글 중 오류 발생:", error.message || error);
  }
};

const setActiveTab= (tab) => {
    activeTab.value= tab;
};

//모달 
const showModal = ref(false);

const openModal= () => {
    bookStore.setbook(bookInLibrary.value); //pinia로 rbook설정
    showModal.value=true;
};

const closeModal = (updatedBook) => {
    if (updatedBook) {
        Object.assign(book.value, updatedBook);
    }
    showModal.value=false;
};

const loadBookDetail = async () => {
    try {
        const response = await apiClient.get(`/api/book/${isbn13}`)
        book.value = response.data.data;
        await checkLibraryStatus();
    } catch (error) {
        console.log(error);
    }
}
// 내 서재 상태 확인
const checkLibraryStatus = async () => {
    try {
        const response = await apiClient.get(`/api/library/${authStore.user.userId}`);
        const libraryItems = response.data.data || [];
        const existingBook = libraryItems.find((item) => item.isbn13 === book.value.isbn13);

        if (existingBook) {
            isInLibrary.value = true;
            libraryId.value = existingBook.libraryId;
        } else {
            isInLibrary.value = false;
            libraryId.value = null;
        }
    } catch (error) {
        console.error("내 서재 상태 확인 오류:", error);
    }
};

// 내 서재 담기/삭제
const toggleWishList = async () => {
    if (isInLibrary.value) {
        try {
            if (!libraryId.value) {
                return;
            }

            await apiClient.delete("/api/library", {
                data: {
                    userId: authStore.user.userId,
                    libraryId: libraryId.value,
                },
            });

            isInLibrary.value = false;
            libraryId.value = null;
            book.value.status = "";
        } catch (error) {
            console.error("도서 삭제 오류:", error);
        }
    } else {
        try {
            const response = await apiClient.post(`/api/library/${authStore.user.userId}`, book.value);
            libraryId.value = response.data.data;
            isInLibrary.value = true;
            utilModalStore.showModal("도서 담기", `내 서재에 ${book.value.title}<br>도서가 저장되었습니다.`, "add-book");
        } catch (error) {
            console.error("도서 추가 오류:", error);
            alert("이미 담은 책입니다");
        }
    }
};

const loadUserGoalExist = async () => {
    try {
        const response = await apiClient.get(`/api/library/${authStore.user.userId}/${book.value.isbn13}`);
        console.log("내 서재에서 책 정보:", response.data.data);

        // bookInLibrary에 책 정보와 상태를 저장
        bookInLibrary.value = response.data.data;

        // 상태를 가져오고, book.value에 상태를 반영
        if (bookInLibrary.value && bookInLibrary.value.status) {
            book.value.status = bookInLibrary.value.status;
        }
    } catch (error) {
        console.error("목록에서 책 상태 가져오기 오류:", error);
    }
}

onMounted(async() => {
    MusicPlayer;
    await loadBookDetail();

    if(route.query.data){
        try{
            const queryData= JSON.parse(route.query.data);
            
            //쿼리로 전달한 데이터를 book.value에 병합
            Object.assign(book.value, queryData);

            if(queryData.status){
                book.value.status = queryData.status;
                
            };
        }catch(error){
            console.log(error);
        }
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