<template>
    <div class="community-post">
      <div class="community-header">
        <h2>커뮤니티</h2>
      </div>  
    <div class="community-items">
        <!-- 책 이미지 -->
        <img class="book-cover" :src="community.image" :alt="community.title" />
        <!-- 제목과 저자 -->
        <div class="content">
            <h1 class="title">{{ community.title }}</h1>
            <p class="author">{{ community.author }}</p>
        </div>
      </div>
        <RouterLink to="/bookclub/mybookclub" style="display: flex; justify-self:flex-end; width: auto;">
          <button class="Btn">
            <div class="svgWrapper">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 42 42"
                class="svgIcon"
              >
                <path
                  stroke-width="5"
                  stroke="#909090"
                  d="M9.14073 2.5H32.8593C33.3608 2.5 33.8291 2.75065 34.1073 3.16795L39.0801 10.6271C39.3539 11.0378 39.5 11.5203 39.5 12.0139V21V37C39.5 38.3807 38.3807 39.5 37 39.5H5C3.61929 39.5 2.5 38.3807 2.5 37V21V12.0139C2.5 11.5203 2.6461 11.0378 2.91987 10.6271L7.89266 3.16795C8.17086 2.75065 8.63921 2.5 9.14073 2.5Z"
                ></path>
                <rect
                  stroke-width="3"
                  stroke="#909090"
                  rx="2"
                  height="4"
                  width="11"
                  y="18.5"
                  x="15.5"
                ></rect>
                <path stroke-width="5" stroke="#909090" d="M1 12L41 12"></path>
              </svg>
              <div class="text">Archive</div>
            </div>
          </button>
        </RouterLink>
     
   
      <!-- 구분선 -->
      <hr class="divider" />
  
    <!-- 게시글 추가 -->
    <div class="add-post"  @click="addPost = true">
            <p class="description">책에 대한 이야기를 나눠보세요</p>
            <img class="add-icon" src="@/assets/icons/add.png" alt="추가 아이콘" />
    </div>
      <PostForm :modelValue="addPost" @update:modelValue="addPost = $event" />
  
      <!-- 내용 -->
     <section class="post-section">
        <div class="post-container">
        <article class="post" v-for="post,index in posts" :key="index">
            <div class="post-items">
                <div class="post-header">
                    <img class="post-profile" src="@/assets/icons/profile.png" alt="커뮤니티 이미지" />
                    <p class="username">{{post.user}}</p>
                </div>
                <div>
                    <p class="post-cnt">
                    {{ post.content }}
                    </p>
                      <!-- 아이콘 섹션 -->
                    <div class="icon-container">
                        <img class="icon" @click="openComment(index)" src="@/assets/icons/chat.png" alt="댓글 아이콘" />
                        <img class="like-icon" :src="post.likes.changeLike" @click="checkLike(index)" alt="좋아요 아이콘" />
                        <p class="like-count">{{post.likeCount}}</p>
                    </div> 
                </div>
            </div>   
            <Comment v-model:showComments="post.showComment" :postId="index" />
        <hr class="divider" />
        </article>
      </div>
     </section>  
    </div>
  </template>
  <script>
  import { ref } from "vue";
  import dislike from "@/assets/icons/dislike.png";
  import like from "@/assets/icons/like.png";
  import PostForm from "@/components/bookclub/PostForm.vue";
  import Comment from "@/components/bookclub/Comment.vue"
  export default {
    components : {
      PostForm,
      Comment,
    },  
    setup() {
      const community = ref({
        title: "크리스마스로 불리는 소년",
        author: "매트 헤이그",
        image: "boy-20.png",
      },
    );
    const posts = ref([
        { 
            user: "작성자1",
            content: `크리스마스로 불리는 소년은 크리스마스를 떠올리기에 딱 좋았던 책인 것 같다.
            크리스마스를 좋아하는 내게 굉장히 흥미로웠고 영화를 먼저 봤었기 때문에
            영화가 책의 내용을 잘 담아냈다고 생각한다.
    
            나처럼 크리스마스를 좋아하는 분들이 있다면 ‘크리스마스를 지켜라’ 라는 책을
            추천하고 싶다.`,
            likeCount: 0,
            likes: {changeLike: dislike},
            showComment : false,
        },
        {
            user: "작성자2",
            content: `영화도 재밌으니까 꼭 봐줬으면 해요!! 크리스마스처럼 따뜻한 분위기의
            영화였어요!!`,
            likeCount: 0,
            likes: {changeLike: dislike},
            showComment : false,
        },
    ]);
    const addPost = ref(false);
      // 부모에서 modelValue 값이 업데이트 될 때 호출되는 함수

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
    const openComment = (index) => {
      if(!posts.value[index].showComment){
        posts.value[index].showComment = true;
      }else{
        posts.value[index].showComment = false;
      }
    };

      return { 
        dislike,
        like,
        checkLike,
        community,
        posts,
        addPost,
        openComment,
       };
    },
  };
  </script>
  <style scoped>
  .community-post {
    width: 60%;
    margin: 0 auto;
    padding: 20px;
    font-family: "Inter", sans-serif;
  }
  .community-header {
    font-size: 25px;
    font-weight: 700;
    margin : 50px 0;
  }
  .community-items {
    display: flex;
    flex-direction: row;
    gap : 60px;
    padding : 30px;
  }
  .book-cover {
    width: 180px;
    height: 200px;
    object-fit: cover;
    border: 1px solid #909090;
  }
  
  .content {
    margin-top: 10px;
    line-height: 2;
  }
  
  .title {
    font-size: 24px;
    font-weight: bold;
  }
  
  .author {
    font-size: 18px;
    color: #666;
  }
  
  .divider {
    margin: 20px 0;
    border: none;
    border-top: 1px solid #ccc;
  }
    
  .post-container {
    margin-top: 20px;
  }
  
  .post-header {
    display: flex;
    align-items: center;
    flex-direction: column;

  }
  /* archive   */
.Btn {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 45px;
  height: 45px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition-duration: 0.3s;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.199);
  background-color: #FFFDF1;
}

/* plus sign */
.svgWrapper {
  width: 100%;
  transition-duration: 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.svgIcon {
  width: 17px;
}

/* text */
.text {
  position: absolute;
  right: 0%;
  width: 0%;
  opacity: 0;
  color: #909090;
  font-size: 1.2em;
  font-weight: 600;
  transition-duration: 0.3s;
}
/* hover effect on button width */
.Btn:hover {
  width: 125px;
  border-radius: 40px;
  transition-duration: 0.3s;
}

.Btn:hover .svgWrapper {
  width: 30%;
  transition-duration: 0.3s;
  padding-left: 5px;
}
/* hover effect button's text */
.Btn:hover .text {
  opacity: 1;
  width: 70%;
  transition-duration: 0.3s;
  padding-right: 6px;
}
/* button click effect*/
.Btn:active {
  transform: translate(2px, 2px);
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

    /* 게시글 부분 */
  .post-section {
    width: 70%;
    border : 1px solid #ccc;
    border-bottom: none;
    border-radius: 20px 20px 0 0;
    padding: 20px 50px;
    margin:auto;
  }
  .post-items{
    display: flex;
    flex-direction: row;
    gap: 40px;
  }

  .post{
    display: flex;
    flex-direction: column;
    gap: 20px;
    padding-top: 20px
  }
  .post-profile {
    width: 50px;
    height: 50px;
    margin-right: 10px;
    border-radius: 10px;
    margin-top: 10px;
  }
  .username {
    font-size: 13px;
    font-weight: 400;
    margin-top: 10px;
  }
  .post-cnt {
    margin: 10px 0;
    line-height: 1.6;
    border: none;
  }
  
  .icon-container {
    display: flex;
    justify-content: flex-start;
    gap: 30px;
    margin-top: 20px;
  }
  .icon-container:hover {
    cursor: pointer;
  }
  
  .add-icon {
    width: 15px;
    height: 15px;
    margin-bottom: 2px;
  }
  .icon {
    width: 18px;
    height: 18px;
  }
  .like-icon {
    width: 15px;
    height: 15px;
    margin-top: 2px;
  }
  .like-count {
    margin-top: 3px;
  }
  </style>
  