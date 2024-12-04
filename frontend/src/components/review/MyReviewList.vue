<template>
  <div class="review-container">
    <div class="review-header">
      <h2>나의 리뷰</h2>
      <hr style="color: #909090; opacity: 0.7;">
    </div>
    <section  
      class="review-item" 
      v-for="(review, index) in reviews" 
      :key="index"
    >
      <img class="review-image" :src="review.image" :alt="review.title" />
      <div class="review-content">
        <div class="remove-review">
          <img src="@/assets/icons/close.png" @click="review.showModal = true" class="removeIcon">
          <!-- 모달 표시 -->
          <RemoveReview v-model:isVisible="review.showModal" :deleteId="index" @delete-review="deleteReview" />
        </div>
        <div class="title-icon">
          <h3 class="title">{{ review.title }}</h3>
        <div class="rating">
          <div>
            <template v-if="editingIndex === index">
              <img v-for="starIndex in 5"
              :key="starIndex"
              class="star" :src="starIndex <= editingReview.rating ? fullStarImage : emptyStarImage"
              @click="ratingStar(starIndex)" />
            </template>
            <template v-else>
              <img :src="fullStarImage" class="star">
            </template>
            <span class="rating-value">
              <template v-if="editingIndex === index">
                {{ editingReview.rating }}
              </template>
              <template v-else>
                {{ review.rating }}
              </template>
            </span>
          </div>
        </div>
          <div class="edit-review">
            <img src="@/assets/icons/edit-2-line.png" @click="openInput(index)" 
            :style="{display: editingIndex === index ? 'none' : 'block' }"
            style="width: 27px; margin-top: px;">
          </div>
          <span class="date">{{ review.date }}</span>
      </div>
        <div class="content-icon" style="display: flex;">
          <p class="summary">
          <template v-if="editingIndex === index">
            <textarea v-model="editingReview.summary" class="textbox"></textarea>
          </template>
          <template v-else>{{ review.summary }}</template>
        </p>
        <div v-if="editingIndex === index">
          <img src="@/assets/icons/task-line.png" @click="saveReview(index)" class="task-line">
        </div> 
        </div>
      </div>
    </section>
    </div>
</template>

<script>
import fullStarImage from "@/assets/icons/full_star.png";
import emptyStarImage from "@/assets/icons/empty_star.png";
import RemoveReview from "./RemoveReview.vue";
import { ref } from "vue";

export default {
  components : {
    RemoveReview,
  },
  setup() {
    const reviews = ref([
      {
        title: "소년이 온다",
        image: "image-40.png",
        summary: "소년이 온다에 대한 한줄평을 이 곳에 띄운다.",
        date: "2024-11-29",
        rating: 5,
        showModal: false, //개별로 등록해야 각각의 값으로 인식하고 삭제가 진행된다.
      },
      {
        title: "면도날",
        image: "image-110.png",
        summary: "면도날 한줄평을 수정할 때 입니다.",
        date: "2024-10-19",
        rating: 4,
        showModal : false,
      },
    ]);
    const editingIndex = ref(null); // 어떤 리뷰가 수정 중인지 추적
    const editingReview = ref({
      title: "",
      rating: 1,
      summary: "",
    });

    const openInput = (index) => {
      console.log("수정하려는 값 : "+ index);
      editingIndex.value = index;
      editingReview.value = { ...reviews.value[index] }; // 리뷰의 데이터를 복사하여 편집용 데이터로 설정
    };

    const saveReview = (index) => {
      reviews.value[index] = { ...editingReview.value }; // 수정된 리뷰를 저장
      editingIndex.value = null; // 수정 모드 종료
    };

    const deleteReview = (index) => {
          console.log("삭제하려는 값 : "+ index);
            reviews.value.splice(index, 1); // 리뷰 삭제
       };

    const ratingStar = (starIndex) => {  //별점 값 변경
      editingReview.value.rating = starIndex;
    }
    return {
      reviews,
      fullStarImage,
      emptyStarImage,
      openInput,
      saveReview,
      deleteReview,
      editingIndex,
      editingReview,
      ratingStar,
    };
  },
};
</script>

  
  <style scoped>
  /* 공통 스타일 */
  .review-container {
    width: 100%;
    max-width: 700px;
    margin: 180px auto;
    padding: 20px;
    box-sizing: border-box;
    background-color: #ffffff;
  }
  
  .review-header {
    text-align: left;
    margin-bottom: 20px;
  }
  
  .review-header h2 {
    font-family: "Inter-Bold", sans-serif;
    font-size: 23px;
    font-weight: 500;
    color: #000000;
  }
  
  .review-item {
    display: flex;
    align-items: flex-start;
    gap: 20px;
    margin-bottom: 20px;
    border-bottom: 1px solid #dcdcdc;
    padding: 20px 40px;
  }
  
  .review-image {
    width: 120px;
    height: 140px;
    object-fit: cover;
    border: 1px solid #c0c0c0;
    border-radius: 5px;
  }
  
  .edit-review {
    margin-top: 3px;
    padding-bottom: 10px;
  }

  .edit-review:hover {
    cursor: pointer;
  }

  .review-content {
    flex: 1;
    }
  
  .remove-review{
    margin-left: auto;
  }

  .title-icon {
    display: flex;
    gap : 40px;
    align-items: center;
  }

  .title {
    font-family: "Inter-Bold", sans-serif;
    font-size: 23px;
    font-weight: 500;
    color: #000000;
    margin: 0 0 10px;
  }
  
  .rating {
    display: flex;
    align-items: center;
    justify-content: space-between; /* 양쪽 끝으로 정렬 */
    gap: 5px;
    margin: 5px 0 15px;
  }

  .stars-and-edit {
    display: flex;
    align-items: center;
  } 
  .rating .star {
    width: 24px;
    height: 24px;
  }
  .star:hover {
    cursor: pointer;
  }
  
  .rating-value {
    font-family: "Inter-Regular", sans-serif;
    font-size: 22px;
    font-weight: 400;
    color: #000000;
    margin: auto 10px;
  }
  
  .summary {
    font-family: "Inter-Regular", sans-serif;
    font-size: 20px;
    font-weight: 400;
    color: #000000;
    margin: 0 0 10px;
  }
  
  .date {
    font-family: "Inter-Regular", sans-serif;
    font-size: 16px;
    font-weight: 400;
    color: #909090;
    margin-left: auto; /* 오른쪽으로 밀기 */
  }

  .task-line:hover {
    cursor: pointer;
    margin : 10px;
  }
  
  .task-line {
    margin : 10px;
  }

  .textbox {
    resize: none;
    width: 400px;
    height: auto;
    font-size: 16px;
    border: none;
    background-color: #dcdcdc;
    text-indent: 10px;
    line-height: 1.5;
    padding-top: 10px;
    border-radius: 5px;
  }

  .removeIcon:hover {
    cursor: pointer;
  }

  .remove-review {
    display: flex;
    flex-direction: row-reverse;
  }
  </style>
  