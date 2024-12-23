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
        <div class="date-status" v-if="rbook.status==='reading'">독서상태</div>
        <div class="date-row-status" v-if="rbook.status==='reading'">
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
             <!-- 날짜가 모두 선택되었을 때만 렌더링 -->
            <div v-if="startDate && endDate" class="date-status">독서 상태</div>
            <div v-if="startDate && endDate" class="date-row-status">
                <span class="date-label">
                    <input type="radio" value="reading" v-model="radioSelect">독서중
                </span>
                <span class="date-label">
                    <input type="radio" value="dropped" v-model="radioSelect">독서 중 해제
                </span>
            </div>
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
// const rbook= bookStore.rbook;
const rbook = ref({});

const isLoading= ref(false);

// API 요청으로 도서 정보 불러오기
// const fetchBookData = async () => {
//     if (!props.isbn13) return;

//     try {
//         isLoading.value = true;
//         const response = await apiClient.get(`/api/library/${authStore.user.userId}/${props.isbn13}`);
//         rbook.value = response.data.data;

//         startDate.value = rbook.value.startDate || null;
//         endDate.value = rbook.value.endDate || null;
//         console.log("도서 정보:", rbook.value);
//     } catch (error) {
//         console.error("도서 정보 불러오기 실패:", error);
//     } finally {
//         isLoading.value = false;
//     }
// };

const route= useRoute();
const router= useRouter();
const progressStore= useProgressStore();

const libraryId = ref(''); // 초기값 null
const isInLibrary = ref(false); // 서재 상태 저장 변수



const props = defineProps({
    visible: Boolean ,
    isbn13: {String,  require:true,}
});

const emit= defineEmits(["close","dropReading"]);
const emitClose= () => {
    emit("close");
};
//날짜 포맷팅
const dateFormat = "yyyy-MM-dd";
//독서기간 변경
const changeDate= async (rbook) => {
    const utilModalStore = useUtilModalStore();
    const formatStartDate= format(new Date(startDate.value),"yyyy-MM-dd");
    const formatEndDate= format(new Date(endDate.value),"yyyy-MM-dd");
    
    try{
        const response= await apiClient.put(`/api/goal/reset/${rbook.isbn13}`,null, {
            headers: {
                Authorization: `Bearer ${authStore.token}`, // JWT 토큰 추가
            },
            params: {
                status: rbook.status,
                startDate: formatStartDate,
                endDate: formatEndDate,
            },
        });
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
    rbook.startDate= format(new Date(value),'yyyy-MM-dd');
    checkDateSelection();
};
const updateEndDate = (value) => {
    endDate.value = value;
    rbook.endDate= format(new Date(value),'yyyy-MM-dd');
    checkDateSelection();
};
const checkDateSelection = () => {
    // 두 날짜 모두 선택되었을 때만 라디오 버튼 표시
    if (startDate.value && endDate.value) {
        radioSelect.value = "reading"; 
    }else {
        radioSelect.value = ""; 
    }
};
const startDate = ref(null);
const endDate = ref(null);
const radioSelect= ref("");

// 내 서재 상태 확인
const checkLibraryStatus = async () => {
    try {
        const response = await apiClient.get(`/api/library/${authStore.user.userId}/${rbook.value.isbn13}`);
        // const libraryItems = response.data.data || [];

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
        // DB 조회로 상태 동기화
        await checkLibraryStatus();

        if (!rbook.value.isbn13) {
            console.error("ISBN 값이 정의되지 않았습니다.");
            utilModalStore.showModal(
                "오류 발생",
                "ISBN 값이 누락되었습니다. 다시 시도해주세요.",
                "error"
            );
            return; // API 호출 중단
        }

        const currentStatus = rbook.value.status;
        console.log("현재 도서 상태:", currentStatus);

        // 'wished' 상태일 때 'reading'으로 변경하여 목표 설정
        if (currentStatus === "wished" && radioSelect.value === "reading") {
            rbook.value.status = "reading"; // 현재 상태를 'reading'으로 변경
            console.log("상태 변경됨:", rbook.value.status);

            await setGoal(rbook); // 독서 목표 설정
            // utilModalStore.showModal(
            //     "독서 목표 설정",
            //     `"${rbook.title}" 독서 목표로 설정되었습니다.`,
            //     "success"
            // );
            return; // 실행 완료 후 종료
        }

        // if (currentStatus === "reading") {
        //     if (radioSelect.value === "dropped") {
        //         await dropReading(rbook);
        //         utilModalStore.showModal(
        //             "독서 상태 변경",
        //             `"${rbook.title}" 독서 상태가 해제되었습니다.`,
        //             "success"
        //         );
        //     } else {
        //         await changeDate(rbook); // 날짜 변경
        //         utilModalStore.showModal(
        //             "목표 기간 변경",
        //             `"${rbook.title}"의 독서 기간이 수정되었습니다.`,
        //             "success"
        //         );
        //     }
        // } else {
        //     utilModalStore.showModal(
        //         "오류 발생",
        //         "작업 중 오류가 발생했습니다. 다시 시도해주세요.",
        //         "error"
        //     );
        // }
                // 'reading' 상태에서 날짜 변경 처리
                if (currentStatus === "reading") {
            if (radioSelect.value === "dropped") {
                await dropReading(rbook);
            } else {
                await changeDate(rbook); // 날짜 변경
            }
        } else {
            utilModalStore.showModal(
                "오류 발생",
                "작업 중 오류가 발생했습니다. 다시 시도해주세요.",
                "error"
            );
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

const setGoal = async (rbook) => {
    const utilModalStore = useUtilModalStore(); 

    if (!rbook.value.isbn13) {
        console.error("ISBN 값이 정의되지 않았습니다.",rbook.value.isbn13);
        utilModalStore.showModal(
            "오류 발생",
            "ISBN 값이 누락되었습니다. 다시 시도해주세요.",
            "error"
        );
        return; // API 호출 중단
    }

    if (!startDate.value || !endDate.value) {
        console.error("날짜 값이 선택되지 않았습니다.");
        utilModalStore.showModal(
            "오류 발생",
            "시작일과 종료일을 선택해주세요.",
            "error"
        );
        return;
    }


    const formatStartDate= format(new Date(startDate.value),"yyyy-MM-dd");
    const formatEndDate= format(new Date(endDate.value),"yyyy-MM-dd");
    try{
        const response= await apiClient.put(`/api/goal/register/${rbook.value.isbn13}`, null, {
            params: {
                status: "reading",
                startDate: formatStartDate,
                endDate: formatEndDate,
            },
        });
        utilModalStore.showModal(
            "독서 목표 설정",
            `"${rbook.value.title}"의 독서 목표가 설정되었습니다.`,
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
            `"${rbook.title}" 독서 상태가 해제되었습니다.`,
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
const progress = progressStore.getProgress(rbook.isbn13);
    return progress?.progressPercentage || 0;     
});

// Pinia 상태 감지 및 업데이트
// watch(
//     () => progressStore.progressData[rbook.isbn13],
//     (newProgress) => {
//         if (newProgress) {
//             rbook.currentPage = newProgress.currentPage;
//             rbook.progressPercentage = newProgress.progressPercentage;
//         }
//     },
//     { deep: true, immediate: true }
// );

// watch(rbook, (newVal) => {
//     console.log("rbook 변경 감지:", newVal);
//     startDate.value = newVal.startDate || null;
//     endDate.value = newVal.endDate || null;
// }, { deep: true });

watch(
    () => bookStore.rbook,
    (newBook) => {
        if (newBook && newBook.isbn13)  {
            rbook.value= {...newBook};
            startDate.value= newBook.startDate || null;
            endDate.value= newBook.endDate || null;
            console.log("새로운 도서정보",rbook.value);
        }
    },
    { deep: true, immediate: true }
);

onMounted(async () => {

    // rbook 초기화
    const bookFromStore = bookStore.rbook || {};
    rbook.value = { ...bookFromStore };

    // rbook 값 확인
    console.log("초기 rbook 값:", rbook.value);

    // 1. props로 전달된 isbn13 사용하여 초기화
    if (!rbook.value.isbn13 && props.isbn13) {
        rbook.value.isbn13 = props.isbn13; // props로부터 ISBN 설정
    }
    console.log("초기 ISBN 값 확인:", rbook.value.isbn13);


    // 날짜 초기화
    startDate.value = rbook.value.startDate || null;
    endDate.value = rbook.value.endDate || null;

    console.log("초기화 완료 - ISBN:", rbook.value.isbn13);

    const savedProgress= progressStore.getProgress(rbook.isbn13);
    if(savedProgress){
        rbook.currentPage=savedProgress.currentPage || 0;
        rbook.progressPercentage=savedProgress.progressPercentage || 0;
    }
    console.log("rbook : ", rbook.value.data);
    if(props.book){
        fetchBookData();
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

