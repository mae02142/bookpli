<template>
  <section class="post-content">
    <article class="post-article" v-for="item, index in posts" :key="item.postId">
      <div class="post-body">   
             <!-- 게시글 내용 -->
        <div class="post-container"> 
          <div class="post-article">
                <!-- 이미지 슬라이드 컨테이너 -->
            <div class="post-nav"> 
              <div v-if="item.imageUrl.length >1" class="image-circle"> 
                <button class="slide-prev" @click="prevSlide(item)"><</button>
              </div>  
              <div class="post-images-wrapper" >
                <div
                  class="post-images"
                  :style="{ transform: `translateX(-${item.curpos * 100}%)` }"
                >
                  <!-- 각 이미지 하나씩 슬라이드 -->
                  <div
                    class="post-image"
                    v-for="(img, index) in item.imageUrl"
                    :key="index"
                  >
                    <img :src="img" class="post-image-img" />
                  </div>
                </div>
                     <!-- 슬라이드 네비게이션 -->
              </div>
              <div v-if="item.imageUrl.length >1" class="image-circle">
                    <button class="slide-next" @click="nextSlide(item)">></button>
              </div>
            </div>
          </div>
          <div class="text-box-post">
            <div class="author-info">
              <img class="author-image" :src="item.profilePath || profile" alt="user profile" />
              <h3>{{item.userNickname || 'USER'}}</h3>
            </div>
                  <!-- 본문 글 -->
            <p class="mypost-cnt">{{ item.postContent }} </p>                                 
          </div>
                      <!-- 아이콘 섹션 -->
                    <div class="post-footer">
                      <div class="footer-icon">
                        <img class="icon" 
                        @click="openComment(index)" 
                        src="@/assets/icons/chat.png"
                        alt="댓글 아이콘" />

                        <img class="like-icon" 
                        :src="item.likes.changeLike"
                        @click="checkLike(item.postId,index)" 
                        alt="좋아요 아이콘" />
                        <p v-show="item.likeCount > 0" class="like-count">{{item.likeCount }}</p>
                      </div>
                      <p class="date">{{item.postDate.split('T')[0]}}</p>
                    </div> 
                </div>
                <!-- 수정 삭제 드롭다운 -->
          <div class="dropdown-section">
              <img class="icon" @click="dropdown(index)" src="@/assets/icons/more.png" alt="More" />
              <div v-show="showBtn[index]" class="dropdown">
                  <button @click="item.editCheck= true" class="show-btn">수정</button>
                  <EditPost v-if="item.editCheck" 
                  :editId="item.postId" @edit-post="EditPost"
                   @close="getMyposts" />
                  <hr class="btn-line">
                  <button class="show-btn" @click="item.deleteCheck = true">삭제</button>
                  <!-- 삭제 컴포넌트 -->
                  <RemovePost v-if:isVisible="item.deleteCheck" 
                  :deleteId="item.postId" @delete-post="filterPost" />
              </div>
            </div> 
          </div>   
          <Comment v-if:showComments="item.showComment" :postId="item.postId" />
          <hr class="divider" /> 
        </article>
    </section>
  </template>
  <script se>
  import { onMounted, ref } from "vue"; 
  import dislike from "@/assets/icons/dislike.png";
  import like from "@/assets/icons/like.png";
  import profile from "@/assets/icons/profile.png";
  import RemovePost from "@/components/bookclub/RemovePost.vue";
  import PostForm from "@/components/bookclub/PostForm.vue";
  import EditPost from "@/components/bookclub/EditPost.vue";
  import Comment from "@/components/bookclub/Comment.vue";
  import { useAuthStore } from "@/stores/auth";
  import apiClient from '@/api/axiosInstance';

  export default {
    props: {
  userId: {
    type: Number,
    required: true,
  },
  bookclubId: {
    type: Number,
    required: true,
  },
  nickname: {
    type: String,
    required: true,
  },
  profile: {
    type: String,
    required: true,
  },
     },
    components: {
        RemovePost,
        PostForm,
        EditPost,
        Comment,
    },
    setup(props) {
    const authStore = useAuthStore();
      onMounted(()=>{
        console.log('club:'+props.bookclubId +'user:'+ props.userId);
        if(props.bookclubId && props.userId){
          getMyposts();
        }
      })

      const serverPosts = ref([]); //서버에서 받아온 데이터 
      const posts = ref([]);   // 서버에서 받아온 데이터에 추가적인 상태 값을 포함시킴

          // 나의 게시글 조회
      const getMyposts = async()=> {
        console.log('불러올 데이터의 유저 :' + props.userId);
        console.log('북클럽 : '+ props.bookclubId);

        const response = await apiClient.get(`/api/post/bookclub/mypost`, {
          params : {userId : props.userId, 
            bookClubId : props.bookclubId }
        });
        if(response.status == 200){
          serverPosts.value = response.data.data;
          if (Array.isArray(serverPosts.value)) {
            posts.value = await Promise.all(
              serverPosts.value.map(async (post) => {
                const likeCount = await getLikes(post.postId);  // getLikes 비동기 호출
                const heartCheck = await heartChecking(post.postId, authStore.user.userId);
                return {
                  ...post,
                  likeCount: likeCount || 0,
                  likes: { changeLike: heartCheck },
                  deleteCheck: false,
                  editCheck: false,
                  showComment: false,
                  curpos : 0,
                };
              })
            );
          }
          console.log(posts.value);
        } else {
            console.error('serverPosts는 배열이 아닙니다.');
        }
      };

       /* 이미지 슬라이더 처리 */

       const nextSlide = (item) => {
          if (item.curpos < item.imageUrl.length - 1) {
            item.curpos++;
          } else {
            item.curpos = 0; // 마지막 슬라이드 이후 첫 번째 슬라이드로
          }
        };

        const prevSlide = (item) => {
          if (item.curpos > 0) {
            item.curpos--;
          } else {
            item.curpos = item.imageUrl.length - 1; // 첫 번째 슬라이드 이전 마지막 슬라이드로
          }
        };

        // 좋아요 숫자 가져오기 
      const getLikes = async(postId)=>{
        try{
        const response = await apiClient.get(`/api/postlike/${postId}`);
        console.log('getlikes : '+ response.data)
        return response.data.data;
        }catch(error){
          console.error(error, "에러발생");
          return 0;
        }
      };
      
          // default 좋아요 체킹 
      const heartChecking = async(postId, userId)=>{
        console.log(typeof(postId));
      const response = await apiClient.get(`/api/postlike/checkingLike`, {
        params: {
          postId : postId ,
          userId : userId , 
        },
      });
      console.log(response.data.data);
      if(response.data.data){
        console.log('좋아요 처리');
        return like;
      }else{
        console.log('체킹되지 않았습니다.');
        return dislike;
      }
    };
      
             /* 좋아요 처리 기능 */
      const checkLike = async(postId,index) => { 
          const checking = {
            postId : postId,
            userId : authStore.user.userId,
          }
          console.log('postId :' + postId + 'userId : '+ authStore.user.userId);
          try{
            const response = await apiClient.post(`api/postlike/mylike` ,checking );
            console.log('checkLike :'+JSON.stringify(response.data));
            if(response.data.data !== undefined){
              posts.value[index].likes.changeLike = response.data.data  ? like : dislike;
              posts.value[index].likeCount += response.data.data ? 1 : -1;
            } else {
              console.error("좋아요 여부를 가져오지 못했습니다.");
            } 
          }catch(error) {
            console.error("API 호출 중 에러 발생: ", error);
            }
          };

          /*  댓글 오픈  */
      const openComment = (index) => {
        if(!posts.value[index].showComment){
          posts.value[index].showComment = true;
        }else{
          posts.value[index].showComment = false;
        }
      };

  
          /* 수정 삭제 관련 함수 */
        const showBtn = ref([]);
        const dropdown = (index) => {
            showBtn.value[index] = !showBtn.value[index];
        }

        const filterPost = (postId) =>{
          console.log('현재 posts:', posts.value); 
          posts.value = posts.value.filter(post => post.postId !== postId); 
          console.log('삭제 후 posts:', posts.value);
        };
      return {
        dislike,
        like,
        profile,
        nextSlide,
        prevSlide,
        serverPosts,
        posts,
        getMyposts,
        checkLike,
        openComment,
        showBtn,
        dropdown,
        filterPost,
        getLikes,
      };
    },
  };
  </script>
  <style scoped>
    /* 리스트 */
    .post-content {
      width: 80%;
      border : 1px solid #ccc;
      border-bottom: none;
      border-radius: 20px 20px 0 0;
      padding: 20px 50px;
      margin:auto;
      white-space: pre-wrap; /* 줄바꿈과 공백 유지 */
    }

    .post-body {
      display: flex;
      margin-bottom: 20px;
      padding-top: 20px;
    }
    
    .author-image {
      width: 40px;
      height: 40px;
      border-radius: 50%;
    }
    
    .author-info {
      font-size: 14px;
    margin-right: 20px;
    gap: 5px;
    display: flex;
    align-items: center;
    }

    .mypost-cnt {
      line-height: 1.6;
      border: none;
      white-space: pre-wrap;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .post-container{
      width: 100%;
    }

    .text-box-post{
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      margin-top: 10px;
    }
    
    /* 아이콘 섹션 */
    .footer-icon{
      display: flex;
      gap: 20px;
    }

    .post-footer {
      display: flex;
      justify-content: space-between;
      gap: 20px;
      margin-top: 20px;
    }
    
    .icon {
      width: 18px;
      height: 18px;
      margin : 0 10px;
    }
    .icon:hover {
      cursor: pointer;
    }
    .like-icon {
      width: 15px;
      height: 15px;
      margin-top: 2px;
    }
    
    .date {
      color: #909090;
      font-size: 14px;
    }

    .divider {
    margin: 20px 0;
    border: none; 
    border-top: 1px solid #ccc;
  }

    /* 수정 삭제 드롭다운  */

    .dropdown-section {
     position: relative;
    }

    .dropdown {
    position: absolute;
    top: 0; /* 이미지 버튼 바로 아래에 위치 */
    left: 80%;
    background-color: white;
    border: 1px solid #ccc;
    border-radius: 4px;
    padding: 5px 0;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    z-index: 10;
    width: max-content; /* 버튼 크기에 맞춰 크기 조정 */
  }
  
  .show-btn {
    display: block;
    width: 100%;
    padding: 10px;
    border: none;
    background: none;
    text-align: left;
  }
    .btn-line {
      margin : 0;
      border: 0;
      border-top: 1px solid #ccc;
      height: 1px;
    }
    .show-btn:hover{
      cursor: pointer;
    }

      /* post-image 부분 */

      .post-images-wrapper {
      position: relative;
      width: 100%;
      max-width: 300px;
      margin : 0 auto;
      overflow: hidden;
    }

    .post-article{
      width: 100%;
    }

    .post-nav {
      display: flex;
      flex-direction: row;
    }
    .post-images{
      display: flex;
      transition: transform 0.3s ease-in-out;
      width: 100%;
    }

    .post-image{
      flex : 0 0 100%;
      max-width: 100%;
      box-sizing: border-box;
    }

    .post-image-img{
      width: 100%;
      height: 300px;
      object-fit: cover;

    }    
    .image-circle {
    display: flex;
    justify-content: space-between;

    }   
    .slide-prev{
      background-color: transparent;
      border: none;
      cursor: pointer;
      font-size: 40px;
      color: #ccc
    }
    .slide-next {
      background-color: transparent;
      border: none;
      cursor: pointer;
      font-size: 40px;
      color: #ccc
    }

    
  </style>