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
            <div class="book-status-goal" @click="openModal(book)">
                <img src="@/assets/icons/book_option.png" class="register-status-icon">
                <span v-if="book.status === 'reading'">독서 설정 변경</span>
                <span v-else>바로 독서 설정</span>
            </div>
            <div class="book-status-wish" @click="toggleWishList">
                <img src="@/assets/icons/add_book_shelf.png">
                <span v-if="isInLibrary">내 서재에서 삭제하기</span>
                <span v-else>내 서재에 담기</span>
            </div>
            <div class="book-status-like">
                <img :src="isLiked ? likeImage : dislikeImage" class="detail-icons" @click="likeAndToggle()"/>
            </div>
        </div>
        <ReadGoalModal 
            :visible="showModal"
            :rbook="selectBook"
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
        <Recommend v-if="activeTab ==='recommend'" @recomBook="recomBookClick" />
    </div>
    <BookReview v-if="activeTab==='review'" :isbn13="book.isbn13"/>
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
import Recommend from "@/components/recommBooks/Recommend.vue";

const route= useRoute();
const authStore = useAuthStore();
const book= ref({});
const isbn13 = route.params.isbn13;
const utilModalStore = useUtilModalStore();
const isInLibrary = ref(false); // 내 서재 상태 관리
const libraryId = ref("");

const activeTab= ref("");

const recomBook= ref(null);

const recomBookClick= (recomBook) => {
    book.value=recomBook;
}

import dislikeImage from '@/assets/icons/dislike_lightgray.png';
import likeImage from '@/assets/icons/like.png';


// 좋아요 상태 관리 변수
const isLiked = ref(false);

// 좋아요 상태 토글 함수
const toggleLike = () => {
    isLiked.value = !isLiked.value;
}

//찜하기
const likeBook = async ()  =>{
    try{
        const response= await apiClient.post(`/api/book/like/${authStore.user.userId}/${isbn13}`);
    }catch(error){
        console.log(error);
    }
}

//찜하기 해제
const dislike = async ()  =>{
    try{
        const response= await apiClient.delete(`/api/book/dislike/${authStore.user.userId}/${isbn13}`);
    }catch(error){
        console.log(error);
    }
}

const liked= ref("");

//찜한 도서인지
const likeordislike = async () => {
    try{
        const response= await apiClient.get(`/api/book/${authStore.user.userId}/${isbn13}`);
        isLiked.value=response.data;
        console.log(liked.data);
    }catch(error){
        console.log(error);
    }
}


const likeAndToggle= async () => {
    if(isLiked.value){
        await dislike();
    }else{
        await likeBook();
    }
    
    //상태 반대 토글
    isLiked.value= !isLiked.value;
}

const setActiveTab= (tab) => {
    activeTab.value= tab;
};

//모달 
const showModal = ref(false);
const selectBook= ref({});

const openModal= (readList) => {
    selectBook.value=readList;
    showModal.value=true;
};

const closeModal= () =>{
    showModal.value=false;
};

const loadBookDetail = async () => {
    try {
        const response = await apiClient.get(`/api/book/${isbn13}`)
        book.value = response.data.data;
        // 도서 상세를 로드한 후 상태 확인
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
        }
    }
};

onMounted(async() => {
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
});

</script>

<style>
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
    font-size: 20px;
    font-weight: 700;
    color: #000000;
}

.book-subtitle {
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

.title-and-author span {
    font-size: 14px;
}
</style>