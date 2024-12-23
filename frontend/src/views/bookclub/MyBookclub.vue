<template>
    <div class="community-post">
      <div class="community-label">커뮤니티</div>
      <header class="header">
        <img class="profile-image" :src="userInfo.profilePath || Profile" alt="Profile" />
        <div class="title-icon">
            <div class="post-header">
                <div class="post-title">{{ bookInfo.title.replace(/\(.*?\)/g, '').trim()}}</div>
                <div class="title">{{ userInfo.userNickname}} 님의 활동</div> 
            </div>
          </div>
              <div class="nav-with-name">
                <nav class="nav-container">
                  <ul class="nav-list">
                    <!-- 북클럽 홈 -->
                  <RouterLink :to="{path :'/bookclub/community', 
                        query : {title: bookInfo.title, author : bookInfo.author,
                          cover : bookInfo.cover, bookClubId : bookInfo.bookclubId,
                          description : bookInfo.description
                        }
                      }"> 
                    <li class="nav-item">
                      <a aria-label="bookclub-home" data-social="bookclub-home">
                        <div class="filled"></div>
                        <svg
                          viewBox="0 0 24 24"
                          fill="currentColor"
                          height="24"
                          width="24"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                        <path d="m4 12 8-8 8 8M6 10.5V19a1 1 0 0 0 1 1h3v-3a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v3h3a1 1 0 0 0 1-1v-8.5" stroke-width="2" stroke-linejoin="round" stroke-linecap="round" stroke="currentColor"></path>
                        </svg>
                      </a>
                      <div class="nav-name">북클럽 홈</div>
                    </li>
                    </RouterLink>
                     <!-- 게시글  -->
                    <li class="nav-item">
                      <a
                        @click="selected=1"
                        aria-label="mypost"
                        data-social="mypost"
                      >
                        <div class="filled"></div>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="24"
                          height="24"
                          fill="currentColor"
                          viewBox="0 0 24 24"
                        >
                        <path d="M17.5556 18.2H18.0556V17.7V7.3V6.8H17.5556H7.44444H6.94444V7.3V17.7V18.2H7.44444H17.5556ZM18.2778 18.5H6.72222C6.64439 18.5 6.58348 18.4717 6.54602 18.438C6.50939 18.405 6.5 18.3733 6.5 18.35V6.65C6.5 6.62676 6.50939 6.59499 6.54602 6.56203C6.58349 6.52831 6.64439 6.5 6.72222 6.5H18.2778C18.3556 6.5 18.4165 6.52831 18.454 6.56203C18.4906 6.59499 18.5 6.62675 18.5 6.65V18.35C18.5 18.3733 18.4906 18.405 18.454 18.438C18.4165 18.4717 18.3556 18.5 18.2778 18.5ZM9.38889 9.1H11.2778V10.7H9.38889V9.1ZM9.38889 13H15.6111V13.3H9.38889V13ZM9.38889 15.6H15.6111V15.9H9.38889V15.6ZM13.7222 9.75H15.6111V10.05H13.7222V9.75Z" stroke="currentColor"/>
                        </svg>
                      </a>
                      <div class="nav-name">내가 쓴 게시글</div>
                    </li>
                     <!-- 댓글 -->
                    <li class="nav-item">
                      <a
                       @click="selected=2"
                        aria-label="mycomment"
                        data-social="mycomment"
                      >
                        <div class="filled"></div>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="24"
                          height="24"
                          fill="currentColor"
                          viewBox="0 0 24 24"
                          xml:space="preserve"
                        >
                        <path d="M9.67505 17.7948L9.51166 17.7074L9.3308 17.7476L6.65854 18.3415L7.25237 15.6692L7.29256 15.4883L7.20519 15.3249C6.75529 14.4836 6.5 13.5224 6.5 12.5C6.5 9.18629 9.18629 6.5 12.5 6.5C15.8137 6.5 18.5 9.18629 18.5 12.5C18.5 15.8137 15.8137 18.5 12.5 18.5C11.4776 18.5 10.5164 18.2447 9.67505 17.7948ZM9.81649 17.5302L9.8165 17.5302C10.6354 17.9681 11.5503 18.2 12.5 18.2C15.648 18.2 18.2 15.648 18.2 12.5C18.2 9.35198 15.648 6.8 12.5 6.8C9.35198 6.8 6.8 9.35198 6.8 12.5C6.8 13.4497 7.03185 14.3646 7.46975 15.1835L7.88033 14.9639L7.46975 15.1835L7.60954 15.4449L7.22411 17.1793L7.05366 17.9463L7.82067 17.7759L9.55508 17.3904L9.81649 17.5302Z" stroke="currentColor"/>

                        </svg>
                      </a>
                      <div class="nav-name">내가 쓴 댓글</div>
                    </li>
                  </ul>
                </nav>
              </div> 

      </header>
      <hr class="divider" />
          <!-- 게시글 추가 -->
      <div class="add-post"  @click="addPost = true">
            <p class="description">책에 대한 이야기를 나눠보세요</p>
            <img class="add-icon" src="@/assets/icons/add.png" alt="추가 아이콘" />
      </div>
      <PostForm :modelValue="addPost"
       @update:modelValue="addPost = $event" 
       :userId="userInfo.userId" 
       :bookclubId="bookInfo.bookclubId" 
       @reload="reloadpost"
       />
             <!-- 게시글 리스트 -->
    <MyPost 
      v-if="selected === 1" 
      :userId="authStore.user.userId" 
      :bookclubId="bookInfo.bookclubId" 
      :nickname="userInfo.userNickname" 
      :profile="userInfo.profilePath" 
      ref="myPostComponent"
    />
    <MyComment
     v-if="selected === 2"
     :userId="userInfo.userId"
     :bookclubId="bookInfo.bookclubId"
    />
    <musicPlayer />
    </div>
  </template>
  
  <script>
  import { onMounted, ref } from "vue";
  import PostForm from "@/components/bookclub/PostForm.vue";
  import MyPost from "@/components/bookclub/MyPost.vue";
  import MyComment from "@/components/bookclub/MyComment.vue";
  import { useRoute } from "vue-router";
  import { useAuthStore } from '@/stores/auth';
  import Profile from"@/assets/icons/profile.png";
import apiClient from "@/api/axiosInstance";
import musicPlayer from "@/components/layouts/musicPlayer.vue";


  export default {
    components: {
        PostForm,
        MyPost,
        MyComment,
        musicPlayer,
    },
    setup() {
      // 전역 스토어 설치
      const route = useRoute();
      const authStore = useAuthStore();
      onMounted(()=>{
        getInfo();
      })

    const selected = ref(1);
 // 클릭한 항목에 대해 동작을 처리하는 메서드


      const bookInfo = ref(
          { title : route.query.title,
          bookclubId: Number(route.query.bookClubId),
          author : route.query.author,
          cover : route.query.cover,
          description : route.query.description,
          });  
       
        const addPost = ref(false);
        
        const userInfo = ref({});
        const getInfo = async() => {
          const response = await apiClient.get(`api/mypage/${authStore.user.userId}`);
          if(response.status == 200){
            userInfo.value = response.data.data;
          }else{
            console.error('오류 발생');
          }
        }

           /* mypost로 게시글 리로딩 전달 */
        const myPostComponent = ref(null);
        const reloadpost = ( ) => {
          if(myPostComponent.value){
            myPostComponent.value.getMyposts();
          }
        }
      return {
        authStore,
        Profile,
        selected,
        addPost,
        bookInfo,
        userInfo,
        getInfo,
        myPostComponent,
        reloadpost,
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

  /* 네비 */

  .nav-with-name{
    margin-left: 50px;
  }

   .nav-container {
    width: 200px;
    height: 40px;
    display: flex;
    align-items: center;
    margin-left: auto;
    margin-top: auto;
} *

.nav-list {
  display: flex;
  justify-content: center;
  align-items: center;
}
.nav-list .nav-item {
  margin: 0 10px;
  position: relative;
}
.nav-list .nav-item .nav-name {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  border-radius: 5px;
  opacity: 0;
  visibility: hidden;
  font-size: 14px;
  transition: all 0.3s ease;
  width: 90px;
  height: 30px;
  text-align: center;
}
.nav-list .nav-item:hover .nav-name {
  opacity: 1;
  visibility: visible;
  width: 90px;
  text-align: center;
  padding-top:13px;
  top: -50px;
  background-color: #171d25;
  color:#fff;
}
.nav-list .nav-item a {
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  color: #4d4d4d;
  background-color: #fff;
  transition: all 0.3s ease-in-out;
  cursor: pointer;
}
.nav-list .nav-item a:hover {
  box-shadow: 3px 2px 45px 0px rgb(0 0 0 / 12%);
  
}
.nav-list .nav-item a svg {
  position: relative;
  z-index: 1;
  width: 24px;
  height: 24px;
}
.nav-list .nav-item a:hover {
  color: white;
}
.nav-list .nav-item a .filled {
  position: absolute;
  top: auto;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 0;
  background-color: #000;
}

.nav-list .nav-item a:hover .filled {
  height: 100%;
}

.nav-list .nav-item a .filled {
  background-color: #171d25;
}


    /*  헤 더  */
  .header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin: 20px 0;
    gap: 50px;
  }
  
  .profile-image {
    width: 90px;
    height:90px;
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
    flex-direction: column;;
    align-items: center;
    gap: 20px;
  }
  
  .post-title {
    font-size: 20px;
    font-weight: 400;
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

</style>