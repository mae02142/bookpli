<template>
  <div class="detail-modal-container">
    <div class="detail-modal-content">
      <div class="bookinfo-grid">
        <img class="modal-image" :src="localBook.cover" alt="Book Image" />
        <div class="progress-grid">
          <p class="book-title">{{ localBook.title }}</p>
          <p class="book-author">{{ localBook.author }}</p>
          <div class="progress-container" v-if="book.status === 'reading'">
            <img src="@/assets/icons/read_book.png" />
            <span class="progress-text">{{ localBook.progress }}%</span>
            <span class="remaining-days">{{ localBook.remainingDays }}일 남음</span>
          </div>
          <div class="complete-container" v-if="localBook.status === 'completed'">
            <img src="@/assets/icons/read_complete.png" />
            독서 완료
          </div>
        </div>
        <img
          v-if="isLiked"
          class="like-icon"
          src="@/assets/icons/like.png"
          alt="Like Icon"
          @click="changeToDislike"
        />
        <img
          v-else
          class="like-icon"
          src="@/assets/icons/dislike.png"
          alt="Dislike Icon"
          @click="changeToLike(localBook.isbn13)"
        />
      </div>
      <div class="btn-grid">
        <p
          v-if="!localBook.status"
          class="btn add-to-library"
          @click="handleAddToLibrary"
        >서재에 담기</p>
        <p
          class="btn change-status"
          v-if="localBook.status === 'wished' || localBook.status === 'reading'"
          @click="openGoalModal(localBook)"
        >독서 상태 변경</p>
        <p class="btn write-review" @click="writeReview">리뷰 작성</p>
        <p class="btn" @click="handleClick">도서 상세 보기</p>
        <p class="btn remove-book" @click="removeBook(localBook.libraryId)" v-if="localBook.status === 'wished' || localBook.status === 'reading' || localBook.status === 'completed'">내 서재에서 삭제</p>
        <p class="btn confirm" @click="closeModal">확인</p>
      </div>
    </div>
    <ReadGoalModal
      :visible="readGoalToggle"
      :rbook="bookData"
      @close="closeGoalModal"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { useRouterUtils } from "@/router/routerUtils";
import apiClient from "@/api/axiosInstance";
import { addBookLike, removeBookLike } from "@/utils/likeUtils";
import ReadGoalModal from "@/components/readGoal/ReadGoalModal.vue";

// Store 및 유틸리티
const { gotoDetail } = useRouterUtils();

// 상태 변수
const bookLikeId = ref(null);
const readGoalToggle = ref(false);
const bookData = ref({});

// Props 정의
const props = defineProps({
  book: {
    type: Object,
    required: true,
  },
});

// 로컬 상태로 사용할 book 데이터
const localBook = ref({ ...props.book });

// props.book 변화를 감지하여 localBook을 업데이트
watch(
  () => props.book,
  (newBook) => {
    localBook.value = { ...newBook };
  },
  { deep: true }
);

const emit = defineEmits(["close", "openForm", "delete-library", "book-like-status", "update-book-status"]);

const isLiked = computed(() => bookLikeId.value !== null);

const getBookLikeStatus = async () => {
  try {
    const response = await apiClient.get(
      `/api/library/book/${props.book.isbn13}`
    );
    bookLikeId.value = response.data.data;
  } catch (error) {
    console.error("좋아요 상태를 가져오는 중 오류 발생:", error);
  }
};

const handleAddToLibrary = async () => {
  try {
    // API 요청
    const response = await apiClient.post(`/api/library/${localBook.value.isbn13}`);

    // 로컬 데이터 업데이트
    localBook.value = response.data.data;

    // 부모에게 업데이트된 도서 데이터 전달
    emit("update-book-status", localBook.value );
  } catch (error) {
    console.error("서재 추가 실패:", error);
  }
};


const changeToLike = async (isbn13) => {
  bookLikeId.value = await addBookLike(isbn13);
  emit("book-like-status", isbn13);
};

const changeToDislike = async () => {
  const result = await removeBookLike(bookLikeId.value);
  if (result) {
    bookLikeId.value = null;
    emit("book-like-status", props.book.isbn13);
  }
};

const openGoalModal = (book) => {
  bookData.value = book;
  readGoalToggle.value = true;
};

const closeGoalModal = () => {
  readGoalToggle.value = false;
};

const writeReview = () => {
  emit("openForm");
  emit("close");
};

const removeBook = async (libraryId) => {
  emit("delete-library", libraryId);
  emit("close");
};

const handleClick = () => {
  gotoDetail(props.book.isbn13);
};

const closeModal = () => {
  emit("close");
};

onMounted(() => {
  getBookLikeStatus();
});
</script>
  
  <style scoped>
  /* 모달 컨테이너 */
  .detail-modal-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 1000;
  }
  
  /* 모달 내용 */
  .detail-modal-content {
    border-radius: 10px;
    padding: 20px;
    width: 460px;
    text-align: center;
    position: relative;
    background-color: white;
    justify-items: center;
  }

  .bookinfo-grid {
    display: flex;
    margin: 25px 0px;
    justify-content: center;
    max-width: 380px;
    min-width: 380px;
  }

  .progress-grid {
    display: flex;
    flex-direction: column;
    margin-left: 17px;
    margin-right: 13px;
    align-items: flex-start;
    min-width: 160px;
    max-width: 185px;
  }
  
  /* 이미지 */
  .modal-image {
    width: 140px;
    height: 200px;
    margin-bottom: 15px;
    border-radius: 3px;
  }
  
  /* 책 제목 */
  .book-title {
    font-size: 17px;
    color: #1f1f1f;
    margin-top: 3px;
    margin-bottom: 6px;
    font-weight: bold;
    text-align: start;
  }
  
  /* 책 저자 */
  .book-author {
    font-size: 13px;
    color: #3d3d3d;
    margin-bottom: 15px;
    text-align: start;
  }
  
  /* 진행률 및 남은 일 */
  .progress-container {
    display: flex;
    justify-content: center;
    gap: 5px;
    align-items: center;
    margin-bottom: 20px;
    border: 1px solid #acacac;
    padding: 5px;
    border-radius: 5px;
  }

  .complete-container {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 15px;
    gap: 5px;
    border: 1px solid #acacac;
    padding: 5px;
    border-radius: 5px;
  }
  
  .complete-container img{
    width: 25px;
  }
  
  .progress-text {
    font-size: 15px;
    color: #414141;
    margin-left: 5px;
  }
  
  .remaining-days {
    font-size: 15px;
    color: #ff5e00;
  }
  
  /* 좋아요 아이콘 */
  .like-icon {
    width: 24px;
    height: 22px;
    cursor: pointer;
    margin-top: 3px;
  }
  
  /* 버튼 스타일 */
  .btn {
    width: 363px;
    padding: 15px 10px;
    border: 1px solid #929292;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
    background-color: white;
  }

  .btn-grid {
    display: flex;
    gap: 10px;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .btn.change-status {
    color: #1f1f1f;
  }
  
  .btn.write-review {
    color: #1f1f1f;
  }
  
  .btn.remove-book {
    color: #ff5e00;
  }
  
  .btn.confirm {
    color: #1f1f1f;
  }

  .btn:hover {
    font-weight: bold;
  }

  .progress-container img{
    width: 25px;
  }
  </style>
  