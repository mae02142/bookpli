<template>
<div>
    <!-- Title -->
    <div class="title">불안의 서</div>

    <!-- Book Section -->
    <div class="book-section">
    <img class="book-cover" src="../assets/images/book1.jpg" alt="Book Cover" />
    <div class="book-info">페르난두 페소아(808p)</div>
    <div class="reading-status">
        <img class="bookmark" src="../assets/images/책갈피.png" alt="Bookmark" />
        <span class="reading-status-text">읽고 있는 책</span>
    </div>
    </div>

    <!-- Date Section -->
    <div class="date-section">
        <div class="date-status">독서상태</div>
        <div class="date-row">
            <span class="date-label"><input type="radio">독서중</span>
            <span class="date-label"><input class="date-label" type="radio">독서 중 해제</span>
        </div>      
        <div class="date-header">독서기간</div>
            <div class="date-row">
                <span class="date-label">시작일
                    <img src="../assets/images/달력.png">
                    <span class="date-value">{{ startDate }}</span>
                </span>
                <span class="date-label">종료일
                    <img src="../assets/images/달력.png">
                    <span class="date-value">{{ endDate }}</span>
                </span>
            </div>
    </div>

    <!-- Progress Section -->
    <div class="progress-section">
    <div class="progress-header">독서량</div>
    <div class="progress-bar">
        <div class="progress-bar-fill"></div>
    </div>
    <span class="progress-percentage">25%</span>
    </div>

    <!-- Confirm Button -->
    <button class="confirm-button" @click="confirmAction">확인</button>
</div>
</template>

<script setup>
import { ref } from "vue";

// State variables
const startDate = ref("2024.11.06");
const endDate = ref("2024.11.30");
const progress = ref(25); // Progress percentage

// Methods
const confirmAction = () => {
alert("확인이 완료되었습니다!");
};
</script>
<style>
* {
box-sizing: border-box;
margin: 0;
padding: 0;
}

/* Body */
body {
background: #ffffff;
font-family: "Inter", sans-serif;
}

/* Title Section */
.title {
font-size: 48px;
font-weight: 700;
color: #000000;
text-align: center;
margin: 20px 0;
}

/* Book Section */
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
margin: 40px auto;
font-size: 30px;
color: #000000;
margin-bottom: 10px;
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

/* Reading Progress Section */
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
    width: 25%; /* Dynamic progress width */
}

/* Confirm Button */
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
    display: inline-flex; /* 텍스트와 이미지를 같은 줄에 배치 */
    align-items: center; /* 텍스트와 이미지를 수직 가운데 정렬 */
    font-size: 28px;
    color: #000000;
    gap: 8px; /* 텍스트와 이미지 사이 간격 */
}

.date-label img {
  width: 1.2em; /* 폰트 크기에 비례하여 이미지 크기 설정 */
  height: 1.2em; /* 정사각형 비율 유지 */
  cursor: pointer; /* 클릭 가능하도록 커서 변경 */
}

</style>
