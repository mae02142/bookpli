<template>
    <section class="comment-content">
      <article class="comment-article" v-for="item, index in comments" :key="item.commentId">
        <div class="comment-body">
          <button class="cta" @click="showPost(index)">
            <span v-if="item.openPost ===true">게시글 닫기</span>
            <span v-else>게시글 보기</span>
            <svg width="15px" height="10px" viewBox="0 0 13 10">
              <path d="M1,5 L11,5"></path>
              <polyline points="8 1 12 5 8 9"></polyline>
            </svg>
          </button>
                            <!-- 게시글 보기 -->
        <div class="post-section" v-if="item.openPost === true">
          <div class="post-header">
              <img
                class="post-profile"
                :src="item.post.profilePath || Profile"
                alt="커뮤니티 이미지"
              />
              <p class="username">{{ item.post?.userNickname || 'User' }}</p>
            </div>
                    <!-- 이미지 슬라이드 컨테이너 -->
              <div class="post-nav">    
                <div v-if="item.post.imageUrl?.length > 1" class="image-circle"> 
                  <button class="slide-prev" @click="prevSlide(index)"><</button>
                </div>  
                <div class="post-images-wrapper">
                  <div
                    class="post-images"
                    :style="{ transform: `translateX(-${item.curpos * 100}%)` }"
                  >
                    <!-- 각 이미지 하나씩 슬라이드 -->
                    <div
                      class="post-image"
                      v-for="(img, index) in item.post.imageUrl"
                      :key="img.imageId"
                      :class="{ active: index === item.curpos }"
                    >
                      <img :src="img.imageUrl" class="post-image-img" />
                    </div>
                  </div>
                    <!-- 이미지 인디케이터 -->
                    <div class="image-indicator-box" v-show="item.post.imageUrl.length>1">
                      <div
                        class="img-indicator"
                        v-for="image, index in item.post.imageUrl"
                        :key="index"
                        :class="{activeImg: index === item.curpos}" 
                      ></div>
                    </div> 
                </div>
                <div v-if="item.post.imageUrl.length > 1" class="image-circle">
                  <button class="slide-next" @click="nextSlide(index)">></button>
                </div>
             </div>
          </div>
          <div class="post-items">
            <div class="postContent">
              <p class="post-cnt">{{ item.post?.postContent }}</p>
              <p class="post-date">{{ item.post?.postDate }}</p>
            </div>
          </div>
          
          <!-- 조회 및 수정 -->
          <div class ="cmt-article ">
           <div class="comment-box"> 
            <div class="author-info">
              <img class="author-image" src="@/assets/icons/profile.png" alt="Author" />
              <h3>{{ item.userNickname }}</h3>
            </div>
  
            <div class="comment-text">
              <textarea
                v-if="editingId === item.commentId"
                class="edit-comment"
                @input="adjustHeight"
                v-model="editComment.commentContent"></textarea>
              <p v-else>{{ item.commentContent }}</p>
  
              <div v-if="editingId === item.commentId" class="comment-footer">
                <img
                  class="icon"
                  :src="editComment.likes.changeLike"
                  @click="checkLike(index)"
                  id="like-icon"
                  alt="Like"/>
                <p v-show="editComment.likeCount >0" class="like-count">{{ editComment.likeCount }}</p>
                <p class="comment-date">{{ item.commentDate }}</p>
              </div>
  
              <div v-else class="comment-footer">
                <div class="comment-likes">
                  <img
                    class="icon"
                    :src="item.likes.changeLike"
                    @click="checkLike(index)"
                    id="like-icon"
                    alt="Like"/>
                  <p v-show="item.likeCount >0" class="like-count">{{ item.likeCount }}</p>
                </div>
                <p class="comment-date">{{ item.commentDate }}</p>
              </div>
            </div>
            <div>
              <button
              v-if="editingId === item.commentId"
              class="comment-btn"
              @click="saveComment">
              수정
            </button>
            </div>
         
            <!-- 수정, 삭제 토글 버튼 -->
            <div style="position: relative;">
              <img
                class="icon"
                @click="dropdown(index)"
                src="@/assets/icons/more.png"
                alt="More"/>
              <div v-show="showBtn[index]" class="dropdown">
                <button @click="openInput(item.commentId)" class="show-btn">수정</button>
                <hr class="btn-line" />
                <button class="show-btn" @click="confirmRemove(item.commentId)">삭제</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <hr class="divider" />
    </article>
    </section>
  </template>
  
  <script>
  import apiClient from "@/api/axiosInstance";
  import { onMounted, ref } from "vue"; 
  import dislike from "@/assets/icons/dislike.png";
  import like from "@/assets/icons/like.png";
  import Profile from "@/assets/icons/profile.png"
  import PostForm from "@/components/bookclub/PostForm.vue";
  import Comment from "@/components/bookclub/Comment.vue";
  import { useConfirmModalStore } from "@/stores/utilModalStore";
  
  export default {
     props: {
      userId : {
          type : Number,
          required : true,
      },
      bookclubId : {
        type : Number,
        required : true,
      },
     },
    components: {
        PostForm,
        Comment,
    },
    setup(props) {
      onMounted(() => {
        getComments();
      })
    
      const serverComments = ref([]);   //서버에서 받아온 데이터
      const comments = ref([]);     // 서버에서 받아온 데이터에 추가적인 상태 값을 포함시킴
      
          // 댓글 조회
      const getComments = async() => {
        try{
          const response = await apiClient.get(`/api/comment/user`, {
            params : {
              userId : props.userId,
              bookClubId : props.bookclubId
            }, 
          });

          if(response.status == 200){ // 요청 성공 시
            serverComments.value = response.data.data;

            if(Array.isArray(serverComments.value)){  // 추가 값 더하기 
              comments.value = serverComments.value.map(comment => ({
                ...comment,
                likeCount : 0,
                likes : {changeLike : dislike},
                deleteCheck : false,
                openPost : false,
                post: {
                  imageUrl: [],
                },
                curpos: 0,
              }));
            }
          }
        }catch(error){
          console.error(error, '에러 발생');
        }
      };
     
            // 좋아요 카운팅 
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
                // 수정, 삭제 버튼 보이게 하기
        const showBtn = ref([]); 
        const dropdown = (index) => {
            showBtn.value[index] = !showBtn.value[index];
        }

                 //댓글 높이 조정
        const adjustHeight = (event) => { 
            const textarea= event.target;
            textarea.style.height = "auto";
            textarea.style.height = `${textarea.scrollHeight}px`;
      }
            /* 댓글 수정 */
        const editingId=ref(null);   //수정할 id를 추적하기위함
        const editComment = ref({});
        // 수정 폼 오픈
        const openInput = (commentId) => {
          editingId.value = commentId;
          editComment.value = { ...comments.value.find(comment => 
           comment.commentId === commentId) }; 
    };

          // 서버로 댓글 수정 전송 후 처리
        const saveComment = async() => {
          const comment ={
            commentId : editComment.value.commentId,
            userId : editComment.value.userId,
            postId : editComment.value.postId,
            commentContent : editComment.value.commentContent,
            commentDate : editComment.value.commentDate
          }
          
          try {
            const response =  await apiClient.put("/api/comment/edit", comment);
                if(response.status ==200){
                  const index = comments.value.findIndex((item) => item.commentId == comment.commentId);

                  if(index !== -1){
                    comments.value[index] = {...comments.value[index],...comment};
                  }
                  editingId.value = null;
                }
            } catch (error) {
                console.error("서버 동기화 실패", error);
            }
        };

                /*댓글 삭제*/
        const confirmRemove = (commentId) =>{
          const confirmModalStore = useConfirmModalStore();
          confirmModalStore.showModal(
            '댓글 삭제',
            '댓글을 삭제하겠습니까?',
            '삭제 후 복원은 어렵습니다.',
            '삭제하기',
            '',
            () =>  deleteList(commentId)
          )
        };        
        const deleteList = async(commentId) => {
                try{
                    const response = await apiClient.delete(`/api/comment/delete`, {
                        params : {commentId : commentId},
                    });
                    if(response.status == 200){
                      const index = comments.value.findIndex((item)=> item.commentId == commentId);
                      if(index !== -1){
                        comments.value.splice(index, 1); // 리뷰 삭제
                      }     
                    }
                }catch(error){
                    console.error(error,'오류 발생');
                }
            };

                 /* 게시글 함수  */  
       const showPost = async (index) => {
        const comment = comments.value[index];
        if (comment.openPost === true) {
          // 게시글 닫기
          comment.openPost = false;
          comment.post = {}; // 게시글 데이터 초기화
        } else {
          // 게시글 열기
          try {
            const post = await getPost(comment.postId);
            comment.openPost = true;
            comment.post = post; // 게시글 데이터를 해당 댓글에 추가
          } catch (error) {
            console.error("게시글 데이터를 불러오지 못했습니다:", error);
          }
        }
      };

      const getPost = async (postId) => {
        try {
          const response = await apiClient.get(`/api/post/comment/readOne`, {
            params: { postId },
          });
          if (response.status === 200) {
            return response.data.data; // 게시글 데이터를 반환
          }
        } catch (error) {
          console.error("게시글 불러오기 에러:", error);
          throw error; // 에러를 상위 함수로 전달
        }
      };


         /* 이미지 슬라이더 처리 */

         const nextSlide = (index) => {
          const comment = comments.value[index];
          if (comment.curpos < comment.post.imageUrl.length - 1) {
            comment.curpos++;
          } else {
            comment.curpos = 0; // 마지막 슬라이드 이후 첫 번째 슬라이드로
          }
        };

        const prevSlide = (index) => {
          const comment = comments.value[index];
          if (comment.curpos > 0) {
            comment.curpos--;
          } else {
            comment.curpos = comment.post.imageUrl.length - 1; // 첫 번째 슬라이드 이전 마지막 슬라이드로
          }
        };


      return {
        getComments,
        showPost,
        getPost,
        dislike,
        like,
        Profile,
        serverComments,
        comments,
        checkLike,
        showBtn,
        dropdown,
        adjustHeight,
        editingId,
        editComment,
        openInput,
        saveComment,
        confirmRemove,
        nextSlide,
        prevSlide,
      };
    },
  };
  </script>
  <style scoped>
    /* 리스트 */
    .comment-content {
      width: 80%;
      border : 1px solid #ccc;
      border-bottom: none;
      border-radius: 20px 20px 0 0;
      padding: 20px 50px;
      margin:auto;
    }
    .comment-body {
      display: flex;
      flex-direction: column;
      margin-bottom: 20px;
      padding-top: 20px;
    }
    
    .comment-article{
      margin-top: 10px;
      display: flex;
      flex-direction: column;
    }

    .comment-box{
      display: flex;
      flex-direction: row;
      gap:15px;
    }

    .author-image {
      width: 40px;
      height: 40px;
      border-radius: 50%;
    }
    
    .author-info {
      font-size: 15px;
      display: flex;
      flex-direction: column;
      text-align: center;
    }
    .comment-text {
      font-size: 16px;
      line-height: 1.5;
      width: 100%;
    }
    .edit-comment{
        resize: none;
        border: 1px solid #e5e5e5;
        outline: none;
        border-radius: 5px;
        height: auto;
        width: 95%;
        padding: 10px;
        overflow: hidden;
        transition: height 0.2s ease;
    }
    .comment-date {
        text-align: end;
        color: #909090;
    }
    
    .comment-footer {
      display: flex;
      justify-content: space-between;
      gap: 20px;
      margin-top: 20px;
    }
    
    .comment-likes{
      display: flex;
      flex-direction: row;
    }

    .icon {
      width: 18px;
      height: 18px;
      margin-right : 10px;
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

    .divider {
      border: 0.5px solid #e5e5e5;
    }

    /* 게시글 보기 버튼 */
.cta {
  position: relative;
  margin: auto;
  margin-bottom: 20px;
  padding: 12px 18px;
  transition: all 0.2s ease;
  border: none;
  background: none;
  cursor: pointer;
}

.cta:before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  display: block;
  border-radius: 50px;
  background: beige;
  width: 45px;
  height: 45px;
  transition: all 0.3s ease;
}

.cta span {
  position: relative;
  font-family: "Ubuntu", sans-serif;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: #234567;
}

.cta svg {
  position: relative;
  top: 0;
  margin-left: 10px;
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke: #234567;
  stroke-width: 2;
  transform: translateX(-5px);
  transition: all 0.3s ease;
}

.cta:hover:before {
  width: 100%;
  background: beige;
}

.cta:hover svg {
  transform: translateX(0);
}

.cta:active {
  transform: scale(0.95);
}
/* 수정 버튼 */

.comment-btn {
    border: none;
    background: #FFFDF1;
    width: 40px;
    height: 40px;
    border-radius: 50%
}
.comment-btn:hover{
    cursor: pointer;
    background: beige;
    transform: translateY(-3px);
    transition: all 0.5s ease ;
}

/* 게시글 부분 */
.post-section {
    width: 80%;
    margin: auto;
    margin-bottom: 20px;
    border: 1px solid #e5e5e5;
    border-radius: 10px;
    padding-bottom: 10px;
}
.post-items{
    display: flex;
    flex-direction: row;
    gap: 40px;
  }

  .post-header{
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin: 5px 20px;
  }

  .post{
    display: flex;
    flex-direction: column;
    gap: 20px;
    padding-top: 20px
  }
  .post-profile {
    width: 30px;
    height: 30px;
    margin-right: 10px;
    border-radius: 10px;
    margin-top: 10px;
  }
  .username {
    font-size: 13px;
    font-weight: 400;
    margin-top: 10px;
  }

  .postContent{
    width: 100%;
    margin : 10px 20px;
   
  }

  .post-cnt {
    margin: 10px 0;
    line-height: 1.6;
    border: none;
    white-space: pre-wrap; /* 줄바꿈과 공백 유지 */
  }
  
  .post-date{
    font-size: 14px;
    color: #909090;
    text-align: end;
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
      position: relative;
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

/* 인디케이터 스타일 */
.image-indicator-box{
  display: flex;

  margin-top: 10px;
  position: absolute;
  top: 90%;
  left: 50%;
  transform: translateX(-50%);  /* 중앙 정렬 */
  z-index: 10;
}

.img-indicator {
  display: inline-block;
  width: 9px;
  height: 9px;
  margin: 0 5px;
  background-color: #c0bfbf;  /* 기본 색상 */
  border-radius: 50%;
  text-align: center;
  line-height: 10px;
  cursor: pointer;
}

.img-indicator.activeImg {
  background-color: #faf5d1;  /* 활성화된 인디케이터 색상 */
}

.post-image.active {
  opacity: 1;
  transition: opacity 0.3s ease;
}

.post-image {
  opacity: 0.5;
  transition: opacity 0.3s ease;
}

   </style>