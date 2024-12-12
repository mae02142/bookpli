<template>
<div>
    <div class="book-details">
        <div class="book-cover-section">
            <img class="book-cover" :src="book.cover"/>
        </div>
    <div class="book-info-section">
        <div>
            <div class="title-and-icons">
                <h1 class="book-title">{{ book.title }}</h1>
                <div class="icons-container">
                    <img :src="isLiked ? likeImage : dislikeImage" class="detail-icons" @click="toggleLike"/>
                    <img src="../../assets/icons/cart.png" class="detail-icons"/>
                </div>
            </div>
            <span class="book-meta">
                출판일: {{ book.pubdate.split('T')[0] }}
                <p class="book-meta" v-if="book.startindex">쪽수: {{ book.startindex }}쪽</p>
                ISBN: {{ book.isbn13 }}
            </span>
            <span class="book-meta">
                출판사: {{ book.publisher }}
                지은이: {{ book.author.split('(')[0] }}
            </span>
            <div>
                <h2 class="book-intro-header">책소개</h2>
                <p class="book-intro">{{ book.description }}</p> 
                보기
            </div>
            <button class="btn-read" @click="openModal(book)" v-if="book.status !== 'reading'">선택</button>
            
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
    </div>
    <ReviewForm v-if="activeTab==='review'" :bookId="book.isbn13" :userId="userId"/>
</div>
</div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import ReviewForm from "@/components/review/ReviewForm.vue";
import ReadGoalModal from "@/components/readGoal/ReadGoalModal.vue";

const route= useRoute();
const router= useRouter();
const book= ref(JSON.parse(route.query.data));

import { useAuthStore } from '@/stores/auth';
const authStore= useAuthStore();

const activeTab= ref('recommend');

import dislikeImage from '@/assets/icons/dislike.png';
import likeImage from '@/assets/icons/like.png';


// 좋아요 상태 관리 변수
const isLiked = ref(false);

// 좋아요 상태 토글 함수
const toggleLike = () => {
    isLiked.value = !isLiked.value;
    // console.log("Like 상태:", isLiked.value);
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

const gotoGoal = (book) => {
    console.log(book);
    router.push({
        path: `/miniroom/goal/${book.isbn13}`,
        query: { 
            data: JSON.stringify(book),
        },  
    });
};

const userInfo = async () => {
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/miniroom/user/${authStore.user.userId}/profile`);
        const userData= response.data;
        
    } catch (error) {
        console.log(error);
    }
}

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
}

.book-cover-section {
    background: rgba(245, 245, 220, 0.6);
    border-radius: 35px;
    padding: 20px;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
}

.book-cover {
    width: 100%;
    max-width: 281px;
    height: auto;
    object-fit: cover;
}

.book-info-section {
    position: relative;
    background: #ffffff;
    border: 1px solid #cccccc;
    border-radius: 8px;
    padding: 20px;
    flex: 2;
}

.book-title {
    font-family: "Inter-Bold", sans-serif;
    font-size: 40px;
    font-weight: 700;
    color: #000000;
    margin-bottom: 10px;
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
    font-family: "Inter-Regular", sans-serif;
    font-size: 20px;
    font-weight: 400;
    color: #000000;
    margin: 10px 0;
}

.book-intro-header {
    font-size: 20px;
    font-weight: 400;
    color: #000000;
    margin-top: 30px;
}

.book-intro {
    font-family: "Inter-Regular", sans-serif;
    font-size: 20px;
    font-weight: 400;
    color: #000000;
    margin-bottom: 10px;
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
    gap: 10px;
}

.recommendation-covers img {
    width: 281px;
    height: 383px;
}

.recommendation-cover {
    width: 182px;
    height: 280px;
    object-fit: cover;
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
    width: 30px !important;
    height: 30px !important;
    cursor: pointer;
}

.title-and-icons {
    display: flex; 
    justify-content: space-between; 
    align-items: center;
    margin-bottom: 10px;
}

.icons-container {
    display: flex; 
    gap: 10px; 
}
</style>