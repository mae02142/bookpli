<template>
    <section class="comment-content">
      <article class="comment-article" v-for="item, index in comments" :key="index">
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
            <div class="post-items">
                <div class="post-header">
                    <img class="post-profile" :src="serverPost.postProfile" alt="커뮤니티 이미지" />
                    <p class="username">{{serverPost.userName}}</p>
                </div>
                <div>
                    <p class="post-cnt">{{ serverPost.postContent }}</p>
                    <p class="post-date">{{ serverPost.postDate }}</p>
                      <!-- 아이콘 섹션 -->
                    <!-- <div class="icon-container">
                        <img class="like-icon" :src="serverPost.likes.changeLike" @click="checkLike(index)" alt="좋아요 아이콘" />
                        <p class="like-count">{{serverPost.likeCount}}</p>
                    </div>  -->
                </div>
            </div>   
            <hr style="border: 1px solid #c3c3c3;">
          </div>
          
          <!-- 조회 및 수정 -->
          <div style="display: flex; flex-direction: row;">
            <div class="author-info">
              <img class="author-image" src="@/assets/icons/profile.png" alt="Author" />
              <h3>{{ item.author }}</h3>
            </div>
  
            <div class="comment-text">
              <textarea
                v-if="editIndex === index"
                class="edit-comment"
                @input="adjustHeight"
                v-model="editComment.content"></textarea>
              <p v-else>{{ item.content }}</p>
              <p class="comment-date">{{ item.commentDate }}</p>
  
              <div v-if="editIndex === index" class="comment-footer">
                <img
                  class="icon"
                  :src="editComment.likes.changeLike"
                  @click="checkLike(index)"
                  id="like-icon"
                  alt="Like"/>
                <p class="like-count">{{ editComment.likeCount }}</p>
              </div>
  
              <div v-else class="comment-footer">
                <img
                  class="icon"
                  :src="item.likes.changeLike"
                  @click="checkLike(index)"
                  id="like-icon"
                  alt="Like"/>
                <p class="like-count">{{ item.likeCount }}</p>
              </div>
            </div>
  
            <button
              v-if="editIndex === index"
              class="comment-btn"
              @click="saveComment">
              수정
            </button>
  
            <!-- 수정, 삭제 토글 버튼 -->
            <div style="position: relative;">
              <img
                class="icon"
                @click="dropdown(index)"
                src="@/assets/icons/more.png"
                alt="More"/>
              <div v-show="showBtn[index]" class="dropdown">
                <button @click="openInput(index)" class="show-btn">수정</button>
                <hr class="btn-line" />
                <button class="show-btn" @click="item.deleteCheck = true">삭제</button>
  
                <!-- 삭제 컴포넌트 -->
                <RemoveComment
                  v-model:isVisible="item.deleteCheck"
                  :deleteId="index"
                  @delete-comment="deleteComment"/>
              </div>
            </div>
          </div>
        </div>
        <hr class="divider" />
      </article>
    </section>
  </template>
  
  <script>
  import { ref } from "vue"; 
  import dislike from "@/assets/icons/dislike.png";
  import like from "@/assets/icons/like.png";
  import PostForm from "@/components/bookclub/PostForm.vue";
  import Comment from "@/components/bookclub/Comment.vue";
  import RemoveComment from "./RemoveComment.vue";  
  
  export default {
     props: {
      userInfo : {
          type : Object,
          required : true,
      },
     },
    components: {
        RemoveComment,
        PostForm,
        Comment,
    },
    setup() {
    
      const serverComments = ref([ //서버에서 받아온 데이터
          {  
          author: "트리",
          profile: `@/assets/icons/profile.png`,
          commentDate: `2024-11-27`,
          content: `저도 그 도서 너무 감명깊게 봤습니다~~@@!.`,
          },
          {
          author: "트리",
          profile: `@/assets/icons/profile.png`,
          commentDate: `2024-11-29`,
          content: `혹시 크리스마스 관련된 도서 중 미스테리장르의 도서도 있을까요?`,
          },
      ]);
      const comments = ref([]);
      // 서버에서 받아온 데이터에 추가적인 상태 값을 포함시킴
      if (Array.isArray(serverComments.value)) {
          comments.value = serverComments.value.map(post => ({
              ...post,
              likeCount: 0,
              likes: { changeLike: dislike },
              deleteCheck: false,
              openPost : false,
          }));
      } else {
          console.error('serverPosts는 배열이 아닙니다.');
      }
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
            //댓글 수정
        const editIndex=ref(null);   //수정할 인덱스를 추적하기위함
        const editComment = ref({
          author: "",
          profile: ``,
          commentDate: ``,
          content: ``,
          likeCount: 0,
          likes: { changeLike: dislike },
          deleteCheck: false,
          openPost: false,
        });
        const openInput = (index) => {
            editIndex.value = index;
            editComment.value = {...comments.value[index]};
        }    
        const saveComment = (index) => {
            comments.value[index] = {...editComment.value};
            editIndex.value = null; // 수정 종료
        }
        const updateComment = async () => {
            try {
                await axios.put("http://your-api-endpoint/comments", comments.value);
                console.log("서버 동기화 완료");
            } catch (error) {
                console.error("서버 동기화 실패", error);
            }
        };

                //댓글 삭제
        const deleteComment = (index) => {
          console.log("삭제하려는 댓글 : "+ index);
            comments.value.splice(index, 1); // 리뷰 삭제
       };

        //게시글 보여주기 
       const serverPost = ref({ //서버에서 받아온 게시글 
        userName : "길동",
        postProfile : "사진",
        postContent : `도전도전`,
        postDate : "2024-12-25",
    })
       const showPost = (index) => {
            //서버에서 값을 받아서 serverPost 에 넣어
            if(comments.value[index].openPost === true){
                comments.value[index].openPost = false;
            }else{
                // 서버에서 값 받아오기
                comments.value[index].openPost = true;
            }
       };



      return {
        serverPost,
        showPost,
        dislike,
        like,
        serverComments,
        comments,
        checkLike,
        showBtn,
        dropdown,
        adjustHeight,
        editIndex,
        editComment,
        openInput,
        saveComment,
        deleteComment,
      };
    },
  };
  </script>
  <style>
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
        width: 270px;
        padding: 10px;
        overflow: hidden;
        transition: height 0.2s ease;
    }
    .comment-date {
        text-align: end;
        margin-top: 20px;
        color: #909090;
    }
    
    .comment-footer {
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
    margin : 0 10px;
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
    margin : 10px;
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
   </style>