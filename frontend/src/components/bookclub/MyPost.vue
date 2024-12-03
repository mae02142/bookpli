<template>
    <div class="community-post">
      <div class="community-label">커뮤니티</div>
      <header class="header">
        <img class="profile-image" src="@/assets/icons/profile.png" alt="Profile" />
        <div class="title-icon">
            <div class="post-header">
                <div class="post-title">{{ title }}</div>
                <RouterLink to="/bookclub/community">
                    <img class="home-icon" src="@/assets/icons/home.png" alt="goto community" />
                </RouterLink>
            </div>
            <div class="title">{{post[0].author}} 님의 글</div>
        </div>
      </header>
      <hr class="divider" />
          <!-- 게시글 추가 -->
      <div class="add-post"  @click="addPost = true">
            <p class="description">책에 대한 이야기를 나눠보세요</p>
            <img class="add-icon" src="@/assets/icons/add.png" alt="추가 아이콘" />
        <PostForm v-model="addPost" />
      </div>
             <!-- 게시글 리스트 -->
      <section class="post-content">
        <article class="post-article" v-for="item, index in post" :key="index">
            <div class="post-body">
            <div class="author-info">
                <img class="author-image" src="@/assets/icons/profile.png" alt="Author" />
                <h3>{{item.author}}</h3>
            </div>
            <div class="post-text"> 
                <p>{{item.content}}</p>
                <div class="post-footer">
                    <img class="icon" src="@/assets/icons/chat.png" alt="Chat" />
                    <img class="icon" :src="item.likes.changeLike" @click="checkLike(index)" id="like-icon" alt="Like" />
                    <p class="like-count">{{item.likeCount}}</p>
                </div>
            </div>
            <div style="position: relative;">
                <img class="icon" @click="dropdown(index)" src="@/assets/icons/more.png" alt="More" />
                <div v-show="showBtn[index]" class="dropdown">
                    <button class="show-btn">수정</button>
                    <hr class="btn-line">
                    <button class="show-btn" @click="item.deleteCheck = true">삭제</button>
                    <!-- 삭제 컴포넌트 -->
                    <RemovePost v-model:isVisible="item.deleteCheck" :deleteId="index" @delete-post="deletePost" />
                </div>
            </div> 
            </div>   
          <hr class="divider" /> 
        </article>
      </section>
    </div>
  </template>
  
  <script>
  import { ref, watchEffect } from "vue";
  import dislike from "@/assets/icons/dislike.png";
  import like from "@/assets/icons/like.png";
  import RemovePost from "./RemovePost.vue";
  import PostForm from "./PostForm.vue";

  
  export default {
    components: {
        RemovePost,
        PostForm,
    },
    setup() {
      const title = ref("크리스마스로 불리는 소년");  
      const post = ref([
        {
        author: "작성자명",
        content: `크리스마스로 불리는 소년은 크리스마스를 떠올리기에 딱 좋았던 책인 것 같다.
          크리스마스를 좋아하는 내게 굉장히 흥미로웠고 영화를 먼저 봤었기 때문에
          영화가 책의 내용을 잘 담아냈다고 생각한다.`,
        likeCount: 0,
        likes: {changeLike: dislike},
        deleteCheck : false,
      },
      {
        author: "작성자명",
        content: `
          나처럼 크리스마스를 좋아하는 분들이 있다면 ‘크리스마스를 지켜라’ 라는 책을 추천하고 싶다.`,
        likeCount: 0,
        likes: {changeLike: dislike},
        deleteCheck : false,
      },
    ]);
        const checkLike = (index) => {
            let currentLike = post.value[index];
            if(currentLike.likes.changeLike == dislike){
                currentLike.likes.changeLike = like;
                currentLike.likeCount +=1;
            }else{
                currentLike.likes.changeLike = dislike;
                currentLike.likeCount -=1;
            }   
        };
        const addPost = ref(false);
        //버튼도 각각 지정해야하기 때문에 배열로...
        const showBtn = ref([]);
        const dropdown = (index) => {
            showBtn.value[index] = !showBtn.value[index];
        }
       
        const deletePost = (index) => {
          console.log("삭제하려는 게시글 : "+ index);
            post.value.splice(index, 1); // 리뷰 삭제
       };
      return {
        dislike,
        like,
        title,
        post,
        checkLike,
        addPost,
        showBtn,
        dropdown,
        deletePost,
      };
    },
  };
  </script>
  
  <style scoped>
  .community-post {
    box-sizing: border-box;
    width: 60%;
    margin: 0 auto;
    padding: 50px 20px 0 20px;
    font-family: "Inter", sans-serif;
  }
  
    /*  헤 더  */
  .header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin: 50px 0;
    gap: 30px;
  }
  
  .profile-image {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    margin: 0 20px;
  }
  .title-icon {
    display: flex;
    flex-direction: column;;
  }
  .title {
    font-size: 24px;
    font-weight: 600;
    margin-left: 20px;
  }
  
  .community-label {
    font-size: 25px;
    font-weight: 700;
    margin : 50px 0;
  }
  
  .divider {
    border: none;
    height: 2px;
    background-color: #ddd;
    margin-bottom: 20px;
  }
  
  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .post-title {
    font-size: 20px;
    font-weight: 400;
  }
  
  .home-icon {
    width: 24px;
    height: 24px;
    margin-left: 10px;
  }
  .home-icon:hover {
    cursor: pointer;
  }
  /* 게시글 등록 */

  .add-post {
    display: flex;
    flex-direction: row;
    gap:20px;
    padding: 10px;
    align-items: end;
    justify-content: end;
    margin-bottom: 20px;
  }
  .add-post:hover {
    cursor: pointer;
  }
  
  .description {
    font-size: 15px;
    color : #909090;
  }

    /* 리스트 */
  .post-content {
    width: 60%;
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
    margin-right: 10px;
  }
  
  .author-info {
    font-size: 15px;
    margin-right: 20px;
    display: flex;
    flex-direction: column;
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
  