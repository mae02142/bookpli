<template>
    <div class="community-post" >
      <div class="community-header">
        <h2>커뮤니티</h2>
      </div>  
    <div class="community-items">
        <!-- 책 이미지 -->
         <div class="cover-one">
          <div class="cover-two">
            <div class="cover-three">
              <div class="cover-four">
                <img class="book-cover" :src="community.image" :alt="community.title" />
              </div>
            </div>
          </div>
         </div>
       
        <!-- 제목과 저자 -->
        <div class="content">
            <h1 class="title">{{ community.title.replace(/\(.*?\)/g, '').trim() }}</h1>
            <p class="author">{{ community.author.replace(/\(.*?\)/g, '').trim() }}</p>
            <p class="description">{{ community.description }}</p>
        </div>
      </div>
      <RouterLink :to="{path :'/bookclub/mybookclub' , 
        query : { title : community.title, bookClubId : community.bookClubId, 
        author : community.author, cover: community.image, description: community.description}}"
        class="link"
        >
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
      <PostForm :modelValue="addPost"
       @update:modelValue="addPost = $event" 
       :userId="authStore.user.userId" 
       :bookclubId="community.bookClubId" 
       @close="getPosts"
       />
  
      <!-- 내용 -->
     <section class="post-section">
        <div class="post-content">
        <article class="post" v-for="post,index in posts" :key="post.postId">
            <div class="post-items">
                    <!-- 게시글 내용 -->
              <div class="post-container"> 
                <div class="post-article">
                      <!-- 이미지 슬라이드 컨테이너 -->
                  <div class="post-nav">    
                    <div v-if="post.imageUrl.length >1" class="image-circle"> 
                      <button class="slide-prev" @click="prevSlide(post)"><</button>
                    </div>  
                      <div class="post-images-wrapper" >
                        <div
                          class="post-images"
                          :style="{ transform: `translateX(-${post.curpos * 100}%)` }"
                          >
                        <!-- 각 이미지 하나씩 슬라이드 -->
                          <div
                            class="post-image"
                            v-for="img in post.imageUrl"
                            :key="img.imageId"
                          >
                            <img :src="img.imageUrl" class="post-image-img" />
                          </div>
                        </div>
                           <!-- 슬라이드 네비게이션 -->
                      </div>
                    <div v-if="post.imageUrl.length >1" class="image-circle">
                        <button class="slide-next" @click="nextSlide(post)">></button>
                    </div>
                </div>
                <!--  유저 정보와 텍스트 -->
                <div class="text-box-bookclub">
                  <div class="post-header">
                    <img class="post-profile" :src="post.profilePath || profile" alt="커뮤니티 이미지" />
                    <p class="username">{{post.userNickname == null ? 'USER' : post.userNickname}}</p>
                </div>
                <!-- 본문 글 -->
                <p class="post-cnt">{{ post.postContent }} </p>                        
                </div>
                 
                      <!-- 아이콘 섹션 -->
                    <div class="post-footer">
                      <div class="footer-icon">
                        <img class="icon"  
                         @click="openComment(index)" 
                        src="@/assets/icons/chat.png"
                        alt="댓글 아이콘" />

                        <img class="like-icon" 
                        :src="post.likes.changeLike" 
                         @click="checkLike(post.postId,index)" 
                        alt="좋아요 아이콘" />
                        <p v-show="post.likeCount > 0" class="like-count">{{post.likeCount }}</p>
                      </div>
                      <p class="date">{{post.postDate.split('T')[0]}}</p>
                    </div> 
                </div>
              </div>
            </div>   
            <Comment v-if:showComments="post.showComment" :postId="post.postId" />
        <hr class="divider" />
        </article>
      </div>
     </section>  
    </div>
  </template>
  <script>
  import { onMounted, ref } from "vue";
  import dislike from "@/assets/icons/dislike.png";
  import like from "@/assets/icons/like.png";
  import profile from "@/assets/icons/profile.png";
  import PostForm from "@/components/bookclub/PostForm.vue";
  import Comment from "@/components/bookclub/Comment.vue"
  import { useRoute } from "vue-router";
  import { useAuthStore } from '@/stores/auth';
  import apiClient from '@/api/axiosInstance';
  

  export default {
    components : {
      PostForm,
      Comment,
    },  
    setup() {
      const authStore = useAuthStore();
      const route = useRoute(); // 현재 라우트 정보
    
      onMounted(()=>{
        console.log('북클럽 상세 :' + JSON.stringify(route.query));
        getPosts();
      });

      const community = ref({
        title: route.query.title,
        author: route.query.author,
        image: route.query.cover,
        bookClubId : Number(route.query.bookClubId),
        description : route.query.description,
      },
    );
    const serverPosts = ref([]); // 서버
    const posts = ref([]); // 서버 + 추가된 데이터 
    const addPost = ref(false);
    // 부모에서 modelValue 값이 업데이트 될 때 호출되는 함수
  
          /* 게시글 조회  */
    const getPosts = async() =>{
      console.log("bookclubId :"+ route.query.bookClubId);
    try{
      const response = await apiClient.get("/api/post/bookclubs", {
        params: {bookclubId : route.query.bookClubId},
      });
      console.log(response.data);
      if(response.status == 200){
        serverPosts.value = response.data.data;
        if (Array.isArray(serverPosts.value)) {
        // 각 게시글에 대해 좋아요 수를 가져오고 추가
        posts.value = await Promise.all(
          serverPosts.value.map(async (post) => {
            const likeCount = await getLikes(post.postId);  // getLikes 비동기 호출
            const heartCheck = await heartChecking(post.postId, authStore.user.userId);
            return {
              ...post,
              likeCount: likeCount || 0,
              likes: { changeLike: heartCheck },
              showComment: false,
              curpos : 0,
            };
          })
        );
        console.log(posts.value);
        };
      }
    }catch(error){
      console.error(error, '에러 발생!');
    }};

        /* 이미지 슬라이더 처리 */

        const nextSlide = (post) => {
          if (post.curpos < post.imageUrl.length - 1) {
            post.curpos++;
          } else {
            post.curpos = 0; // 마지막 슬라이드 이후 첫 번째 슬라이드로
          }
        };

        const prevSlide = (post) => {
          if (post.curpos > 0) {
            post.curpos--;
          } else {
            post.curpos = post.imageUrl.length - 1; // 첫 번째 슬라이드 이전 마지막 슬라이드로
          }
        };

            /* 좋아요 수 조회 */
    const getLikes = async(postId)=> {
      try{
        const response = await apiClient.get(`/api/postlike/${postId}`);
        console.log('getlikes : '+ response.data)
        return response.data.data;
      }catch(error){
        console.error(error, "에러발생");
        return 0;
      }
    }
        /* default 좋아요 처리 */
    const heartChecking = async(postId,userId) =>{
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
    }

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
       
              /* 댓글 오픈 */
    const openComment = (index) => {
      if(!posts.value[index].showComment){
        posts.value[index].showComment = true;
      }else{
        posts.value[index].showComment = false;
      }
    };

  
      return { 
        authStore,
        profile,
        getPosts,
        dislike,
        like,
        checkLike,
        community,
        posts,
        addPost,
        openComment,
        nextSlide,
        prevSlide,
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
    border: 1px double #c0c0c0;
    margin-right: 1px;
    border-radius: 4px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2); /* 그림자 추가 */
    transform-style: preserve-3d; /* 3D 변환 활성화 */
    transition: transform 0.5s ease;
  }

  .cover-one{
  border: 1px double gainsboro;
  border-left: none;
  border-top: none;
  border-radius: 4px;
  margin: 10px;
}
.cover-two{
  border: 1px double gainsboro;
  border-left: none;
  border-top: none;
  border-radius: 4px;
  margin-right: 1px;
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
}
.cover-three{
  border: 1px double gainsboro;
  border-left: none;
  border-top: none;
  border-radius: 4px;
  margin-right: 1px;
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
}
.cover-four{
  border: 1px double gainsboro;
  border-left: none;
  border-top: none;
  border-radius: 4px;
  margin-right: 1px;
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
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
    width: 100%;
  }
  
  .post-header {
    display: flex;
    align-items: center;
    flex-direction: column;
    margin-right: 10px;

  }
  /* archive   */
  .link {
    display: flex; 
    justify-self:flex-end; 
    width: auto;
  }

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
    font-size: 14px;
    color : #909090;
    margin-top: 10px;
  }

    /* 게시글 부분 */
  .post-section {
    width: 80%;
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
    width: 100%;
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
    border-radius: 10px;
    margin-top: 10px;
  }
  .username {
    font-size: 13px;
    font-weight: 400;
    margin-top: 10px;
  }
  .post-cnt {
    line-height: 1.6;
      border: none;
      white-space: pre-wrap;
      display: flex;
      justify-content: center;
      align-items: center;
  }

  .text-box-bookclub{
    display: flex;
      flex-direction: row;
      justify-content: flex-start;
      margin-top: 10px;
      gap:30px;
  }
  
  .post-footer {
      display: flex;
      justify-content: space-between;
      gap: 20px;
      margin-top: 20px;
    }
    
  .footer-icon:hover {
    cursor: pointer;
    display: flex;
  }

  .footer-icon{
    display: flex;
    gap: 5px;
  }
  
  .add-icon {
    width: 15px;
    height: 15px;
    margin-bottom: 2px;
  }
  .icon {
    width: 18px;
    height: 18px;
    margin : 0 10px;
  }
  .like-icon {
    width: 15px;
    height: 15px;
    margin: 2px 10px;
  }
  .like-count {
    margin-top: 3px;
  }

  .date {
      color: #909090;
      font-size: 14px;
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
      justify-content: center;
      align-items: center;
      width: 80%;
      margin: auto;
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
      font-size: 30px;
      color: #ccc
    }
    .slide-next {
      background-color: transparent;
      border: none;
      cursor: pointer;
      font-size: 30px;
      color: #ccc
    }
    .slide-prev:hover {
      color : black;
    }
    .slide-next:hover {
      color: black;
    }

  </style>
  