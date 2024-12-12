<template>
    <div v-if="book" class="modal-container">
      <div class="modal-content">
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
              <span class="progress-text">{{ book.progress }}%</span>
              <span class="remaining-days">{{ book.remainingDays }}일 남음</span>
            </div>
            <div class="complete-container" v-if="book.status === 'completed'">
              <img src="@/assets/icons/read_complete.png">
              독서 완료
            </div>
          </div>
          <img
            class="like-icon"
            src="@/assets/icons/like.png"
            alt="Like Icon"
            @click="toggleLike"
          />
        </div>
        <div class="btn-grid">
          <p class="btn change-status" @click="changeStatus">
            독서상태 변경
          </p>
          <p class="btn write-review" @click="writeReview">리뷰 작성</p>
          <p class="btn remove-book" @click="removeBook(book.libraryId)">
            내 서재에서 삭제
          </p>
          <p class="btn confirm" @click="closeModal">확인</p>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
import apiClient from '@/api/axiosInstance';
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth'; 

const authStore = useAuthStore();
   
  // 부모로부터 전달받은 책 데이터
  const props = defineProps({
    book: {
    type: Object,
    required: true,
  },
  });
  
  // 부모로 이벤트 전달
  const emit = defineEmits(['close','openForm', 'update-library']);
  
  // 좋아요 상태 관리
  const isLiked = ref(false);
  const toggleLike = () => {
    isLiked.value = !isLiked.value;
  };
  
  // 이벤트 핸들러
  const changeStatus = () => {
    alert('독서 상태를 변경합니다!');
  };

  const writeReview = () => {
    emit('openForm'); //이벤트 전달
    closeModal();
  };
  
  const removeBook = async (libraryId) => {
    console.log("라이브러리 아이디 >>>>> " , libraryId);
    try {
      await apiClient.delete(`/api/library`, {
      data: {
        userId: authStore.user.userId,
        libraryId: libraryId,
      },
    });
      emit('update-library');
      closeModal();
    } catch (error) {
      console.log(error);
    }
  };
  
  // 모달 닫기
  const closeModal = () => {
    emit('close'); // 부모로 close 이벤트 전달
  };
  </script>
  
  <style scoped>
  /* 모달 컨테이너 */
  .modal-container {
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
  .modal-content {
    border-radius: 10px;
    padding: 20px;
    width: 430px;
    text-align: center;
    position: relative;
    background-color: white;
  }

  .bookinfo-grid {
    display: flex;
    margin: 25px 0px;
    justify-content: center;
  }

  .progress-grid {
    display: flex;
    flex-direction: column;
    margin-left: 12px;
    align-items: flex-start;
    min-width: 160px;
  }
  
  /* 이미지 */
  .modal-image {
    width: 160px;
    height: 220px;
    margin-bottom: 15px;
    object-fit: cover;
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
    max-width: 160px;
  }
  
  /* 책 저자 */
  .book-author {
    font-size: 13px;
    color: #3d3d3d;
    margin-bottom: 15px;
  }
  
  /* 진행률 및 남은 일 */
  .progress-container {
    display: flex;
    justify-content: center;
    gap: 10px;
    align-items: center;
    margin-bottom: 20px;
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
    width: 340px;
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
    margin-bottom: 25px;
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
  </style>
  