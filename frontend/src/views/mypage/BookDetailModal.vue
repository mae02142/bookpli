<template>
    <div v-if="book" class="detail-modal-container">
      <div class="detail-modal-content">
        <div class="bookinfo-grid">
          <img
            class="modal-image"
            :src="`${book.cover}`"
            alt="Book Image"
          />
          <div class="progress-grid">
            <p class="book-title">{{ book.title }}</p>
            <p class="book-author">{{ book.author }}</p>
            <div class="progress-container" v-if="book.status === 'reading'">
              <img src="@/assets/icons/read_book.png">
              <span class="progress-text">{{ book.progress }}%</span>
              <span class="remaining-days">{{ book.remainingDays }}일 남음</span>
            </div>
            <div class="complete-container" v-if="book.status === 'completed'">
              <img src="@/assets/icons/read_complete.png">
              독서 완료
            </div>
          </div>
          <img v-if="isLiked" class="like-icon" src="@/assets/icons/like.png"
            alt="Like Icon" @click="changeToDislike" />
          <img v-else class="like-icon" src="@/assets/icons/dislike.png"
            alt="Like Icon" @click="changeToLike(book)" />
        </div>
        <div class="btn-grid">
          <p v-if="!isInLibrary" class="btn add-to-library" @click="handleAddToLibrary">서재에 담기</p>
          <p v-else class="btn change-status" v-if="props.book.status!=='completed'" @click="openGoalModal(book)">
            독서상태 변경
          </p>
          <p class="btn write-review" @click="writeReview">리뷰 작성</p>
          <p class="btn" @click="handleClick">도서 상세 보기</p>
          <p class="btn remove-book" @click="removeBook">
            내 서재에서 삭제
          </p>
          <p class="btn confirm" @click="closeModal">확인</p>
        </div>
      </div>
    </div>
    <ReadGoalModal
            :visible="readGoalToggle"
            :rbook="bookData"
            @close="closeModal"
        />
  </template>
  
  <script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouterUtils } from '@/router/routerUtils';
import apiClient from '@/api/axiosInstance';
import { addBookLike, removeBookLike } from '@/utils/likeUtils';
import { useAuthStore } from '@/stores/auth';
import ReadGoalModal from "@/components/readGoal/ReadGoalModal.vue";

const authStore = useAuthStore();
const { gotoDetail } = useRouterUtils();
const bookLikeId = ref(null);
const readGoalToggle = ref(false);
const bookData= ref({});
const isInLibrary = ref(false); // 서재 포함 여부

const openGoalModal = (book) => {
  if (!isInLibrary.value) {
    alert('먼저 서재에 담아주세요!');
    return;
  }
  bookData.value = book;
  readGoalToggle.value = true;
};

const closeGoalModal = () => {
  readGoalToggle.value = false;
};

const isLiked = computed(() => bookLikeId.value !== null);

  // 부모로부터 전달받은 책 데이터
  const props = defineProps({
    book: {
    type: Object,
    required: true,
  },
  });
  
  // 부모로 이벤트 전달
  const emit = defineEmits(['close','openForm', 'delete-library', 'book-like-status']);
  

  // 좋아요 상태 관리
  const changeToLike = async (book) => {
    bookLikeId.value = await addBookLike(authStore.user.userId, book);
    emit('book-like-status', book.isbn13);
    
  };

  const changeToDislike = async () => {
    const result = await removeBookLike(bookLikeId.value);
    if (result) {
      bookLikeId.value = null;
      emit('book-like-status', props.book.isbn13);
  }
  }
  
  const writeReview = () => {
    emit('openForm'); //이벤트 전달
    emit('close');
  };
  
  const removeBook = async () => {
    emit('delete-library', props.book.libraryId); // 부모에게 이벤트 전달
    emit('close');
  };
  
  // 모달 닫기
  const closeModal = () => {
    emit('close'); // 부모로 close 이벤트 전달
  };

  const handleClick = () => {
    gotoDetail(props.book.isbn13);
  }

  const getBookLikeStatus = async () => {
  try {
    const response = await apiClient.get(`/api/library/${authStore.user.userId}/book/${props.book.isbn13}`);
    bookLikeId.value = response.data.data;
  } catch (error) {
    console.error("좋아요 상태를 가져오는 중 오류 발생: ", error);
  }
};

// 서재에 책 추가
const addBookToLibrary = async (book) => {
  try {
    await apiClient.post('/api/library', { // API 경로 확인 필요
      bookId: book.id,
      title: book.title,
      author: book.author,
      cover: book.cover,
    });
    isInLibrary.value = true; // 서재에 담김 표시
  } catch (error) {
    console.error('책 추가 실패:', error);
  }
};

// 서재에 담기 버튼 로직
const handleAddToLibrary = async () => {
  if (!isInLibrary.value) {
    await addBookToLibrary(book.value);
  }
};

  onMounted(()=> {
    getBookLikeStatus();
    console.log("::::::::::::;",props.book);
  })
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
  