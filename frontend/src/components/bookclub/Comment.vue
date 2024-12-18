<template>
    <div class="comment-section">
    <header class="header">
        <h3>코멘트</h3>
    </header>

<section> 
      <!-- 댓글 작성 -->
      <article class="user-comment">
        <div class="comment-form">
        <div class="user-comment-info">
            <img class="user-profile" src="@/assets/icons/profile.png" alt="댓글작성자">
            <p class="user-name">{{userName}}</p>
        </div>
        <textarea class="comment-box" v-model="userComment" @input="adjustHeight" rows =1 placeholder="댓글을 입력하세요"></textarea>
      </div>
        <button class="post-btn" @click="postComment">게시</button>
     </article> 
      

      <!-- 댓글 목록 -->
      <article v-show="isExisting"  v-for="(comment, index) in comments" :key="comment.commentId" class="comment">
        <div class="user-comment-info">
            <img class="user-profile" src="@/assets/icons/profile.png" alt="댓글작성자">
            <p class="user-name">{{ comment.userNickname || 'USER' }}</p>
        </div>
        <div class="comment-cnt">{{ comment.commentContent }}</div>
        <div class="like-box">
            <img class="icon" :src="comment.likes.changeLike" 
            @click="checkLike(comment.commentId,index)" 
            id="like-icon" alt="Like" />
            <p class="like-count">{{comment.likeCount}}</p>
        </div>
        <hr class="divider" />
      </article>
    </section> 
    </div>
  </template>
  
  <script>
import apiClient from "@/api/axiosInstance";
import { onMounted, ref, computed } from "vue";
import dislike from "@/assets/icons/dislike.png";
import like from "@/assets/icons/like.png";
import { useAuthStore } from "@/stores/auth";
import { useUtilModalStore } from "@/stores/utilModalStore";
  
  export default {
 
    props: {
      showComments: {
        type : Boolean,
        default : false,
      },
      postId : {
        type : Number,
        required : true,
      }
      },
    emits: ['update:commentCount'],
  
    setup(props, { emit }) {

      const authStore = useAuthStore();

      onMounted(()=>{
        if(props.postId){
          getComments();
        }
      });
      
      const isExisting = ref(false);   // 댓글 목록 
      const serverComment = ref([]);   // 서버에서 받아온 데이터
      const comments = ref([]);    // 서버에서 받아온 데이터에 추가적인 상태 값을 포함시킴

        // 등록된 댓글 가져오기 
      const getComments = async() => {
        const response = await apiClient.get(`/api/comment/post/${props.postId}`)
        if(response.data.data && response.data.data.length > 0){
          serverComment.value = response.data.data;
          isExisting.value = true;
        }else{
          serverComment.value = [];
          isExisting.value = false;
        }
        if (Array.isArray(serverComment.value)) {
          comments.value = await Promise.all(
             serverComment.value.map(async(comment) => {
              const getcount =await getLikes(comment.commentId);
              const heartCheck =await heartChecking(comment.commentId,authStore.user.userId);
              return {
                ...comment,
                likeCount:getcount ||0,
                likes: {changeLike :heartCheck},
              }
            })
          )
          emit('update:commentCount', comments.value.length);
        } else {
            console.error('serverComments는 배열이 아닙니다.');
        }
      };  

          // 좋아요 default 값 설정
      const heartChecking = async(commentId,userId) => {
        const response = await apiClient.get(`/api/commentlike/checking`,{
          params:{
            commentId : commentId,
          userId : userId
          }
        });
        if(response.data.data){  
          return like;
        }else{
          return dislike;
        }
      }

      const getLikes = async(commentId) => {
        const response = await apiClient.get(`/api/commentlike/${commentId}`);
        try{
        if(response.status == 200){
          return response.data.data;
        }
        }catch(error){
          console.error('에러 발생 : '+error);
        }
      }
          /*  댓글 등록  */
      const userComment = ref('');
      const userName = authStore.user.userNickname || 'USER'; // 댓글 작성 시 작성자명
      const utilModalStore = useUtilModalStore();

      const postComment =async() => {
        
          const newComment = {
            userId : authStore.user.userId, 
            commentContent : userComment.value,
            postId : props.postId,
          };
        try{
          const response = await apiClient.post(`/api/comment/insert`,newComment);
          if(response.status ==200){
            utilModalStore.showModal(
              '댓글 등록',
              '댓글이 등록되었습니다.',
              'alert'
            );
            userComment.value = "";
          }
        }catch(error){
          console.error('오류 발생', error)
        }
        await getComments();

      };

      const adjustHeight = (event) => { //댓글 높이 조정
        const textarea= event.target;
        textarea.style.height = "auto";
        textarea.style.height = `${textarea.scrollHeight}px`;
      }
        // 좋아요 체크
      const checkLike = async(commentId,index) => {
        const checking = {
          userId : authStore.user.userId,
          commentId : commentId,
        };
        try{
          const response = await apiClient.post(`/api/commentlike/mylike`,checking);

          if(response.data.data!==undefined){
          // 현재 상태를 확인하고 적절히 처리
            const currentLikeStatus = comments.value[index].likes.changeLike; // 현재 상태
            const newLikeStatus = response.data.data ? like : dislike; // 새 상태

            // 상태 변경
            comments.value[index].likes.changeLike = newLikeStatus;
            
            // 좋아요 수 업데이트 (현재 상태와 새 상태 비교)
            if (currentLikeStatus === like && newLikeStatus === dislike) {
                    comments.value[index].likeCount -= 1; // 좋아요 취소
                  } else if (currentLikeStatus === dislike && newLikeStatus === like) {
                    comments.value[index].likeCount += 1; // 좋아요 추가
            }
          }
        }catch(error){
          console.error(error+'오류 발생');
        }
        }; 
           //버튼도 각각 지정해야하기 때문에 배열로...
        const showBtn = ref([]);
        const dropdown = (index) => {
            showBtn.value[index] = !showBtn.value[index];
        }


      return {
        isExisting,
        serverComment,
        getComments,
        comments,
        userComment,
        postComment,
        adjustHeight,
        dislike,
        like,
        checkLike,
        showBtn,
        dropdown,
        userName,
  
      };
    },
  };
  </script>
  
  <style scoped>
  .comment-section {
    padding: 20px;
    border-top: 1px solid #ccc;
  }
  .header {
    margin : 10px 0;
  }
  
  .comment {
    padding: 10px;
  }
  
  .comment-author {
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .comment-cnt {
    color: #555;
    font-size: 13px;
    line-height: 3;
    margin-bottom: 2;
  }
  
  .divider {
    margin: 10px 0;
    border: none;
    border-top: 1px solid #ccc;
  }
    
  /* 댓글 작성  */
  .user-comment {
    display: flex;
    flex-direction: row;
    border-radius: 7px;
    border : 1px solid #cdcdcd;
    width: 100%;
    height: auto;
    padding: 10px;
  }
  .comment-form{
    display: flex;
    flex-direction: column;
    width: 90%;
  }
  .user-comment-info{
    width: auto;
    height: auto;
    margin : 5px;
    flex-direction: row;
    display: flex;
  }
  .user-profile {
    width: 30px;
  }
  .user-name {
    font-size: 11px;
    margin-top: auto;
    margin-bottom: auto;
  }
  
  .comment-box {
    padding: 10px;
    font-size: 13px;
    resize: none;
    border: none;
    height: 100%;
    outline: none;
    overflow: hidden;
    transition: height 0.2s ease;
    white-space: pre-wrap; /* 줄바꿈과 공백 유지 */
  }

  .post-btn {
    width: 45px;
    height: 30px;
    background-color: #FFFDF1;
    border: none;
    cursor: pointer;
    border-radius: 5px;
    margin-top: auto;
  }
  .post-btn:hover {
    cursor: pointer;
    background-color: beige;
  }
  /* 좋아요  */
  .like-box{
    display: flex;
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
    width: 13px;
    height: 13px;
  }
  </style>
  