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
                <nav class="nav-container">
                  <ul class="nav-list">
                    <li v-for="(item, index) in navItems" :key="index" class="nav-item">
                       <!-- 첫 번째 항목은 라우터 링크로 이동 -->
                      <RouterLink v-if="index === 0" :to="{path : item.link, 
                        query : {title: bookInfo.title, author : bookInfo.author,
                          cover : bookInfo.cover, bookClubId : bookInfo.bookclubId,
                          description : bookInfo.description
                        }
                      }"> 
                        <svg
                          :class="['nav-icon', { 'nav-icon--active': selected === index }]"
                          fill="none"
                          :viewBox="item.viewBox"
                          xmlns="http://www.w3.org/2000/svg"
                          v-html="item.icon"
                        ></svg>
                        <span class="nav-name">{{item.name}}</span>
                      </RouterLink>
                       <!-- 두 번째와 세 번째 항목은 컴포넌트를 표시 -->
                 <div v-else> 
                  <svg
                    :class="['nav-icon', { 'nav-icon--active': selected === index }]"
                    fill="none"
                    :viewBox="item.viewBox"
                    xmlns="http://www.w3.org/2000/svg"
                    v-html="item.icon"
                    @click="handleItemClick(index)"
                  ></svg>
                  <span class="nav-name">{{item.name}}</span>
                </div>
              </li>
            </ul>
          </nav>
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
      const route = useRoute();
      const authStore = useAuthStore();
      onMounted(()=>{
        getInfo();
      })

      const navItems = ref([
      { 
        name : '북클럽 홈',
        link: '/bookclub/community',
        icon: '<path d="m4 12 8-8 8 8M6 10.5V19a1 1 0 0 0 1 1h3v-3a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v3h3a1 1 0 0 0 1-1v-8.5" stroke-width="2" stroke-linejoin="round" stroke-linecap="round" stroke="currentColor"></path>' },
      {
        name : '내가 쓴 게시글',
        link : '',
        icon: `
        <path d="M17.5556 18.2H18.0556V17.7V7.3V6.8H17.5556H7.44444H6.94444V7.3V17.7V18.2H7.44444H17.5556ZM18.2778 18.5H6.72222C6.64439 18.5 6.58348 18.4717 6.54602 18.438C6.50939 18.405 6.5 18.3733 6.5 18.35V6.65C6.5 6.62676 6.50939 6.59499 6.54602 6.56203C6.58349 6.52831 6.64439 6.5 6.72222 6.5H18.2778C18.3556 6.5 18.4165 6.52831 18.454 6.56203C18.4906 6.59499 18.5 6.62675 18.5 6.65V18.35C18.5 18.3733 18.4906 18.405 18.454 18.438C18.4165 18.4717 18.3556 18.5 18.2778 18.5ZM9.38889 9.1H11.2778V10.7H9.38889V9.1ZM9.38889 13H15.6111V13.3H9.38889V13ZM9.38889 15.6H15.6111V15.9H9.38889V15.6ZM13.7222 9.75H15.6111V10.05H13.7222V9.75Z" stroke="currentColor"/>
        `,
      },
      {
        name : '내가 쓴 댓글',
        link : '',
        icon: `
        <path d="M9.67505 17.7948L9.51166 17.7074L9.3308 17.7476L6.65854 18.3415L7.25237 15.6692L7.29256 15.4883L7.20519 15.3249C6.75529 14.4836 6.5 13.5224 6.5 12.5C6.5 9.18629 9.18629 6.5 12.5 6.5C15.8137 6.5 18.5 9.18629 18.5 12.5C18.5 15.8137 15.8137 18.5 12.5 18.5C11.4776 18.5 10.5164 18.2447 9.67505 17.7948ZM9.81649 17.5302L9.8165 17.5302C10.6354 17.9681 11.5503 18.2 12.5 18.2C15.648 18.2 18.2 15.648 18.2 12.5C18.2 9.35198 15.648 6.8 12.5 6.8C9.35198 6.8 6.8 9.35198 6.8 12.5C6.8 13.4497 7.03185 14.3646 7.46975 15.1835L7.88033 14.9639L7.46975 15.1835L7.60954 15.4449L7.22411 17.1793L7.05366 17.9463L7.82067 17.7759L9.55508 17.3904L9.81649 17.5302Z" stroke="currentColor"/>
        `,
      },
    ]);
    const selected = ref(1);
 // 클릭한 항목에 대해 동작을 처리하는 메서드
     const handleItemClick = (index) => {
      selected.value = index;  // 클릭된 항목을 선택으로 표시
    };

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
        handleItemClick,
        selected,
        navItems,
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
  .nav-container {
    width: 200px;
    height: 40px;
    background-color: #fff;
    border-radius: 40px;
    box-shadow: 0 10px 15px rgba(0, 0, 0, 0.041);
    display: flex
;
    align-items: center;
    margin-left: auto;
    margin-top: auto;
}

.nav-name{
  font-size: 10px;
}

.nav-list {
  list-style: none;
  width: 100%;
  display: flex;
  justify-content: space-between;
  margin: 0;
  padding: 0;
}

.nav-item {
  display: flex;
  justify-content: center;
  align-items: center;
}

.nav-radio {
  display: none;
}

.nav-label {
  cursor: pointer;
}

.nav-icon {
  width: 24px;
  height: 24px;
  opacity: 0.8;
  padding: 13px 20px;
  transition: 0.2s;
}

.nav-icon:hover {
  opacity: 1;
  color: rgb(235, 40, 176);
  margin-top: -4px;
}

.nav-icon--active {
  color: rgb(235, 40, 176);
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
  