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
        <span class="date-label">
            <input type="radio" :checked="book.status === 'reading'" value="reading" v-model="radioSelect">독서중
        </span>
        <span class="date-label">
            <input type="radio" value="dropped" v-model="radioSelect">독서 중 해제
        </span>
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
            <div class="progress-bar-fill" :style="{width: `${book.progressPercentage || 0}%`}"></div>
        </div>
        <p class="progress-percentage">{{ book.progressPercentage || 0 }}%</p>
    </div>
    <span class="button-container">
        <button class="confirm-button" @click="handleAction()">확인</button>
        <button class="confirm-button" >닫기</button>
    </span>    
</div>
</template>

<script setup>
import { ref,computed, onMounted } from "vue";
import { useAuthStore } from '@/stores/auth';
import { useRoute, useRouter } from "vue-router";
import { useProgressStore } from "@/stores/readingProgressbar";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import { ko } from "date-fns/locale";
import { format } from "date-fns";
import axios from "axios";

const route= useRoute();
const router= useRouter();
const progressStore= useProgressStore();

//날짜 포맷팅
const dateFormat = "yyyy-MM-dd";


const book =ref(
    route.query.data ? JSON.parse(route.query.data): {}
);

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

const radioSelect= ref("");

const handleAction= async () => {
    if(radioSelect.value === "reading"){
        await changeStatus();
    }else if(radioSelect.value === "dropped"){
        await dropReading();
    }else{
        alert("독서상태를 선택해주세요");
    }
}

const changeStatus = async () => {
    if(!book.value.isbn13 || !book.value.status|| !startDate.value || !endDate.value){
        alert("독서상태와 독서기간을 모두 선택해주세요");
        return;
    }

    const formatStartDate= format(new Date(startDate.value),"yyyy-MM-dd HH:mm:ss");
    const formatEndDate= format(new Date(endDate.value),"yyyy-MM-dd HH:mm:ss");

    try{
        const response= await axios.put(`/api/goal/register/${book.value.isbn13}`, null, {
            params: {
                status: book.value.status,
                startDate: formatStartDate,
                endDate: formatEndDate,
            },
        });
        alert(response.data);
        router.push('/miniroom/minihome');
    }catch(error){
        console.error(error.response?.data || error.message);
        alert("오류가 발생했습니다.");
    }
}


const dropReading = async () => {
    try{
        const response= await axios.delete(`/api/goal/${book.value.isbn13}`,{
            params: { status: "dropped" },
        });
        alert(response.data);
        router.push("/miniroom/minihome");
    }catch(error){
        console.error(error.response?.data || error.message);
        alert("오류가 발생했습니다.");
    }
}

//pinia에서 저장된 진행상황
onMounted(() => {
    const savedProgress= progressStore.getProgress(book.value.isbn13);
    if(savedProgress){
        book.value.currentPage=savedProgress.currentPage || 0;
        book.value.startIndex=savedProgress.startIndex || 0;
        book.value.progressPercentage=savedProgress.progressPercentage || 0;
    }
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
    background: #fffdf1;
    border: none;
    border-radius: 15px;
    padding: 10px 15px;
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

.button-container {
    display: flex;
    justify-content: center;
    gap: 100px;
    margin-top: 40px;
}
</style>
