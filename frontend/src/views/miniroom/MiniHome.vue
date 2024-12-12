<template>
<div class="dashboard">
    <LeftSidebar/> 
    <div class="left-section">
    <!-- 사용자 프로필 -->
        <div class="user-profile">
            <div class="user-info">
                <img :src="userImg" class="avatar"></img>
                <div class="book-plan">
                    <p class="userNm">{{userName}}님</p><br>
                    <p>이번달 목표 권 수: <span>{{currentGoal}}권</span></p>
                    <p>이번달 읽은 권 수: <span>{{currentRead}}권</span></p>
                </div>
            </div>
            <div class="status-card">
                <div class="most-read-month">
                    <div class="card-background"></div>
                    <div class="card-title">가장 많이 읽은달</div>
                    <div class="card-status">{{ mostReadInfo.month}}월 | {{mostReadInfo.count}}권</div>
                </div>
                <div class="yearly-read">
                    <div class="card-background"></div>
                    <div class="card-title">1년 동안</div>
                    <div class="card-status">{{yearCount}}권</div>
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
            <MusicPlayer/>
            <div class="current-track">
                <img class="track-cover" src="../../assets/test/music2.png">    
                    <div class="track-details">
                        <p class="track-title">첫 눈!</p>
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
        <div class="reading-status-box">
            <ul v-if="readList.length > 0">
                <div class="book-progress" v-for="(book, index) in readList" :key="index">
                <p class="book-title">{{ book.title.split('-')[0] }}</p>
                <p class="book-start-date">시작일 {{ book.startDate.split('T')[0] }}</p>
                <div class="progress-wrapper">
                <!-- Progress Bar -->
                <!-- 목표량 Progress Bar -->
                <div class="full-progress" max="100"></div>
                <div class="goal-progress" :style="{ width: calculateGoalProgress[index]+ '%'}"></div> 
                                
                <!-- 현재 Progress Bar -->
                <div class="current-progress" :style="{ width: calInputPage[index]+ '%'}"></div>
                    <div class="progress-info">
                        <span class="progress-percentage">{{ calInputPage[index] }}%</span>
                        <span class="page-info">
                                <span v-if="isEditing[index]">
                                    <input type="number" v-model.number="currentPage[index]" 
                                    @blur="stopEdit(index)" @keyup.enter="stopEdit(index)" class="edit-input"/>
                                    /{{ book.startindex }}
                                </span>
                                <span v-else>
                                    p.{{ currentPage[index] || 0 }}/{{ book.startindex }}
                                    <img src="../../assets/icons/bookmark2.png" class="sm-images" @click="startEdit(index)"/>
                                    <button @click="clearReading()">완료</button>
                                </span>
                        </span>    
                    </div>
                </div>  
                </div>
            </ul>    
            <p v-else class="empty">읽고 있는 도서가 존재하지 않습니다.</p>      
        </div>

        <h3 class="title-header">내가 읽고 있는 책</h3>    
        <div class="book-section">
            <div v-if="readList.length > 0" class="book-covers">
                <div class="book-item" v-for="rbook in readList" :key="readList.isbn13">
                    <img class="book-cover" :src="rbook.cover" @click="gotoDetail(rbook)"/> 
                    <p class="book-info">
                        <span class="book-icon" @click="gotoGoal(rbook)">📖</span>&nbsp;&nbsp;
                        <span>{{ rbook.title.split('-')[0] }}</span>&nbsp;&nbsp;
                        <span>{{ rbook.author.split('(')[0] }}</span>
                    </p>
                </div>
            </div>
            <p v-else class="empty">읽고 있는 도서가 존재하지 않습니다.</p>      
        </div>

        <h3 class="title-header">내가 담아놓은 책</h3>
        <p class="more-wrapper book-more">
            <img src="../../assets/icons/add.png" class="sm-images"/>더보기
        </p>
        <div class="book-section">
            <div v-if="addList.length > 0" class="book-covers">
                <div class="book-item" v-for="wbook in addList" :key="addList.isbn13">
                    <img class="book-cover" :src="wbook.cover" @click="gotoDetail(wbook)"/>
                </div>
            </div>
            <p v-else class="empty">담은 도서가 존재하지 않습니다.</p>      
        </div>
    </div>
</div>


</template>

<script setup>
import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router'; 
import { useAuthStore } from '@/stores/auth';
import { onMounted } from 'vue';
import musicPlayer from '@/components/layouts/musicPlayer.vue';

const router= useRouter();
const authStore= useAuthStore();
const addList= ref([]);
const readList= ref([]);


const currentPage= ref(readList.value.map(() => 0)); //초기값 0으로 설정
const isEditing= ref(readList.value.map(()=> false)); //현재 페이지 입력 편집모드
const userName= ref("");
const userImg= ref("");
const compRead= ref([]);
const yearCount= ref(0);
const mostReadInfo= ref({ month: null, count: 0 });

//입력 페이지 퍼센트 렌더링
const calInputPage = computed(() => 
    readList.value.map((book, index) => {
    const currentPages= currentPage.value[index] || 0;
    const totalPages= book.startindex || 1;

    if (currentPages <= 0) return 0; // 최소값 0
    if (currentPages >= totalPages) return 100; // 최대값 100
    return Math.round((currentPages / totalPages) * 100); //퍼센트 계산
    }) 
);


//1일 독서량 계산
const calDailyRead= (startDate, endDate, startindex) => {
    const start= new Date(startDate);
    const end= new Date(endDate);

    //기간계산 (밀리초 -> 일 단위)
    const days= (start-end)/(1000 * 60 * 60 * 24);

    //하루 목표량
    return Math.ceil(startindex/days);
}

//편집모드 시작
const startEdit= (index) => {
    isEditing.value[index]=true;

    //값이 변경될때마다 저장
    saveProgress(index);
}

//편집모드 종료
const stopEdit= (index) => {
    const currentPages = currentPage.value[index];
    const totalPages = readList.value[index].startindex;

    // 유효한 범위 내로 제한
    if (currentPages < 0) currentPage.value[index] = 0;
    if (currentPages > totalPages) currentPage.value[index] = totalPages;

    isEditing.value[index] = false;

    saveProgress(index); //진행상황 저장
};


// 반응형 목표량 누적 계산
const calculateGoalProgress = computed(() => {
    return readList.value.map((book) => {
    const start = new Date(book.startDate);
    const end = new Date(book.endDate);
    const today = new Date();

    // 목표 기간 계산 (밀리초 → 일)
    const totalDays = (end - start) / (1000 * 60 * 60 * 24);

    // 오늘까지의 경과일 계산
    const elapsedDays = (today - start) / (1000 * 60 * 60 * 24);

    // 경과일이 0보다 작으면 (시작 전) 0%, 종료일 이후면 100%
    if (elapsedDays < 0) return 0;
    if (elapsedDays > totalDays) return 100;

    // 목표량 누적 계산 (경과 비율 * 총 페이지 수)
    const progress = (elapsedDays / totalDays) * 100;
    return parseFloat(progress.toFixed(2)); // 소수점 2자리까지 표시
    });
});

import { useProgressStore } from '../../stores/readingProgressbar';
import MusicPlayer from '@/components/layouts/musicPlayer.vue';

const progressStore = useProgressStore();

const saveProgress = (index) => {
    const book = readList.value[index];
    if (!book || !book.isbn13) return;

    const progressData = {
    startDate: book.startDate,
    endDate: book.endDate,
    currentPage: currentPage.value[index],
    totalPages: book.startindex || 1,
    progressPercentage: Math.round((currentPage.value[index] / (book.startindex || 1)) * 100)
    };

  progressStore.saveProgress(book.isbn13, progressData); //Pinia에 상태 저장

};


const gotoGoal = (book) =>{
    if(book.status === "reading"){
        router.push({
            path:`/miniroom/goal/${book.isbn13}`,
            query: {data: JSON.stringify(book)},
        });
    }else{
        router.push({
            path:`/miniroom/goal/${book.isbn13}`,
            query: {data: JSON.stringify(book)},
        });
    }
};


const gotoDetail = (book) => {
    console.log(book);
    router.push({
        path: `/miniroom/book/${book.isbn13}`,
        query: { data: JSON.stringify(book) },  
    });
};


const loadMyLibrary = async (status='wished') => {
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/miniroom/user/${authStore.user.userId}/book`,{params: {status}});
        addList.value= response.data;
    } catch (error) {
        console.log(error);
    }
}

const readingBook = async (status='reading') => {
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/miniroom/user/${authStore.user.userId}/book`,{params: {status}});
        readList.value= response.data;
        calculateMonth();
    } catch (error) {
        console.log(error);
    }
}

const userInfo = async () => {
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/miniroom/user/${authStore.user.userId}/profile`);
        const userData= response.data;
        userName.value= userData.display_name;
        userImg.value= userData.profilePath;
    } catch (error) {
        console.log(error);
    }
}

const currentGoal= ref("");
const currentRead= ref("");

//이번달 목표
const calculateMonth= () => {
    const month= new Date().getMonth()+1;
    const year= new Date().getFullYear();

    //이번달 목표 도서 수 
    currentGoal.value= readList.value.filter((book)=>{
        if(book.startDate){
            const bookDate= new Date(book.startDate);
            return(
                bookDate.getFullYear() === year && bookDate.getMonth()+1 === month
            );
        }
        return false;
    }).length;

    //이번달 읽은 도서 수
    currentRead.value=compRead.value.filter((book)=>{
        if(book.startDate){
            const bookDate= new Date(book.startDate);
            return(
                bookDate.getFullYear() === year && bookDate.getMonth()+1 === month
            );
        }
        return false;
    }).length;
};

const clearReading = async () => {
    try{
        const response= await axios.put(`/api/miniroom/clear/${readList.value.isbn13}`,{
            params: {status: "completed"},
        });
    }catch(error){
        console.log(error);
    }
}

const finishStatus= async (status='completed') => {
    try{
        const response= await axios.get(`${import.meta.env.VITE_API_URL}/api/miniroom/user/${authStore.user.userId}/book`,{params: {status}})
        compRead.value=response.data;
        calculateMonth();
        //1년동안 읽은 권 수
        const calYearCnt= () =>{
            const currentY= new Date().getFullYear();
            return compRead.value.filter((book) =>{
                if(book.endDate){
                    const bookYear= new Date(book.endDate).getFullYear();
                    return bookYear === currentY;
                }
                return false;
            }).length;
        };

        yearCount.value=calYearCnt();


        //가장 많이 읽은달 계산
        const MostReadMonth= () => {
            if(!compRead.value || compRead.value.length === 0){
                console.log("독서를 완료한 도서가 존재하지 않습니다.");
                return null;
            }

            const monthCnt= {};

            compRead.value.forEach((book) => {
                if(book.endDate){
                    const month= new Date(book.endDate).getMonth()+1; 
                    monthCnt[month] = (monthCnt[month] || 0) + 1;
                }
            });
        

            let maxCnt= 0;
            let mostRMonth= "";

            for(const [month, count] of Object.entries(monthCnt)){
                if( count > maxCnt){
                    maxCnt=count;
                    mostRMonth= month;
                }
            } 

            return {month:mostRMonth, count: maxCnt};
        };

        mostReadInfo.value= MostReadMonth();
    
    }catch(error){
        console.log(error);
    }
};


onMounted(() => {
    MusicPlayer;
    loadMyLibrary();
    readingBook().then(() => {
        isEditing.value = readList.value.map(() => false); // 각 책의 편집 상태 초기화

        //저장된 진행상황 불러오기
        readList.value.forEach((book, index)=>{
            const savedProgress= progressStore.getProgress(book.isbn13);
            if(savedProgress){
                currentPage.value[index]= savedProgress.currentPage; //저장된 현재페이지
                book.progressPercentage = savedProgress.progressPercentage || 0;
            } else {
                book.progressPercentage = 0;
            }
        });
    });
    userInfo();
    finishStatus();
    
});



</script>

<style scoped>
.dashboard {
    display: flex;
    height: 100vh;
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

.status-card {
    display: flex;
    gap: 15px;
    margin-left: 15px;
    flex-direction: row;
    align-items: center;
    margin-right: 15px;
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
    margin-left: 30px;
}

.reading-status-box {
height: 300px; 
background-color: #f9f9f9;
padding: 20px;
border-radius: 8px;

overflow-y: auto; /* 세로 스크롤 활성화 */
overflow-x: hidden; /* 가로 스크롤 비활성화 */
border: 1px solid #ccc; /* 경계선을 추가해 가시성을 높임 */

display: flex;
flex-direction: column; /* 자식 요소를 수직으로 정렬 */
gap: 20px; 
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

.reading-status ul,
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
    height: 100%; 
    background-color: #ccc;
    width: 2px;
}

.book-progress {
    margin: 0; /* 개별 요소 간 여백 제거 */
    padding: 10px 0; /* 내부 패딩 추가 */
    border-bottom: 1px solid #e0e0e0; /* 구분선 추가 */
}

.book-progress:last-child {
    border-bottom: none; /* 마지막 요소 구분선 제거 */
}

.progress-wrapper {
    position: relative;
    margin-top: 10px;  
    width: 100%;
    height: 12px;
    margin-bottom: 18px;
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
    font-size: x-large;
}

.title-header {
    margin-bottom: 20px;
    margin-top: 20px;
    font-size: x-large;
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

.reading-status {
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
margin-bottom: 15px;
position: relative;
}

.card-status {
font-family: "Inter-Regular", sans-serif;
font-size: 23px;
font-weight: 400;
color: #000000;
margin-top: 10px;
position: relative;
}

.sidebar {
    position: fixed; 
    top: 0;
    left: 0;
    width: 60px; 
    height: 100%;
    background-color: #fffdf1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 10px 0;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
    z-index: 1000; 
}

.empty{
    text-align: center;
    
}

.userNm{
    font-size: x-large;
}
</style>
