<!--모달로 보여주는 독서목표-->
<template>
    <div v-if="visible" class="modal-overlay" @click.self="emitClose">
        <div class="modal-content">
            
            <div class="book-section">
                <img class="bookgoal-cover" :src="rbook.cover" alt="Book Cover" />
                <div class="title">{{rbook.title}}</div>
                <div class="book-info">{{rbook.author}}({{rbook.startindex}}p)</div>
                <div class="reading-status" v-if="rbook.status === 'reading'">
                    <img class="bookmark" src="../../assets/icons/bookmark2.png" alt="Bookmark" />
                    <span class="reading-status-text">읽고 있는 책</span>
            </div>
        </div>

        <div class="date-section">
        <div class="date-status">독서상태</div>
        <div class="date-row">
            <span class="date-label">
                <input type="radio" :checked="rbook.status === 'reading'" value="reading" v-model="radioSelect">
                독서중
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
            <p>{{ rbook.startDate ? rbook.startDate.split("T")[0] : ''}}</p>
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
            <p>{{ rbook.endDate ? rbook.endDate.split("T")[0] : '' }}</p>
            </span>
                </div>
        </div>

        <div class="progress-section" v-if="rbook.status === 'reading'">
            <div class="progress-header">독서량</div>
            <div class="progress-bar">
                <div class="progress-bar-fill" :style="{width: `${rbook.progressPercentage || 0}%`}"></div>
            </div>
            <p class="progress-percentage">{{ rbook.progressPercentage || 0 }}%</p>
        </div>
        <span class="button-container">
            <button class="confirm-button" @click="handleAction">확인</button>
            <button class="confirm-button" @click="emitClose" >닫기</button>
        </span>    
    </div>
</div>
</template>
<script setup>
import {defineProps, defineEmits} from "vue";
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from '@/stores/auth';
import { useRoute, useRouter } from "vue-router";
import { useProgressStore } from "@/stores/readingProgressbar";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import { ko } from "date-fns/locale";
import { format } from "date-fns";
import axios from "axios";
import apiClient from "@/api/axiosInstance";

const route= useRoute();
const router= useRouter();
const progressStore= useProgressStore();

defineProps({
    visible: Boolean,
    rbook: Object,
});

const emit= defineEmits(["close"]);

const emitClose= () => {
    emit("close");
};

const confirmAction= () => {
    alert("독서 목표가 설정되었습니다.");
};


//날짜 포맷팅
const dateFormat = "yyyy-MM-dd";

const changeDate= async (rbook) => {

    const formatStartDate= format(new Date(startDate.value),"yyyy-MM-dd HH:mm:ss");
    const formatEndDate= format(new Date(endDate.value),"yyyy-MM-dd HH:mm:ss");
    
    try{
        const response= await apiClient.put(`/api/goal/reset/${book.isbn13}`, null, {
            params: {
                startDate: formatStartDate,
                endDate: formatEndDate,
            },
        });
        alert("독서기간이 수정되었습니다.");
        
    }catch(error){
        console.log("독서기간 수정중 에러",error);
    }
}


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
    if (!rbook) {
        console.error("rbook이 정의되지 않았습니다.");
        alert("선택된 책이 없습니다. 다시 시도해주세요.");
        return;
    }

    console.log("rbook값있는지 확인확인",rbook);
    console.log("라디오 기본새팅값도 있는지 확인확인",radioSelect.value);
    try{
        if(rbook.status === "reading"){
            await changeDate(rbook);

            if(radioSelect.value === "dropped"){
                await dropReading();
            }
        }else if(rbook.status === "wished"){

        if(radioSelect.value === "reading"){
            await changeStatus();
            await changeDate(rbook);
        }else if(radioSelect.value === "dropped"){
            await dropReading();
        }else{
            alert("독서상태를 선택해주세요");
        }
        }
    }catch(error){
        console.log("기간변경하면서 에러",error);
    }; 
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
font-size: 16px;
font-weight: 700;
color: #000000;
text-align: center;
margin-top: 10px;
}

.book-section {
display: flex;
flex-direction: column;
align-items: center;
margin-top: 10px;
}

.bookgoal-cover {
width: 150px;
height: 200px;
object-fit: cover;
}

.book-info {
font-size: 13px;
color: #757575;
margin-top: 7px;
}

.reading-status {
display: flex;
align-items: center;
justify-content: center;
background: #fffdf1;
border-radius: 25px;
width: 190px;
height: 40px;
margin-top: 15px;
}

.reading-status-text {
font-size: 16px;
color: #000000;
}

.bookmark {
    width: 23px;
    height: 25px;
    margin-right: 10px;
}

.date-section {
margin: 15px auto;
width: 80%;
max-width: 800px;
}

.date-status{
    text-align: left;
    font-size: 16px;
    color: #000000;
    margin-bottom: 10px;
    
}

.date-header {
    text-align: left; 
    font-size: 16px;
    color: #000000;
    margin: 10px; 
}


.date-label input[type="radio"] {
    width: 20px; 
    height: 20px; 
    margin-right: 10px; 
}

.date-row {
    display: flex;
    justify-content: space-around;
    align-items: center;
    background: #f0f0f0;
    border-radius: 15px;
    padding: 10px 15px;
    margin-top: 5px;
    margin-bottom: 5px;
}

.date-label {
font-size: 14px;
color: #000000;
display: flex;
align-items: center;
gap: 5px;
}

.date-value {
font-size: 14px;
color: #000000;
}

.progress-section {
margin: 40px auto;
width: 80%;
max-width: 800px;
}

.progress-header {
    text-align: left;
    font-size: 16px;
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
    text-align: left;
    font-size: 16px;
}

.confirm-button {
    background: #fffdf1;
    border: none;
    border-radius: 15px;
    padding: 10px 15px;
    font-size: 15px;
    color: #000000;
    cursor: pointer;
}

.date-label {
    display: inline-flex;
    align-items: center; 
    font-size: 15px;
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

.date-status,
.date-header {
    text-align: left; /* 좌측 정렬 통일 */
    font-size: 16px;
    color: #000000;
    margin: 0; /* 여백 초기화 */
    padding-left: 5px; /* 동일한 패딩 적용 */
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
    margin-bottom: 10px;
}

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
    z-index: 9999;
}

.modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 10px;
    width: 60%;
    height: 80%;
    max-width: 600px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
}
</style>

