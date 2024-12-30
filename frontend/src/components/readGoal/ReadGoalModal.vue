<!--모달로 보여주는 독서목표-->
<template>
    <div v-if="visible" class="goal-modal-overlay" @click.self="emitClose">
        <div class="goal-modal-content">
            
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
            <div class="date-status"  v-if="isDateSelected || rbook.status === 'reading'" >독서상태</div>
            <div class="date-row-status"  v-if="isDateSelected || rbook.status === 'reading'">
                <span class="date-label">
                    <input type="radio" :checked="rbook.status === 'reading'" value="reading" v-model="radioSelect">독서중
                </span>
                <span class="date-label">
                    <input type="radio" :checked="rbook.status === 'wished'" value="wished" v-model="radioSelect">독서 중 해제
                </span>
            </div>
        
        <div class="date-header">독서 목표 기간</div>
        <div class="date-row">
            <div class="goal-start-date">
                <p class="goal-start-text">시작일</p>
                <span class="date-label">
                    <img src="../../assets/icons/calendar.png"
                    />
                    <VueDatePicker
                    v-model="startDate"
                    :teleport="false"
                    :auto-apply="true"
                    :enable-time-picker="false"
                    placeholder="날짜 선택"
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
                    v-model="endDate"
                    :teleport="false"
                    :auto-apply="true"
                    :enable-time-picker="false"
                    placeholder="날짜 선택"
                    :format="dateFormat"
                    @update:modelValue="updateEndDate"
                />
                </span>
            </div>
            <div class="goal-date-final" v-if="rbook.startDate">
                <p>{{ rbook.startDate ? rbook.startDate.split("T")[0] : ''}}</p>
                <span>~</span>
                <p>{{ rbook.endDate ? rbook.endDate.split("T")[0] : '' }}</p>
            </div>
        </div>
    </div>

        <div class="progress-section" v-if="rbook.status === 'reading'">
            <div class="progress-header">독서량</div>
            <div class="progress-bar">
                <div class="progress-bar-fill" :style="{width: `${progressPercentage}%`}"></div>
            </div>
            <p class="progress-percentage">{{ progressPercentage || 0 }}%</p>
        </div>
        <span class="button-container">
            <button class="confirm-button" @click="handleAction">확인</button>
            <button class="cancel-button" @click="emitClose" >닫기</button>
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
import { useBookStore } from "@/stores/bookStore";
const bookStore= useBookStore();
const authStore= useAuthStore()


const route= useRoute();
const router= useRouter();
const progressStore= useProgressStore();

const libraryId = ref(''); 
const isInLibrary = ref(false); 
const isDateSelected = ref(false);


const props = defineProps({
    visible: Boolean ,
    rbook: Object,
});
const localRbook = ref({ ...props.rbook });
const rbook= computed(() => bookStore.rbook);



const emit= defineEmits(["close","dropReading","updateRbook"]);
const emitClose= () => {
    emit("close");
};
//날짜 포맷팅
const dateFormat = "yyyy-MM-dd";

const book =ref(
    route.query.data ? JSON.parse(route.query.data): {}
);
const updateStartDate = (value) => {
    startDate.value = value; 
    rbook.startDate= format(new Date(value),'yyyy-MM-dd');
    
    checkDateSelection();
};
const updateEndDate = (value) => {
    endDate.value = value;
    rbook.endDate= format(new Date(value),'yyyy-MM-dd');
    
    checkDateSelection();
};

const checkDateSelection = () => {
    isDateSelected.value = !!(startDate.value && endDate.value); 
};
const startDate = ref('');
const endDate = ref('');
const radioSelect= ref("");

watch(
    () => props.rbook,(newVal) => {
        localRbook.value = { ...newVal }; 
        // startDate.value = ''; 
        // endDate.value = '';  
        console.log("rbook 데이터 변경 감지:", localRbook.value);
    },
    { deep: true, immediate: true }
);
// 내 서재 상태 확인
const checkLibraryStatus = async () => {
    try {
        const response = await apiClient.get(`/api/library/${authStore.user.userId}/${rbook.value.isbn13}`);
        
        const libraryItems = Array.isArray(response.data.data) 
        ? response.data.data 
        : (response.data.data ? [response.data.data] : []); 
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

const handleAction = async () => {
    const utilModalStore = useUtilModalStore();

    try {
        // 검증: ISBN 및 날짜 값 확인
        if (!rbook.value || !rbook.value.isbn13) {
            utilModalStore.showModal("오류 발생", "도서 정보가 누락되었습니다.", "error");
            return;
        }

        if (!startDate.value || !endDate.value) {
            utilModalStore.showModal("오류 발생", "시작일과 종료일을 선택해주세요.", "error");
            return;
        }

        // 날짜 검증 추가
        if (new Date(startDate.value) > new Date(endDate.value)) {
            utilModalStore.showModal("날짜 선택 오류", "시작일은 종료일보다 늦을 수 없습니다.", "error");
            emitClose();
            return;
        }

        // 날짜 포맷 변환
        let formatStartDate =null;
        let formatEndDate = null;
        if(startDate.value && endDate.value){
            formatStartDate = format(new Date(startDate.value), "yyyy-MM-dd");
            formatEndDate = format(new Date(endDate.value), "yyyy-MM-dd");
        }

        // 현재 상태와 변경 사항 확인
        const currentStatus = rbook.value.status || "wished"; 
        const selectedStatus = radioSelect.value; 
        console.log("현재 상태:", currentStatus, "선택된 상태:", selectedStatus);

        // 1. 상태 변경 - 독서 목표 설정
        if (currentStatus !== selectedStatus) {
            if (selectedStatus === "reading") {
                console.log("독서 목표 설정 시작");
                await setGoal(rbook.value, formatStartDate, formatEndDate, selectedStatus);
                emitClose();
            } else if (selectedStatus === "wished") {
                console.log("독서 중 해제 시작");
                await dropReading(rbook.value);
                emitClose();
            }
        }
        // 2. 기간 변경
        else if (
            formatStartDate !== rbook.value.startDate ||
            formatEndDate !== rbook.value.endDate
        ) {
            console.log("기간 변경 시작");
            await changeDate(rbook.value, formatStartDate, formatEndDate);
            emitClose();
        } else {
            utilModalStore.showModal("알림", "변경된 내용이 없습니다.", "info");
        }

        // 부모 컴포넌트로 변경된 데이터 전달
        emit("updateRbook", rbook.value);

    } catch (error) {
        console.error("handleAction 실행 중 에러:", error);
        utilModalStore.showModal("오류 발생", "작업 중 문제가 발생했습니다.", "error");
    }
};
const setGoal = async (rbook, startDate, endDate, status) => {
    const utilModalStore = useUtilModalStore();

    try {
        // 날짜 포맷 변환
        const formatStartDate = format(new Date(startDate), "yyyy-MM-dd");
        const formatEndDate = format(new Date(endDate), "yyyy-MM-dd");

        const requestData = {
            userId: authStore.user.userId,
            isbn13: rbook.isbn13,
            status: "reading",
            startDate: format(new Date(startDate), "yyyy-MM-dd"),
            endDate: format(new Date(endDate), "yyyy-MM-dd"),
        };
        console.log("setGoal 요청 데이터:", requestData);

        // API 요청
        const response = await apiClient.put(
            `/api/goal/register/${rbook.isbn13}`,
            requestData,
            {
                headers: {
                    Authorization: `Bearer ${authStore.token}`,
                    "Content-Type": "application/json",
                },
            }
        );
        console.log("setGoal 요청 데이터:", requestData);


        console.log("setGoal 응답:", response.data);
        utilModalStore.showModal("독서 목표 설정", `"${rbook.title}" 목표가 설정되었습니다.`, "success");

        // 상태 업데이트
        rbook.status = status;
        rbook.startDate = formatStartDate;
        rbook.endDate = formatEndDate;

    } catch (error) {
        console.error("setGoal 에러:", error.response?.data || error.message);
        utilModalStore.showModal("오류 발생", "목표 설정 중 문제가 발생했습니다.", "error");
    }
};
const changeDate = async (rbook, startDate, endDate) => {
    const utilModalStore = useUtilModalStore();

    try {
        const response = await apiClient.put(
            `/api/goal/reset/${rbook.isbn13}`,
            null,
            {
                headers: {
                    Authorization: `Bearer ${authStore.token}`,
                },
                params: {
                    status: rbook.status,
                    startDate: startDate,
                    endDate: endDate,
                },
            }
        );

        utilModalStore.showModal("독서 기간 변경", "독서 기간이 수정되었습니다.", "success");

        // 상태 업데이트
        rbook.startDate = startDate;
        rbook.endDate = endDate;

    } catch (error) {
        console.error("changeDate 에러:", error.response?.data || error.message);
        utilModalStore.showModal("오류 발생", "기간 변경 중 문제가 발생했습니다.", "error");
    }
};
const dropReading = async (rbook) => {
    const utilModalStore = useUtilModalStore();

    try {
        const response = await apiClient.delete(`/api/goal/${rbook.isbn13}`, {
            params: { status: "wished" },
        });

        utilModalStore.showModal("독서 상태 해제", `"${rbook.title}" 독서 상태가 해제되었습니다.`, "success");

        // 상태 초기화
        rbook.status = "wished";
        rbook.startDate = '';
        rbook.endDate = null;

    } catch (error) {
        console.error("dropReading 에러:", error.response?.data || error.message);
        utilModalStore.showModal("오류 발생", "독서 상태 해제 중 문제가 발생했습니다.", "error");
    }
};

const progressPercentage = computed(() => {
const savedprogress = progressStore.getProgress(rbook.value.isbn13);
    return savedprogress?.progressPercentage || 0;     
});

const bookInLibrary = ref({});

const loadUserGoalExist = async () => {
    try {
        const response = await apiClient.get(`/api/library/${authStore.user.userId}/${rbook.value.isbn13}`);
        bookInLibrary.value = response.data.data || {};
        
        // 상태 업데이트
        rbook.value.status = bookInLibrary.value?.status || "wished";
        radioSelect.value = rbook.value.status; // 초기값 동기화
        console.log("도서 상태 확인:", rbook.value.status);
    } catch (error) {
        console.error("목록에서 책 상태 가져오기 오류:", error);
        bookInLibrary.value = {};
        rbook.value.status = "wished";
        radioSelect.value = "wished"; 
    }
};

onMounted(async () => {
    // rbook 초기화
    const bookFromStore = bookStore.rbook || {};
    
    if (!bookFromStore || !bookFromStore.isbn13) {
        console.error("초기화 실패: 도서 정보 없음", bookFromStore);
        return;
    }
    
    // rbook 데이터 복사
    rbook.value = { ...bookFromStore };
    console.log("초기 rbook 값:", rbook.value);

    // 사용자 독서 상태 로드
    await loadUserGoalExist();

    // 날짜 초기화
    startDate.value = '';
    endDate.value = '';

    // 진행률 초기화
    const savedProgress = progressStore.getProgress(rbook.value.isbn13);
    if (savedProgress) {
        rbook.value.progressPercentage = savedProgress.progressPercentage || 0;
    } else {
        rbook.value.progressPercentage = 0;
    }

    console.log("초기 상태 확인:", rbook.value.status, radioSelect.value);
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
    display: grid;
    justify-items: center;
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
    padding: 12px 20px;
    border-radius: 30px;
    border: 1px solid gray;
    width: 120px;
    background: #fff8bb;
    cursor: pointer;
}

.cancel-button {
    padding: 12px 20px;
    border-radius: 30px;
    border: 1px solid gray;
    width: 120px;
    background: #ffffff;
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
    gap: 20px;
    margin-bottom: 10px;
    height: 40px;
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

