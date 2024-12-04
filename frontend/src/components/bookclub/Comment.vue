<!-- CommentSection.vue -->
<template>
    <div v-if="showComments" class="comment-section">
      <div class="comment">
        <div class="comment-author">댓글작성자명</div>
        <div class="comment-content">
          크리스마스로 불리는 소년은 크리스마스를 떠올리기에 딱 좋았던 책인 것 같다.
          크리스마스를 좋아하는 내게 굉장히 흥미로웠고 영화를 먼저 봤었기 때문에 영화가 책의 내용을 잘 담아냈다고 생각한다.
          <br /><br />
          나처럼 크리스마스를 좋아하는 분들이 있다면 ‘크리스마스를 지켜라’ 라는 책을 추천하고 싶다.
        </div>
      </div>
  
      <div class="user-comment">
        <textarea v-model="userComment" placeholder="댓글을 입력하세요"></textarea>
        <button @click="postComment">게시</button>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from "vue";
  
  export default {
    name: "CommentSection",
    props: {
      showComments: Boolean
    },
    emits: ['update:showComments'],
    setup(props, { emit }) {
      const userComment = ref("");
  
      const postComment = () => {
        if (userComment.value.trim()) {
          console.log("댓글 작성:", userComment.value);
          userComment.value = ""; // 댓글 전송 후 입력 필드 초기화
        }
      };
  
      return {
        userComment,
        postComment
      };
    }
  };
  </script>
  
  <style scoped>
  .comment-section {
    padding: 20px;
    border-top: 1px solid #ccc;
  }
  
  .comment {
    margin-bottom: 15px;
  }
  
  .comment-author {
    font-weight: bold;
    color: #333;
  }
  
  .comment-content {
    color: #555;
    font-size: 14px;
    line-height: 1.6;
  }
  
  .user-comment {
    display: flex;
    flex-direction: column;
  }
  
  textarea {
    margin-bottom: 10px;
    padding: 10px;
    font-size: 14px;
    width: 100%;
    height: 80px;
    resize: none;
  }
  
  button {
    padding: 8px 15px;
    background-color: #4CAF50;
    color: white;
    border: none;
    cursor: pointer;
  }
  </style>
  