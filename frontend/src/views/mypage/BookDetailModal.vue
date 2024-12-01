<template>
    <div v-if="book" class="modal-container">
      <div class="modal-content">
        <div class="bookinfo-grid">
          <img
            class="modal-image"
            :src="`/src/assets/icons/${book.coverImage}`"
            alt="Book Image"
          />
          <div class="progress-grid">
            <p class="book-title">{{ book.title }}</p>
            <p class="book-author">{{ book.author }}</p>
            <div class="progress-container">
              <span class="progress-text">{{ book.progress }}%</span>
              <span class="remaining-days">{{ book.remainingDays }}일 남음</span>
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
          <button class="btn change-status" @click="changeStatus">
            독서상태 변경
          </button>
          <button class="btn write-review" @click="writeReview">리뷰 작성</button>
          <button class="btn remove-book" @click="removeBook">
            내 서재에서 삭제
          </button>
          <button class="btn confirm" @click="closeModal">확인</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { defineProps, defineEmits } from 'vue';
  
  // 부모로부터 전달받은 책 데이터
  const props = defineProps({
    book: Object,
  });
  
  // 부모로 이벤트 전달
  const emit = defineEmits(['close']);
  
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
    alert('리뷰 작성 페이지로 이동합니다!');
  };
  
  const removeBook = () => {
    alert('책을 서재에서 삭제합니다!');
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
    width: 500px;
    text-align: center;
    position: relative;
    background-color: white;
  }

  .bookinfo-grid {
    display: flex;
    margin: 25px 0px;
  }

  .progress-grid {
    display: flex;
    flex-direction: column;
    margin-left: 15px;
    align-items: flex-start;
    min-width: 160px;
  }
  
  /* 이미지 */
  .modal-image {
    width: 220px;
    height: 300px;
    margin-bottom: 15px;
    object-fit: cover;
    margin-left: 40px;
    border-radius: 3px;
  }
  
  /* 책 제목 */
  .book-title {
    font-size: 18px;
    color: #1f1f1f;
    margin: 10px 0;
    font-weight: bold;
  }
  
  /* 책 저자 */
  .book-author {
    font-size: 15px;
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
    margin-top: 7px;
  }
  
  /* 버튼 스타일 */
  .btn {
    width: 85%;
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
  