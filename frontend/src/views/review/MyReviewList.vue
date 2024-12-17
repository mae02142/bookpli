<template>        

<!--     리뷰 리스트     -->
<div class="review-container" >
                <!-- 리뷰 없을 때 -->
    <section v-if="serverReview.value">
      <h1 class="none-review">등록된 리뷰가 없습니다.</h1>
    </section>
                <!-- 리뷰 있을 때 -->
    <section v-else 
      class="review-item" 
      v-for="review in reviews" 
      :key="review.reviewId"
    >
      <img class="review-image" :src="review.cover" :alt="review.title" />
      <div class="review-content">
        <div class="remove-review">
          <img src="@/assets/icons/close.png" @click="confirmRemove(review.reviewId)" class="removeIcon">
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
            <img src="@/assets/icons/edit-2-line.png" 
            @click="openInput(review.reviewId)" 
            :style="{display: editingId === review.reviewId ? 'none' : 'block' }"
            style="width: 27px; ">
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
          <img src="@/assets/icons/task-line.png" 
          @click="updateReview" class="task-line"
          >
        </div> 
        </div>
      </div>
    </section>
    </div>
   
</template>

<script>
import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
import fullStarImage from "@/assets/icons/full_star.png";
import emptyStarImage from "@/assets/icons/empty_star.png";
import RemoveReview from "@/components/review/RemoveReview.vue";
import { onMounted, ref, computed} from "vue";
import apiClient from '@/api/axiosInstance';
import { useAuthStore } from '@/stores/auth';
import { useConfirmModalStore } from '@/stores/utilModalStore';


export default {
  components : {
    RemoveReview,
  },
  setup() {
  /* user info */
  const authStore = useAuthStore();
  
  onMounted(() => {
    getMyList();
  });

          /* 리뷰 조회 페이지 */ 

    //서버에서 받아온 리뷰 저장
    const serverReview = ref([]); 
    //브라우저에 출력할 리뷰의 값 
    const reviews = ref([]);
    // 어떤 리뷰가 수정 중인지 id 추적
    const editingId = ref(null); 
    // 수정할 값을 담기
    const editingReview = ref({ });

            /*  리뷰  조회  */
    const getMyList = async () => {
      try{
        const response = await apiClient.get(`/api/review/myreview/${authStore.user.userId}`)
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
        } else {
          console.error('serverPosts는 배열이 아닙니다.');
        };
      }catch(error){
        console.error(error, "에러 발생");
      }
    }

            /*  리뷰  수정  */
    const openInput = (reviewId) => {  // 수정하기 위해 데이터를 복사해서 창을 열기
      console.log("수정하려는 값 : " + reviewId);
      editingId.value = reviewId;
      // 리뷰의 데이터를 복사하여 편집용 데이터로 설정
      editingReview.value = { ...reviews.value.find(review => 
        review.reviewId === reviewId) }; 
        console.log("editing review 에 복사: "+ JSON.stringify(editingReview.value));
    };
      //별점 변경
    const ratingStar = (starIndex) => {  
      editingReview.value.rating = starIndex;
    };

    // 서버로 수정 처리 
    const updateReview = async() => {
      try{
        const review =  {
        reviewId : editingId.value,
        reviewContent: editingReview.value.reviewContent,
        rating: editingReview.value.rating,
      };
      console.log(review);

       const response = await apiClient.put(`/api/review/update`, review);
         console.log("리뷰 수정 상태 : "+ response.status);
        
      const index = reviews.value.findIndex((item) => item.reviewId === review.reviewId);
      if(index !== -1){
        reviews.value[index] = {...reviews.value[index],...review};
      }
      editingId.value = null;
      }catch(error){
        console.error(error , "수정 처리 중 오류 발생 ");
      }
    };

     
           /*  리뷰  삭제  */ 

           // 삭제 모달
    const confirmRemove = (reviewId) => {
    const confirmModalStore = useConfirmModalStore();
    confirmModalStore.showModal(
    '리뷰 삭제',
    '선택하신 리뷰를 삭제하겠습니까?',
    '삭제 후 복원할 수 없습니다.',
    '삭제하기',
    '',
    () => deleteReview(reviewId)
    )
  };
          // 서버 삭제 요청
    const deleteReview = async(reviewId) => {
      console.log("삭제하려는 값 : "+ reviewId);
      try{
        const response = await apiClient.delete(`/api/review/delete/${reviewId}`)
        console.log(response.status);

        if(response.status=== 200){
          const checkUser = authStore.user.userId;
          getMyList(checkUser); // 삭제 후 새롭게 리뷰 목록을 가져옴
          close(); // 모달 닫기
        }
      }catch(error){
        console.error(error,"삭제 오류 발생");
      }
    };

    return {
      serverReview,
      reviews,
      getMyList,
      fullStarImage,
      emptyStarImage,
      openInput,
      updateReview,
      editingId,
      editingReview,
      ratingStar,
      deleteReview,
      confirmRemove,
    };
  },
};
</script>

  
  <style scoped>
  .container{
    display: flex;
    box-sizing: border-box;
    height: 100vh;
  }

/* 사이드 바 */
.library-container {
    display: flex;
    box-sizing: border-box;
    height: 100vh;
  }
  
  .sidebar2 {
    width: 250px;
    padding: 10px;
  }
  
  .sidebar-item {
    display: flex;
    align-items: center;
    padding: 20px 0px;
    margin-left: 20px;
    transition: background-color 0.3s ease;
  }

  .sidebar-item:hover {
  cursor: pointer;
  background-color: rgba(214, 214, 214, 0.6);
  border-radius: 5px;
  }
  
  .sidebar-icon {
    width: 30px;
    height: 30px;
    margin-right: 10px;
  }
  .menuitem-count {
    color: #383838;
    font-size: 14px;
  }

  /* no review */
  .none-review {
    font-size: 30px; 
    text-align: center; 
    color: #909090; 
    opacity: 50%; 
    margin : 100px;
  }

  /* 공통 스타일 */
  .review-container {
    max-width: 800px;
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
    font-size: 16px;
    font-weight: 500;
    color: #909090;
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
    margin: 15px 0 10px;
  }

  .task-line:hover {
    cursor: pointer;
    margin : 30px 10px;
  }
  
  .task-line {
    margin :30px 10px;
    
  }

  .textbox {
    resize: none;
    width: 400px;
    height: auto;
    font-size: 16px;
    border: none;
    background-color: #f1f1f1;
    outline: none;
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
  