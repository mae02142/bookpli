<template>
<div>
    <!-- Title -->
    <div class="title">{{book.title}}</div>

    <div class="book-section">
    <img class="book-cover" :src="book.cover" alt="Book Cover" />
    <div class="book-info">{{book.author}}({{book.startindex}}p)</div>
    <div class="reading-status" v-if="book.status === 'reading'">
        <img class="bookmark" src="../../assets/icons/bookmark2.png" alt="Bookmark" />
        <span class="reading-status-text">읽고 있는 책</span>
    </div>
    </div>

    <div class="date-section">
    <div class="date-status">독서상태</div>
    <div class="date-row">
        <span class="date-label"><input type="radio">독서중</span>
        <span class="date-label"><input type="radio">독서 중 해제</span>
    </div>
    <div class="date-header">독서기간</div>
    <div class="date-row">
        <span class="date-label">
        시작일
        <img src="../../assets/icons/calendar.png" @click="showStartPicker = !showStartPicker" />
        <VueDatePicker
            v-if="showStartPicker"
            v-model="startDate"
            :teleport="false"
            placeholder="날짜 선택"
            :locale="ko"
            :format="dateFormat"
            @update:modelValue="updateStartDate"
        />
        </span>
        <span class="date-label">
        종료일
        <img src="../../assets/icons/calendar.png" @click="showEndPicker = !showEndPicker" />
        <VueDatePicker
            v-if="showEndPicker"
            v-model="endDate"
            :teleport="false"
            placeholder="날짜 선택"
            :locale="ko"
            :format="dateFormat"
            @update:modelValue="updateEndDate"
        />
        </span>
            </div>
    </div>

    <div class="progress-section" v-if="book.status === 'reading'">
        <div class="progress-header">독서량</div>
        <div class="progress-bar">
            <div class="progress-bar-fill"></div>
        </div>
        <p class="progress-percentage">25%</p>
    </div>

    <!-- Confirm Button -->
    <button class="confirm-button" @click="confirmAction">확인</button>
</div>
</template>

<script setup>
import { ref,computed } from "vue";
import { useAuthStore } from '@/stores/auth';
import { useRoute, useRouter } from "vue-router";

import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import { ko } from "date-fns/locale";
import { format } from "date-fns";

const route= useRoute();
const router= useRouter();

//날짜 포맷팅
const dateFormat = "yyyy-MM-dd";

const book = ref({
    isbn13: route.params.isbn13, 
    title: route.query.title,
    author: route.query.author,
    pubdate: route.query.pubdate,
    publisher: route.query.publisher,
    cover: route.query.cover,
    startindex: route.query.startindex,
    genre: route.query.genre,
    status: route.query.status,
});

console.log(book.value);


// Methods
const updateStartDate = (value) => {
    startDate.value = value; 
};

const updateEndDate = (value) => {
    endDate.value = value;
};


const authStore= useAuthStore();

const startDate = ref(null);
const endDate = ref(null);
const showStartPicker = ref(false);
const showEndPicker = ref(false);

// Methods
const confirmAction = () => {
alert("확인이 완료되었습니다!");
};

const changeStatus = async () => {
    
}
// const loadMyLibrary = async () => {
//     try {
//         const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/miniroom/user/${authStore.user.userId}`)
//         console.log(response.data);
        
//     } catch (error) {
//         console.log(error);
//     }
// }


// onMounted(() => {
//     loadMyLibrary();
// })

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

.title {
font-size: 48px;
font-weight: 700;
color: #000000;
text-align: center;
margin: 20px 0;
}

.book-section {
display: flex;
flex-direction: column;
align-items: center;
margin-top: 40px;
}

.book-cover {
width: 333px;
height: 452px;
object-fit: cover;
}

.book-info {
font-size: 28px;
color: #757575;
margin-top: 20px;
}

.reading-status {
display: flex;
align-items: center;
justify-content: center;
background: #fffdf1;
border-radius: 25px;
width: 281px;
height: 56px;
margin-top: 15px;
}

.reading-status-text {
font-size: 28px;
color: #000000;
}

.bookmark {
    width: 40px;
    height: 35px;
    margin-right: 10px;
}

.date-section {
margin: 40px auto;
width: 80%;
max-width: 800px;
}

.date-status{
    font-size: 30px;
    color: #000000;
    margin-bottom: 10px;
    
}

.date-header {
    text-align: left; 
    font-size: 30px;
    color: #000000;
    margin: 20px 0 10px 0; 
}


.date-label input[type="radio"] {
    width: 24px; 
    height: 24px; 
    margin-right: 10px; 
}

.date-row {
    display: flex;
    justify-content: space-around;
    align-items: center;
    background: #f0f0f0;
    border-radius: 15px;
    padding: 15px 20px;
    margin-top: 10px;
    margin-bottom: 20px;
}

.date-label {
font-size: 28px;
color: #000000;
}

.date-value {
font-size: 28px;
color: #000000;
}

.progress-section {
margin: 40px auto;
width: 80%;
max-width: 800px;
}

.progress-header {
    font-size: 30px;
    color: #000000;
}

.progress-bar {
    position: relative;
    height: 37px;
    background: #f0f0f0;
    border-radius: 15px;
    margin-top: 10px;
    overflow: hidden;
}

.progress-bar-fill {
    height: 100%;
    background: #fffdf1;
    width: 25%; 
}

.progress-percentage {
    font-size: 25px;
}

.confirm-button {
    display: block;
    margin: 40px auto;
    background: #fffdf1;
    border: none;
    border-radius: 15px;
    padding: 15px 30px;
    font-size: 30px;
    color: #000000;
    cursor: pointer;
}

.date-label {
    display: inline-flex;
    align-items: center; 
    font-size: 28px;
    color: #000000;
    gap: 8px; 
}

.date-label img {
    width: 1.2em; 
    height: 1.2em; 
    cursor: pointer; 
}

.date-section {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.date-row {
display: flex;
gap: 20px;
}

.date-label {
display: flex;
align-items: center;
gap: 5px;
}

.date-value {
margin-left: 10px;
color: #555;
font-size: 14px;
}
</style>
