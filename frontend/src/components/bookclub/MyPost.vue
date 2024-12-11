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
                  <img class="icon" :src="item.likes.changeLike" @click="checkLike(index)" id="like-icon" alt="Like" />
                  <p class="like-count">{{item.likeCount}}</p>
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
                  :deleteId="item.postId" @delete-post="getMyposts" />
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

        console.log(response.status);
        serverPosts.value = response.data.data;

      if (Array.isArray(serverPosts.value)) {
          posts.value = serverPosts.value.map(post => ({
              ...post,
              likeCount: 0,
              likes: { changeLike: dislike },
              deleteCheck: false,
              editCheck: false,
              showComment: false,
          }));
          console.log('최종 출력 값 :'+ JSON.stringify(posts.value));
      } else {
          console.error('serverPosts는 배열이 아닙니다.');
      }
    };
             // 좋아요 체크 
        const checkLike = (index) => {
            let currentLike = posts.value[index];
            if(currentLike.likes.changeLike == dislike){
                currentLike.likes.changeLike = like;
                currentLike.likeCount +=1;
            }else{
                currentLike.likes.changeLike = dislike;
                currentLike.likeCount -=1;
            }   
        };
  
        const showBtn = ref([]);
        const dropdown = (index) => {
            showBtn.value[index] = !showBtn.value[index];
        }
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