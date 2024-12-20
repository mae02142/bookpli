<!--모달로 보여주는 독서목표-->
<template>
    <div v-if="visible" class="goal-modal-overlay" @click.self="emitClose">
        <div class="goal-modal-content">
            
            <div class="book-section">
                <img class="bookgoal-cover" :src="props.rbook.cover" alt="Book Cover" />
                <div class="title">{{copyProps.title}}</div>
                <div class="book-info">{{copyProps.author}}({{copyProps.startindex}}p)</div>
                <div class="reading-status" v-if="copyProps.status === 'reading'">
                    <img class="bookmark" src="../../assets/icons/bookmark2.png" alt="Bookmark" />
                    <span class="reading-status-text">읽고 있는 책</span>
            </div>
        </div>

        <div class="date-section">
        <div class="date-status">독서상태</div>
        <div class="date-row-status">
            <span class="date-label">
                <input type="radio" :checked="copyProps.status === 'reading'" value="reading" v-model="radioSelect">
                독서중
            </span>
            <span class="date-label">
                <input type="radio" value="dropped" v-model="radioSelect">독서 중 해제
            </span>
        </div>
        <div class="date-header">독서 목표 기간</div>
        <div class="date-row"
        :class="{active: showStartPicker === true}"
        >
            <div class="goal-start-date">
                <p class="goal-start-text">시작일</p>
                <span class="date-label">
                    <img src="../../assets/icons/calendar.png"
                     @click="showStartPicker = !showStartPicker"
                     />
                    <VueDatePicker
                    v-if="showStartPicker"
                    v-model="startDate"
                    :teleport="false"
                    :auto-apply="true"
                    :enable-time-picker="false"
                    placeholder="날짜 선택"
                    :locale="'ko'"
                    :format="dateFormat"
                    @update:modelValue="updateStartDate"
                    />
                </span>
            </div>
            <div class="goal-last-date">
                <p class="goal-last-text">종료일</p>
                <span class="date-label">
                <img src="../../assets/icons/calendar.png" @click="showEndPicker = !showEndPicker" />
                <VueDatePicker
                    v-if="showEndPicker"
                    v-model="endDate"
                    :teleport="false"
                    :auto-apply="true"
                    :enable-time-picker="false"
                    placeholder="날짜 선택"
                    :locale="'ko'"
                    :format="dateFormat"
                    @update:modelValue="updateEndDate"
                />
                </span>
            </div>
            <div class="goal-date-final" v-if="copyProps.startDate">
                <p>{{ copyProps.startDate ? copyProps.startDate.split("T")[0] : ''}}</p>
                <span>~</span>
                <p>{{ copyProps.endDate ? copyProps.endDate.split("T")[0] : '' }}</p>
            </div>
        </div>
    </div>

        <div class="progress-section" v-if="copyProps.status === 'reading'">
            <div class="progress-header">독서량</div>
            <div class="progress-bar">
                <div class="progress-bar-fill" :style="{width: `${copyProps.progressPercentage || 0}%`}"></div>
            </div>
            <p class="progress-percentage">{{ copyProps.progressPercentage || 0 }}%</p>
        </div>
        <span class="button-container">
            <button class="confirm-button" @click="handleAction">확인</button>
            <button class="confirm-button" @click="emitClose" >닫기</button>
        </span>    
    </div>
</div>
</template>
<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useAuthStore } from '@/stores/auth';
import { useRoute, useRouter } from "vue-router";
import { useProgressStore } from "@/stores/readingProgressbar";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import { format } from "date-fns";
import apiClient from "@/api/axiosInstance";
import { useUtilModalStore } from "@/stores/utilModalStore";

const route= useRoute();
const router= useRouter();
const progressStore= useProgressStore();


const props = defineProps({
    visible: Boolean,
    rbook: Object,
});

const emit= defineEmits(["close","dropReading"]);

//날짜 포맷팅
const dateFormat = "yyyy-MM-dd";

//독서기간 변경
const changeDate= async (rbook) => {
    const utilModalStore = useUtilModalStore(); 

    
    try{
        const response= await apiClient.put(`/api/goal/reset`,rbook);
        console.log(response.data);
        utilModalStore.showModal(
            "독서기간 수정",
            "독서기간이 수정되었습니다.",
            "success"
        );
        
    }catch(error){
        let errorMessage = "독서기간 수정 중 오류가 발생했습니다.";
        if (error.response) {
            console.error("서버 응답 에러:", error.response.status, error.response.data);
            errorMessage = error.response.data.message || errorMessage;
        } else if (error.request) {
            console.error("요청이 보내졌으나 응답 없음:", error.request);
        } else {
            console.error("에러 설정 문제:", error.message);
        }

        // 에러 알림
        utilModalStore.showModal(
            "독서기간 수정 실패", 
            errorMessage,       
            "error"             
        );
    }
}


const book =ref(
    route.query.data ? JSON.parse(route.query.data): {}
);

const updateStartDate = (value) => {
    startDate.value = value; 
    props.rbook.startDate= format(new Date(value),'yyyy-MM-dd');
};

const updateEndDate = (value) => {
    endDate.value = value;
    props.rbook.endDate= format(new Date(value),'yyyy-MM-dd');
};


const authStore= useAuthStore();

const startDate = ref(null);
const endDate = ref(null);
const showStartPicker = ref(false);
const showEndPicker = ref(false);

const radioSelect= ref("");

// props 변경 시 localRbook 업데이트
watch(
    () => props.rbook,
    (newVal) => {
        copyProps.value = { ...newVal };
    }
);



const handleAction = async () => {
    const utilModalStore = useUtilModalStore();

    try {
        if (copyProps.status === "reading") {
            if (radioSelect.value !== "dropped") {
                await changeDate(copyProps.value);
                emitClose();
            } else if (radioSelect.value === "dropped") {
                await dropReading(copyProps.value);
                utilModalStore.showModal(
                    "독서 상태 변경",
                    `"${copyProps.title}" 독서 상태가 해제되었습니다.`,
                    "success"
                );
                emitClose();
            }
        }
        else if (copyProps.status === "wished") {
            if (radioSelect.value === "reading") {
                await setGoal(copyProps.value);
                utilModalStore.showModal(
                    "독서 목표 설정",
                    `"${copyProps.title}"이 독서 목표로 설정되었습니다.`,
                    "success"
                );
                emitClose();
            } else if (radioSelect.value === "dropped") {
                await dropReading(copyProps.value);
                utilModalStore.showModal(
                    "독서 상태 해제",
                    `"${copyProps.title}" 독서 상태가 해제되었습니다.`,
                    "success"
                );
                emitClose();
            } else {
                utilModalStore.showModal(
                    "알림",
                    "독서 상태를 선택해주세요.",
                    "warning"
                );
                emitClose();
            }
        }
        else {
            await setGoal(copyProps.value); // 독서 목표 설정 호출
            utilModalStore.showModal(
                "독서 목표 설정",
                `"${copyProps.title}" 독서 목표가 설정되었습니다.`,
                "success"
            );
            emitClose();
        }
    } catch (error) {
        console.error("handleAction 실행 중 에러:", error);
        utilModalStore.showModal(
            "오류 발생",
            "작업 중 문제가 발생했습니다. 다시 시도해주세요.",
            "error"
        );
    }
};

const emitClose = () => {
    emit("close", copyProps.value); 
};

const copyProps = ref({ ...props.rbook });


const setGoal = async (book) => {
    
    const utilModalStore = useUtilModalStore(); 
    console.log("책 정보 확인 : ",props.rbook);
    try{
      
        const response= await apiClient.post(`/api/library/${authStore.user.userId}`, props.rbook);
        console.log("Response from setGoal API:", response.data.data);

        utilModalStore.showModal(
            "독서 목표 설정",
            `"${copyProps.title}"의 독서 목표가 설정되었습니다.`,
            "success"
        );
        router.push('/miniroom/minihome');
    }catch(error){
        console.error(error.response?.data || error.message);
        utilModalStore.showModal(
            "오류 발생",
            "작업 중 문제가 발생했습니다. 다시 시도해주세요.",
            "error"
        );
    }
}


const dropReading = async (rbook) => {
    const utilModalStore = useUtilModalStore(); 
    try{
        const response= await apiClient.delete(`/api/goal/${rbook.isbn13}`,{
            params: { status: "wished" },
        });
        emit("dropReading",rbook.isbn13);
        utilModalStore.showModal(
            "독서 상태 해제",
            `"${props.rbook.title}" 독서 상태가 해제되었습니다.`,
            "success"
        );
        emit("close");
    }catch(error){
        console.error(error.response?.data || error.message);
        utilModalStore.showModal(
            "오류 발생",
            "작업 중 문제가 발생했습니다. 다시 시도해주세요.",
            "error"
        );
    }
}


const progressPercentage = computed(() => {
const progress = progressStore.getProgress(props.rbook.isbn13);
    return progress?.progressPercentage || 0;     
});


// Pinia 상태 감지 및 업데이트
watch(
    () => progressStore.progressData[props.rbook.isbn13],
    (newProgress) => {
        if (newProgress) {
            props.rbook.currentPage = newProgress.currentPage;
            props.rbook.progressPercentage = newProgress.progressPercentage;
        }
    },
    { deep: true, immediate: true }
);


onMounted(() => {
    const savedProgress= progressStore.getProgress(props.rbook.isbn13);

    if(savedProgress){
        props.rbook.currentPage=savedProgress.currentPage || 0;
        props.rbook.progressPercentage=savedProgress.progressPercentage || 0;
    }
});
</script>

<style>
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

.date-row-status{
    display: flex;
    justify-content: space-around;
    align-items: center;
    background: #f0f0f0;
    border-radius: 15px;
    padding: 10px 15px;
    margin-top: 5px;
    margin-bottom: 5px;
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
    gap: 20px;
    display: flex;
    justify-content: space-around;
    align-items: center;
    background: #f0f0f0;
    border-radius: 15px;
    padding: 10px 15px;
    margin-top: 5px;
    margin-bottom: 5px;
}

.date-row.active {
    display: flex;
    flex-direction: column;
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

.goal-last-date,
.goal-start-date{
    display: flex;
}
.goal-last-text,
.goal-start-text{
    margin: auto 7px;
}

.goal-date-final{
    display: flex;
    gap: 20px;
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
    background: rgb(171, 235, 171);
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

.confirm-button:hover{
    background: beige;
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


.date-label {
display: flex;
align-items: center;
gap: 5px;
}

.date-status,
.date-header {
    text-align: left; 
    font-size: 16px;
    color: #000000;
    margin: 0; 
    padding-left: 5px; 
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

.goal-modal-overlay {
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

.goal-modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 10px;
    width: 60%;
    height: auto;
    max-width: 600px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    text-align: center;
    justify-content: center;
    display: flex;
    flex-direction: column;
}
</style>

