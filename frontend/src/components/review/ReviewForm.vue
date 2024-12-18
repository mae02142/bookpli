<template>
    <!-- 모달 배경 -->
    <section v-if="isVisible" class="modal-overlay" @click="cancelForm">
      <div class="modal-content" @click.stop> <!-- 이벤트 버블링 막기 -->
        <!-- 모달 내용 -->
      <div class="review-form">
        <div class="date-rating">
          <div class="form-group">
            <label for="rating">별점</label>
            <div class="star-rating">
              <img
                v-for="index in 5"
                :key="index"
                :src="index <= rating ? fullStarImage : emptyStarImage"
                alt="star"
                class="star-icon"
                @click="updateRating(index)"
              />
            </div>
          </div>
        </div>
  
          <div class="form-reviewContent">
            <label for="reviewContent">한 줄 독서</label>
            <textarea
              id="reviewContent"
              v-model="reviewContent"
              maxlength="150"
              placeholder="책을 읽으면서 느꼈던 생각이나 감상을 자유롭게 기록해보세요"
              @input="handleSummaryInput"
            />
            <div class="char-count">{{ charCount }}/150</div>
          </div>
  
          <div class="form-actions">
            <button class="submit-btn" @click.prevent="submitForm">등록</button>
            <button class="cancel-btn" @click="cancelForm">취소</button>
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script>
  import fullStarImage from "@/assets/icons/full_star.png";
  import emptyStarImage from "@/assets/icons/empty_star.png"
  import { ref } from "vue";
  import apiClient from "@/api/axiosInstance";
  import { useUtilModalStore } from "@/stores/utilModalStore";


  export default {
    props: {
      isVisible: {
        type: Boolean,
        default: false,
      },
      userId: {
        type : Number,
        required: true,
      },
      bookId: {
        type : Object,
        required: true,
      }
    },
    emits: ['update:isVisible'],
    setup(props, { emit }) {
    
      const rating = ref(0);
      const reviewContent = ref("");
      const charCount = ref(0);

      // 모달 닫기
      const closeModal = () => {
        emit('update:isVisible', false);
        return undefined;
      };
  
      // 별점 업데이트
      const updateRating = (index) => {

        rating.value = index;
      };
  
      // 한 줄 독서 글자 수 관리
      const handleSummaryInput = (event) => {
        charCount.value = event.target.value.length;
      };
  
      // 취소 버튼 클릭 시
      const cancelForm = () => {
        rating.value = 0;
        reviewContent.value = "";
        charCount.value = 0;
        closeModal();
        return undefined;
      };
  
      // 폼 제출
      const utilModalStore = useUtilModalStore();
      const submitForm = async() => {
        if ( !reviewContent.value || rating.value === 0) {
          utilModalStore.showModal(
        '경고',
        '모든 항목을 작성해주세요.',
        'alert'
       )
          return undefined;
        }else{
          try{
        const review =  {
        userId : props.userId,
        isbn13 : props.bookId.isbn13,
        reviewContent: reviewContent.value ,
        rating: rating.value
      };
        await apiClient.post("/api/review/post",review);
       
       utilModalStore.showModal(
        '리뷰 등록',
        '리뷰가 등록되었습니다.',
        'alert'
       )
      } catch(error){
        console.error(error,"등록 중 에러 발생");
      };
    }
      cancelForm();
  };
  
      return {
        rating,
        reviewContent,
        charCount,
        fullStarImage,
        emptyStarImage,
        updateRating,
        handleSummaryInput,
        cancelForm,
        submitForm,
        closeModal, 
      };
  },
};
  </script>
  
  <style scoped>
  /* 모달 오버레이 */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
  }
  
  /* 모달 콘텐츠 */
   .modal-content {
    background: white;
    padding: 40px;
    border-radius: 10px;
    width: 550px;
    max-width: 90%;
    height: 300px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: hidden;
  } 
  .review-form {
    width: 100%;
  }
  
  .form-group {
    margin-bottom: 20px;
    display: flex;
    flex-direction: column;
  }
  
  label {
    font-size: 15px;
    font-weight: 400;
    color: #000;
    display: block;
    text-align: left;
    margin-bottom: 20px;
  }
  textarea {
    resize: none;
    min-height: 100px;
    height: auto;
    width: 100%;
    outline: none;
    padding: 10px;
  }
  
  .star-rating {
    display: flex;
  }
  
  .star-icon {
    width: 30px;
    height: 30px;
    cursor: pointer;
    object-fit: cover;
  }
  
  .char-count {
    font-size: 16px;
    color: #595959;
    text-align: right;
    margin: 5px;
  }
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 20px;
  }
  
  .cancel-btn,
  .submit-btn {
    padding: 10px 20px;
    font-size: 16px;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .cancel-btn {
    background-color:#FFFDF1;
    border: none;
  }
  
  .submit-btn {
    background-color: #FFFDF1;
    border: none;
  }
  
  .calendar-icon {
    position: absolute;
    left: 350px;
    top: 120px;
    width: 40px;
    height: 47px;
  }

  .date-rating {
    display: flex;
    flex-direction: row;
    gap : 110px;

  }
  </style>
  