<template>
    <section class="post-content">
      <article class="post-article" v-for="item, index in posts" :key="item.postId">
          <div class="post-body">
          <div class="author-info">
              <img class="author-image" :src="item.profilePath || profile" alt="user profile" />
              <h3>{{item.userNickname || 'USER'}}</h3>
          </div>
          <div class="post-text"> 
              <p>{{item.postContent}}</p>
              <div class="post-footer">
                <div class="footer-icon">
                  <img class="icon" @click="item.showComment = true" src="@/assets/icons/chat.png" alt="Chat" />
                  <img class="icon" :src="item.likes.changeLike" @click="checkLike(item.postId,index)" id="like-icon" alt="Like" />
                  <p v-show="item.likeCount > 0" class="like-count">{{item.likeCount }}</p>
                </div>
                <p class="date">{{item.postDate.split('T')[0]}}</p>
              </div>
          </div>
          <div style="position: relative;">
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
          <Comment v-if:showComments="item.showComment" :postId="index" />
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
                };
              })
            );
          }
        } else {
            console.error('serverPosts는 배열이 아닙니다.');
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
        serverPosts,
        posts,
        getMyposts,
        checkLike,
        showBtn,
        dropdown,
        filterPost,
        getLikes,
      };
    },
  };
  </script>
  <style>
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

    .post-article{
      width: 100%;
    }

    .post-body {
      display: flex;
      margin-bottom: 20px;
      padding-top: 20px;
    }
    
    .author-image {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      margin-right: 5px;
    }
    
    .author-info {
      font-size: 15px;
      margin-right: 20px;
      display: flex;
      flex-direction: column;
    }
    .author-info h3 {
      text-align: center;
    }
    .post-text {
      font-size: 16px;
      line-height: 1.5;
      width: 100%;
    }
    
    .footer-icon{
      display: flex;
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
    #like-icon {
      width: 15px;
      height: 15px;
      margin-top: 2px;
    }
    
    .date {
      color: #909090;
      font-size: 14px;
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
  </style>