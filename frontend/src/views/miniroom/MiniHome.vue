<template>
<div class="dashboard">
    <!-- <div class="sidebar">
        <LeftSidebar />
    </div> -->
    <div class="left-section">
    <!-- 사용자 프로필 -->
        <div class="user-profile">
            <div class="user-info">
                <div class="avatar">이미지</div>
                <div>
                    <p>xxx님</p>
                    <p>이번달 목표 권 수: <span>xx 권</span></p>
                    <p>이번달 읽은 권 수: <span>xx 권</span></p>
                </div>
            </div>
            <div class="stats-card">
                <div class="most-read-month">
                    <div class="card-background"></div>
                    <div class="card-title">가장 많이 읽은달</div>
                    <div class="card-stats">9월 | 5권</div>
                </div>
                <div class="yearly-read">
                    <div class="card-background"></div>
                    <div class="card-title">1년 동안</div>
                    <div class="card-stats">7권</div>
                </div>
            </div>
        </div> 

    <!-- 음악 플레이어 -->
<div class="music-section">
    <h3 class="music-title">play music</h3>
    <p class="more-wrapper music-more">
        <img src="../../assets/icons/add.png" class="sm-images"/>더보기
    </p>
    <div class="music-player"> 
        <div class="current-track">
            <img class="track-cover" src="../../assets/test/music2.png">    
                <div class="track-details">
                    <p class="track-title">첫 눈!
                        <img src="../../assets/icons/empty_like.png" class="sm-images"/>
                    </p>
                    <p>엑소</p>
                    <progress class="music-progress" value="30" max="100"></progress>
                </div>
                <div class="controls">
                    <img class="control-button" src="../../assets/icons/previous.png" alt="Play" />
                    <img class="control-button" src="../../assets/icons/play.png" alt="Skip to Start" />
                    <img class="control-button" src="../../assets/icons/next.png" alt="End" />
                </div>
        </div>
    </div>
</div>    
</div>

    <!-- 세로 구분선 -->
    <div class="vr full-height"></div>

    <div class="right-section">
    <h3 class="title-header">이번달 독서통계</h3>
    <div class="reading-stats-box">
        <ul>
            <div v-for="(book, index) in books" :key="index" class="book-progress">
            <p class="book-title">{{ book.title }}</p>
            <p class="book-start-date">시작일 {{ book.startDate }}</p>
            <div class="progress-wrapper">
                
            <!-- 목표량 Progress Bar -->
                <!-- <progress :value="book.progress" max="100">{{ book.progress }}%</progress><br>
                <p class="">{{ book.progress }}%</p> 
                <div  class="goal-progress"  :style="{ width: (book.currentPage / book.totalPages) * 100 + '%' }"></div>-->

                <!-- 목표량 Progress Bar -->
                <div class="full-progress" :value="book.progress" max="100"></div>
                <div class="goal-progress" :style="{ width: (book.totalPages / 400) * 100 + '%' }"></div>
                <!-- 현재 Progress Bar -->
                <div class="current-progress" :style="{ width: book.progress + '%' }"></div>
                <div class="progress-info">
                    <span class="progress-percentage">{{ book.progress }}%</span>
                    <span class="page-info">p.{{ book.currentPage }}/{{ book.totalPages }}
                        <img src="../../assets/icons/bookmark2.png" class="sm-images"/>
                    </span>
                </div>
            </div>  
            </div>
        </ul>          
</div>

<h3 class="title-header">내가 읽고 있는 책</h3>    
<div class="book-section section">
    <div class="book-covers">
        <div class="book-item">
            <img class="book-cover" src="../../assets/test/book1.jpg" @click="gotoDetail"/>
            <p class="book-info">
                <span class="book-icon" @click="gotoGoal">📖</span>&nbsp;&nbsp;
                <span>책제목</span>&nbsp;&nbsp;
                <span>저자명</span>
            </p>
        </div>
        <div class="book-item">
            <img class="book-cover" src="../../assets/test/book2.jpg" @click="gotoDetail"/>
            <p class="book-info">
                <span class="book-icon">📖</span>&nbsp;&nbsp;
                <span>책제목</span>&nbsp;&nbsp;
                <span>저자명</span>
            </p>
        </div>
        <div class="book-item">    
            <img class="book-cover" src="../../assets/test/book3.jpg" @click="gotoDetail"/>
            <p class="book-info">
                <span class="book-icon">📖</span>&nbsp;&nbsp;
                <span>책제목</span>&nbsp;&nbsp;
                <span>저자명</span>
            </p>
        </div>    
    </div>

</div>
<h3 class="title-header">내가 담아놓은 책</h3>
<p class="more-wrapper book-more">
    <img src="../../assets/icons/add.png" class="sm-images"/>더보기
</p>
<div class="book-section section">
<div class="book-covers">
    <div class="book-item">
        <img class="book-cover" src="../../assets/test/book4.jpg" @click="gotoDetail"/>
    </div>
    <div class="book-item">    
        <img class="book-cover" src="../../assets/test/book5.jpg" @click="gotoDetail" />
    </div>
    <div class="book-item">    
        <img class="book-cover" src="../../assets/test/book6.jpg" @click="gotoDetail" /> 
    </div>
</div>
</div>
</div>
</div>


</template>

<script setup>
import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
import { useRouter } from 'vue-router'; 

const router= useRouter();

const books = [
{
    title: '소년이 온다',
    startDate: '2024.11.24',
    progress: 35,
    currentPage: 90,
    totalPages: 253,
},
{
    title: '불안의 서',
    startDate: '2024.11.06',
    progress: 45,
    currentPage: 240,
    totalPages: 355,
},
];

const song = {
title: '혼술 하고 싶은 밤',
artist: '벤',
src: 'song.mp3', // 실제 오디오 파일 경로로 대체하세요.
};

const readingBooks = [
{ title: '소년이 온다', author: '한강' },
{ title: '불안의 서', author: '페르난도 페소아' },
{ title: '면도날', author: '서머싯 몸' },
];

const savedBooks = [
{ title: '불안 세대', author: '제임스 스미스' },
{ title: '시의 언어들', author: '김소연' },
{ title: '숲속의 생활', author: '헨리 소로우' },
];

const gotoGoal = () =>{
    router.push('/miniroom/goal');
}

const gotoDetail = () => {
    router.push('/miniroom/book');
}


</script>

<style scoped>
.dashboard {
    display: grid;
    grid-template-columns: 1fr 2px 2fr;
    padding: 20px;
    align-items: start;
}

.left-section,
.right-section {
display: flex;
flex-direction: column;
}


.left-section {
    align-items: center;
}

.user-profile {
    display: flex;
    justify-content: space-between; 
    align-items: center;
    border-bottom: 1px solid #ccc;
    padding-bottom: 20px;
    margin-bottom: 20px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 15px; 
}

.avatar {
    width: 100px;
    height: 100px;
    background-color: #ddd;
    border-radius: 50%;
}

.stats-card {
    display: flex;
    gap: 15px;
    margin-left: 15px;
    flex-direction: row;
    align-items: center;
}

.most-read-month,
.yearly-read {
    position: relative;
    width: 120px;
    height: 80px;
    text-align: center;
    background: #dffdcc;
    border-radius: 20px;
    padding: 15px;
    box-sizing: border-box;
}


.right-section{
    margin-left: 15px;
}

.reading-stats-box {
height: 200px; 
background-color: #f9f9f9;
padding: 20px;
border-radius: 8px;
}
.user-profile {
    height: 200px;
    border-bottom: 1px solid;
    display: grid;
    width: 100%;
    justify-items: center;
    align-items: center;
    height: fit-content;
    padding: 15px 0px;
}


.music-player,
.book-section {
    margin-top: 20px;       
}

.music-section{
    align-items: center;
}

.music-player {
    display: flex;
    flex-direction: column;
    align-items: center;
    align-items: flex-start; 
    position: relative; 
}

.avatar {
width: 150px;
height: 150px;
background-color: #ddd;
border-radius: 50%;
}

.reading-stats ul,
.book-section ul {
list-style: none;
padding: 0;
}

progress {
width: 100%;
margin: 5px 0;
}

.vertical-line {
    background-color: #ccc;
    grid-column: 2 / 3;
    grid-row: 1 / 4;
    width: 2px;
}


.book-covers {
    display: flex; 
    gap: 20px; 
}

.book-item {
    text-align: center;
    margin-right: 55px;
}

.book-cover {
    width: 150px;
    height: 200px;
    object-fit: cover;
    border-radius: 8px;
}

.book-info {
    margin-top: 10px;
    font-size: 14px;
}

.track-info {
    margin-left: 20px;  
}

.controls {
    display: flex;
    justify-content: space-around;
    margin-top: 20px;
}

.control-button {
    width: 25px;
    height: 25px;
    cursor: pointer;
}


.music-progress {
    height: 8px;
    margin: 10px 0;
    border-radius: 5px;
}

.vr.full-height {
    height: 100%; /* 화면 전체 높이에 맞춤 */
    background-color: #ccc;
    width: 2px;
}

.book-progress:nth-of-type(2) {
    margin-top: 50px;
}


.progress-wrapper {
    position: relative;
    margin-top: 10px;  
    width: 100%;
    height: 12px;
    margin-bottom: 10px;
}

.book-title {
    margin: 0;
    font-size: 16px;
    font-weight: bold;
}

.book-start-date {
    margin: 0;
    font-size: 14px;
    color: #666;
}

.goal-progress {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%; 
    background-color: rgb(2, 77, 42);
    border-radius: 6px;
    z-index: 1; 
}

.current-progress {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    background-color: rgb(171, 235, 171);
    border-radius: 6px;
    z-index: 2; 
}

.progress-percentage {
font-size: 12px;
color: #666;
}

.full-progress{
    top: 0;
    left: 0;
    height: 100%;
    background-color: #D9D9D9;
    border-radius: 6px;
}

.page-info {
font-size: 12px;
color: #666;
}

.music-title{
    margin-bottom: 15px;
}

.title-header {
    margin-bottom: 20px;
    margin-top: 20px;
}

.progress-info{
    display: inline-flex;
    width: 100%;
    justify-content: space-between;
    margin-top: 5px;
}

.more-wrapper,
.track-title {
    display: flex; 
    justify-content: space-between; 
    align-items: center;
}

.sm-images{
    width: 15px;
    height: 15px;
    margin-right: 5px; 
}

.more-wrapper {
    display: flex;
    align-items: center; 
    justify-content: flex-end; 
    cursor: pointer; 
    gap: 5px; 
    font-size: 14px; 
}

.music-more {
    display: flex;
    justify-content: flex-end;
    align-items: center; 
    gap: 5px; 
    font-size: 14px;
    cursor: pointer;
    margin-top: -15px; 
    width: 100%; 
}

.more-wrapper.music-more {
    padding-right: 20px;
}

.reading-stats {
height: 436px;
position: relative;
background: #ffffff;
width: 611px;
padding: 20px;
border-radius: 10px;
}

.profile-image {
width: 129px;
height: 159px;
object-fit: cover;
margin-bottom: 20px;
}

.most-read-month, .yearly-read {
position: relative;
width: 178px;
height: 123px;
margin-top: 20px;
text-align: center;
}

.card-background {
background: #dffdcc;
border-radius: 20px;
width: 100%;
height: 100%;
position: absolute;
top: 0;
left: 0;
}

.card-title {
font-family: "Inter-Regular", sans-serif;
font-size: 20px;
font-weight: 400;
color: #000000;
margin-top: 15px;
margin-bottom: 25px;
position: relative;
}

.card-stats {
font-family: "Inter-Regular", sans-serif;
font-size: 23px;
font-weight: 400;
color: #000000;
margin-top: 10px;
position: relative;
}

.sidebar {
    position: fixed; /* 고정 */
    top: 0;
    left: 0;
    width: 60px; /* 사이드바 너비 */
    height: 100%; /* 화면 전체 높이 */
    background-color: #fffdf1; /* 사이드바 배경색 */
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 10px 0;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
    z-index: 1000; /* 다른 요소 위에 배치 */
}
</style>
