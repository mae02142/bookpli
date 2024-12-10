<template>
    <div v-if="showComments" class="comment-section">
    <header class="header">
        <h3>코멘트</h3>
    </header>

  
      <!-- 댓글 작성 -->
      <article class="user-comment">
        <div class="comment-form">
        <div class="user-comment-info">
            <img class="user-profile" src="@/assets/icons/profile.png" alt="댓글작성자">
            <p class="user-name">작성자명</p>
        </div>
        <textarea class="comment-box" v-model="userComment" @input="adjustHeight" rows =1 placeholder="댓글을 입력하세요"></textarea>
      </div>
        <button class="post-btn" @click="postComment">게시</button>
     </article> 
      

      <!-- 댓글 목록 -->
      <article v-for="(comment, index) in comments" :key="index" class="comment">
        <div class="user-comment-info">
            <img class="user-profile" src="@/assets/icons/profile.png" alt="댓글작성자">
            <p class="user-name">{{ comment.author }}</p>
        </div>
        <div class="comment-cnt">{{ comment.content }}</div>
        <div class="like-box">
            <img class="icon" :src="comment.likes.changeLike" @click="checkLike(index)" id="like-icon" alt="Like" />
            <p class="like-count">{{comment.likeCount}}</p>
        </div>
        <hr class="divider" />
      </article>
    </div>
  </template>
  
  <script>
import axios from "axios";
import { ref } from "vue";
import dislike from "@/assets/icons/dislike.png";
import like from "@/assets/icons/like.png";
  
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
    emits: ["update:showComments"],
    setup(props, { emit }) {
      const serverComment = ref([
        {
            author : "케빈",
            content : `댓글 입니다`,
        },
        {
            author : "홀리데이",
            content : `또 다른 댓글입니다.`,
        },
      ]);

      const comments = ref([]);
    // 서버에서 받아온 데이터에 추가적인 상태 값을 포함시킴
    if (Array.isArray(serverComment.value)) {
      comments.value = serverComment.value.map(comment => ({
            ...comment,
            likeCount: 0,
            likes: { changeLike: dislike },
        }));
    } else {
        console.error('serverComments는 배열이 아닙니다.');
    }

      const userComment = ref([]);
      // 댓글 추가 함수
      const postComment = () => {
        if (userComment.value.trim()) {
          const newComment = {
            author: "익명", // 추후 사용자 정보로 대체 가능
            content: userComment.value,
            likes : {changeLike : dislike},
            likeCount : 0,
          };
  
          comments.value.push(newComment);
          emit("commentAdded", newComment); // 부모 컴포넌트로 새로운 댓글 전달
          userComment.value = ""; // 입력 필드 초기화
        }
      };
      
      const adjustHeight = (event) => { //댓글 높이 조정
        const textarea= event.target;
        textarea.style.height = "auto";
        textarea.style.height = `${textarea.scrollHeight}px`;
      }
        // 좋아요 체크
      const checkLike = (index) => {
            let currentLike = comments.value[index];
            if(currentLike.likes.changeLike == dislike){
                currentLike.likes.changeLike = like;
                currentLike.likeCount +=1;
            }else{
                currentLike.likes.changeLike = dislike;
                currentLike.likeCount -=1;
            } 
        }; 
           //버튼도 각각 지정해야하기 때문에 배열로...
        const showBtn = ref([]);
        const dropdown = (index) => {
            showBtn.value[index] = !showBtn.value[index];
        }
      return {
        serverComment,
        comments,
        userComment,
        postComment,
        adjustHeight,
        dislike,
        like,
        checkLike,
        showBtn,
        dropdown
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
  