<template>
  <div class="review-container">
    <div class="review-header">
      <h2>나의 리뷰</h2>
      <hr style="color: #909090; opacity: 0.7;">
    </div>
    <section  
      class="review-item" 
      v-for="review in reviews" 
      :key="review.reviewId"
    >
      <img class="review-image" :src="review.image" :alt="review.title" />
      <div class="review-content">
        <div class="remove-review">
          <img src="@/assets/icons/close.png" @click="review.showModal = true" class="removeIcon">
          <!-- 모달 표시 -->
          <RemoveReview v-model:isVisible="review.showModal" :deleteId="review.reviewId" @delete-review="deleteReview" />
        </div>
        <div class="title-icon">
          <h3 class="title">{{ review.title }}</h3>
        <div class="rating">
          <article style="display: flex;">
            <div v-if="editingId === review.reviewId">
              <img v-for="starIndex in 5"
              :key="starIndex"
              class="star" :src="starIndex <= editingReview.rating ? fullStarImage : emptyStarImage"
              @click="ratingStar(starIndex)" />
            </div>
            <div v-else>
              <img :src="fullStarImage" class="star">
            </div>
            <span class="rating-value">
              <div v-if="editingId === review.reviewId">
                {{ editingReview.rating }}
              </div>
              <div v-else>
                {{ review.rating }}
              </div>
            </span>
          </article>
        </div>
          <div class="edit-review">
            <img src="@/assets/icons/edit-2-line.png" @click="openInput(review.reviewId)" 
            :style="{display: editingId === review.reviewId ? 'none' : 'block' }"
            style="width: 27px; margin-top: px;">
          </div>
      </div>
        <div class="content-icon" style="display: flex;">
          <p class="summary">
          <div v-if="editingId === review.reviewId">
            <textarea v-model="editingReview.reviewContent" class="textbox"></textarea>
          </div>
          <div v-else>{{ review.reviewContent }}</div>
        </p>
        <div v-if="editingId === review.reviewId">
          <img src="@/assets/icons/task-line.png" @click="saveReview(review.reviewId)" class="task-line">
        </div> 
        </div>
      </div>
    </section>
    </div>
</template>

<script>
import fullStarImage from "@/assets/icons/full_star.png";
import emptyStarImage from "@/assets/icons/empty_star.png";
import RemoveReview from "@/components/review/RemoveReview.vue";
import { onMounted, ref, toRaw } from "vue";
import axios from "axios";

export default {
  components : {
    RemoveReview,
  },
  setup() {
    //서버에서 받아온 리뷰 저장
    const serverReview = ref([]); 
    //브라우저에 출력할 리뷰의 값 
    const reviews = ref([]);
    // 어떤 리뷰가 수정 중인지 index 추적
    const editingId = ref(null); 
    // 수정할 값을 담기
    const editingReview = ref({
      reviewId : 1,
      userId : 1,
      isbn13: "",
      reviewContent: "",
      rating: 1,
    });

          // 서버에서 유저의 리뷰 조회
    const getMyList = async (userId) => {
      try{
        const response = await axios.get(`http://localhost:8081/api/review/myreview/${userId}`);
        if(response.status != 200){
          console.log("리뷰를 불러오는데 오류가 발생했습니다.");
          console.log(response.status);
          return ; 
        }
        serverReview.value = response.data;
        console.log("server data 값 : "+JSON.stringify(serverReview.value));
        // 각각의 배열에 showModal을 넣어서 개별적 실행 
        if (Array.isArray(serverReview.value)){
          reviews.value = serverReview.value.map(review => ({
            ...review,
            showModal : false,
          }))
          console.log("review data 값 : "+JSON.stringify(reviews.value));
        } else {
          console.error('serverPosts는 배열이 아닙니다.');
        };
      }catch(error){
        console.error(error, "에러 발생");
      }
    }

     // 수정하기 위해 데이터를 복사해서 창을 열기
    const openInput = (reviewId) => {
      console.log("수정하려는 값 : " + reviewId);
      editingId.value = reviewId;
      editingReview.value = { ...reviews.value.find(review => review.reviewId === reviewId) }; // 리뷰의 데이터를 복사하여 편집용 데이터로 설정

    };

     // 해당 reviewId의 인덱스를 찾음
    const findIndex= (reviewId) =>{ reviews.value.findIndex(review => review.reviewId === reviewId)};

    // 수정한 리뷰 저장
    const saveReview = (reviewId) => {
      const reviewIndex = findIndex(reviewId);
      if (reviewIndex !== -1) { //조건에 맞지 않으면 -1 반환 
        reviews.value[reviewIndex] = { ...editingReview.value }; // 해당 인덱스의 리뷰를 수정된 값으로 업데이트
        console.log("수정완료")
        editingId.value = null; // 수정 모드 종료
        updateReview(editingReview);
      } else {
        console.log("수정할 리뷰를 찾을 수 없습니다.");
      }
    };
    // 서버로 수정 처리 
    const raw =  toRaw(editingReview)
    const updateReview = async() => {
      try{
        const response = await axios.put("http://localhost:8081/api/review/update", raw);
        console.log("리뷰 수정 상태 : "+ response.status);
      }catch(error){
        console.error(error , "수정 처리 중 오류 발생 ");
      }
    }

    const deleteReview = (reviewId) => {
      console.log("삭제하려는 값 : "+ reviewId);
      const reviewIndex = findIndex(reviewId);  
      reviews.value.splice(reviewIndex, 1); // 리뷰 삭제
       };

    const ratingStar = (starIndex) => {  //별점 값 변경
      editingReview.value.rating = starIndex;
    };
  
    onMounted(() => {
      const userId = 3;  // 예시 사용자
      getMyList(userId);
    });
    return {
      serverReview,
      reviews,
      getMyList,
      fullStarImage,
      emptyStarImage,
      openInput,
      findIndex,
      saveReview,
      updateReview,
      deleteReview,
      editingId,
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
    max-width: 800px;
    margin: 100px auto;
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
  