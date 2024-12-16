<template>
    <div class="review-container">
    <div class="review-header">
    </div>
                <!-- 리뷰 없을 때 -->
    <section v-if="!reviews || reviews.length === 0">
      <h1 class="none-review">등록된 리뷰가 없습니다.</h1>
    </section>
                <!-- 리뷰 있을 때 -->
    <section v-else 
      class="review-item" 
      v-for="review in reviews" 
      :key="review.reviewId || review.isbn13"
    >
    <div class="review-content">
        <div class="title-icon">
            <div class="userInfo">
                <img :src="review.profilePath || profile" class="user-img" alt="user profile">
                <p class="user-name">{{review.userNickname || 'USER'}}</p>
            </div>
            <article class="article">
                <div class="rating">
                    <div>
                        <img :src="fullStarImage" class="star">
                    </div>
                    <span class="rating-value">
                        <div>
                            {{ review.rating }}
                        </div>
                    </span>
                </div>
                    <div>{{review.reviewContent}}</div>
            </article>
        </div>
      </div>
    </section>
    </div>
</template>
<script setup>
import apiClient from '@/api/axiosInstance';
import { onMounted, ref, watch} from "vue";
import fullStarImage from "@/assets/icons/full_star.png";
import profile from "@/assets/icons/profile.png";


onMounted(()=>{
    console.log(props.isbn13);
    getList(props.isbn13);
})
 
const props = defineProps({
   isbn13 : {
    type : String,
    required : true,
   },
});

        /* 리뷰 리스트 조회 */

    //브라우저에 출력할 리뷰의 값 
const reviews = ref([]);

    // 리뷰 리스트 조회
const getList = async(isbn13) => {
    try{
        const response = await apiClient.get(`/api/review/book/${isbn13}`);
        reviews.value = response.data.data || [];
        if(response.status ==200){
            reviews.value=response.data.data;
        }
    }catch(error){
        console.error(error, '에러발생');
        reviews.value= [];
    }  
};

</script>
<style>
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
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    box-sizing: border-box; 
    background-color: #ffffff;
    justify-items: center;
    text-align: center; 

    display: flex;
    flex-direction: column;
    align-items: center; 
    text-align: center;
  }
  
  .review-header {
    text-align: left;
    margin-bottom: 20px;
    max-width: 1200px;
  }
  
  .review-conent{
    max-width: 1200px;
  }

  .review-header h2 {
    font-family: "Inter-Bold", sans-serif;
    font-size: 20px;
    font-weight: 500;
    color: #000000;
  }
  
  .review-item {
    width: 100%; 
    display: flex;
    gap: 80px;
    border-bottom: 1px solid #dcdcdc;
    padding: 20px 100px;
    box-sizing: border-box;
    justify-content: space-between; 
    align-items: center; 
  }

  .article{
    display: flex;
    flex-direction: column;
    gap: 8px; 
  }

  .rating .star {
    width: 24px;
    height: 24px;
  }
  .star:hover {
    cursor: pointer;
  }
  .rating {
    display: flex;
    align-items: center;
    gap: 5px;
    margin: 5px 0;
  }

  .rating-value {
    font-family: "Inter-Regular", sans-serif;
    font-size: 22px;
    font-weight: 400;
    color: #000000;
    margin: auto 10px;
  }

  .title-icon {
    display: flex;
    gap: 20px;
    max-width: 1200px;
  }

  .user-img{
    width: 40px;
    height: 40px;
  }

  .user-name{
    font-size: 13px;
  }

  .userInfo{
    display: flex;
    gap: 10px;
    flex-direction: column;

    align-items: center;
    margin-right: 20px; 
  }

</style>