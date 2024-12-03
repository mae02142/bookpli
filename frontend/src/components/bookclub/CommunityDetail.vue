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
        <RouterLink to="/bookclub/mypost" style="margin-top: auto; margin-left: auto;">
          <img class="user-archive" src="@/assets/icons/archive.png" alt="작성자글목록" />
        </RouterLink>
      </div>
   
      <!-- 구분선 -->
      <hr class="divider" />
  
      <!-- 게시글 추가 -->
      <div class="add-post">
        <p class="description">책에 대한 이야기를 나눠보세요</p>
        <img class="add-icon" src="@/assets/icons/add.png" alt="추가 아이콘" />
      </div>
  
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
                    <p class="post-content">
                    {{ post.content }}
                    </p>
                      <!-- 아이콘 섹션 -->
                    <div class="icon-container">
                        <img class="icon" src="@/assets/icons/chat.png" alt="댓글 아이콘" />
                        <img class="like-icon" :src="post.likes.changeLike" @click="checkLike(index)" alt="좋아요 아이콘" />
                        <p class="like-count">{{post.likeCount}}</p>
                    </div> 
                </div>
            </div>   
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
  export default {
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
        },
        {
            user: "작성자2",
            content: `영화도 재밌으니까 꼭 봐줬으면 해요!! 크리스마스처럼 따뜻한 분위기의
            영화였어요!!`,
            likeCount: 0,
            likes: {changeLike: dislike},
        },
    ]);
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
  
      return { 
        dislike,
        like,
        checkLike,
        community,
        posts,
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
  
  .add-post {
    display: flex;
    flex-direction: row;
    gap:20px;
    padding: 10px;
    align-items: end;
    justify-content: end;
    margin-bottom: 20px;
  }
  
  .description {
    font-size: 15px;
    color : #909090;
  }
  
  .post-container {
    margin-top: 20px;
  }
  
  .post-header {
    display: flex;
    align-items: center;
    flex-direction: column;

  }
  
  .user-archive {
    width: 30px;
    height: 30px;
    margin-right: 10px;
    margin-left: auto;
  }
  .user-archive:hover {
    cursor: pointer;

  }
    /* 게시글 부분 */
  .post-section {
    width: 60%;
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
  .post-content {
    margin: 10px 0;
    line-height: 1.6;
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
  