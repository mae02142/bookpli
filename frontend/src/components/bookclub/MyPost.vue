<template>
    <section class="post-content">
      <article class="post-article" v-for="item, index in posts" :key="index">
          <div class="post-body">
          <div class="author-info">
              <img class="author-image" src="@/assets/icons/profile.png" alt="Author" />
              <h3>{{item.author}}</h3>
          </div>
          <div class="post-text"> 
              <p>{{item.content}}</p>
              <div class="post-footer">
                  <img class="icon" @click="item.showComment = true" src="@/assets/icons/chat.png" alt="Chat" />
                  <img class="icon" :src="item.likes.changeLike" @click="checkLike(index)" id="like-icon" alt="Like" />
                  <p class="like-count">{{item.likeCount}}</p>
              </div>
          </div>
          <div style="position: relative;">
              <img class="icon" @click="dropdown(index)" src="@/assets/icons/more.png" alt="More" />
              <div v-show="showBtn[index]" class="dropdown">
                  <button @click="item.editCheck= true" class="show-btn">수정</button>
                  <EditPost v-model="item.editCheck" :editId="index" @edit-post="EditPost" />
                  <hr class="btn-line">
                  <button class="show-btn" @click="item.deleteCheck = true">삭제</button>
                  <!-- 삭제 컴포넌트 -->
                  <RemovePost v-model:isVisible="item.deleteCheck" :deleteId="index" @delete-post="deletePost" />
              </div>
          </div> 
          </div>   
          <Comment v-model:showComments="item.showComment" :postId="index" />
        <hr class="divider" /> 
      </article>
    </section>
  </template>
  <script>
  import { ref } from "vue"; 
  import dislike from "@/assets/icons/dislike.png";
  import like from "@/assets/icons/like.png";
  import RemovePost from "@/components/bookclub/RemovePost.vue";
  import PostForm from "@/components/bookclub/PostForm.vue";
  import EditPost from "@/components/bookclub/EditPost.vue";
  import Comment from "@/components/bookclub/Comment.vue";
  
  
  export default {
     props: {
      userInfo : {
          type : Object,
          required : true,
      },
     },
    components: {
        RemovePost,
        PostForm,
        EditPost,
        Comment,
    },
    setup() {
    
      const serverPosts = ref([ //서버에서 받아온 데이터
          {  
          author: "트리",
          profile: `@/assets/icons/profile.png`,
          postDate: `2024-11-27`,
          content: `크리스마스로 불리는 소년은 크리스마스를 떠올리기에 딱 좋았던 책인 것 같다.
          크리스마스를 좋아하는 내게 굉장히 흥미로웠고 영화를 먼저 봤었기 때문에
          영화가 책의 내용을 잘 담아냈다고 생각한다.`,
          },
          {
          author: "트리",
          profile: `@/assets/icons/profile.png`,
          postDate: `2024-11-29`,
          content: `
          나처럼 크리스마스를 좋아하는 분들이 있다면 ‘크리스마스를 지켜라’ 라는 책을 추천하고 싶다.`,
          },
      ]);
      const posts = ref([]);
      // 서버에서 받아온 데이터에 추가적인 상태 값을 포함시킴
      if (Array.isArray(serverPosts.value)) {
          posts.value = serverPosts.value.map(post => ({
              ...post,
              likeCount: 0,
              likes: { changeLike: dislike },
              deleteCheck: false,
              editCheck: false,
              showComment: false,
          }));
      } else {
          console.error('serverPosts는 배열이 아닙니다.');
      }
  
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
       
        const deletePost = (index) => {
          console.log("삭제하려는 게시글 : "+ index);
            posts.value.splice(index, 1); // 리뷰 삭제
       };
      return {
        dislike,
        like,
        serverPosts,
        posts,
        checkLike,
        showBtn,
        dropdown,
        deletePost,
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
    }
    
    .post-footer {
      display: flex;
      justify-content: flex-start;
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