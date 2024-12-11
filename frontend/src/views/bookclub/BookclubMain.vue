<template>
    <div class="community">

      <LeftSidebar />
    <div class="sidebar3">
    <div class="svg-container">
      <svg
        class="rotatable-svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        @click="openEdit"
      >
        <path
          d="M9.60803 1.75L9.10502 4.336C8.26922 4.64433 7.49158 5.09181 6.80503 5.6595L4.32003 4.803L1.92603 8.947L3.91603 10.677C3.75891 11.5528 3.75891 12.4497 3.91603 13.3255L1.92603 15.055L4.32053 19.197L6.80253 18.342C7.48887 18.9108 8.26765 19.3578 9.10502 19.6635L9.60753 22.25H14.392L14.8845 19.69C15.7175 19.3576 16.4961 18.9026 17.1945 18.34L19.679 19.197L22.071 15.055L20.083 13.3235C20.2418 12.4492 20.2418 11.5533 20.083 10.679L22.073 8.9455L19.6785 4.8035L17.1965 5.6585C16.5096 5.09047 15.731 4.64358 14.894 4.337L14.3915 1.751L9.60803 1.75ZM11.9565 8.853C12.7921 8.8678 13.5885 9.21013 14.1742 9.80629C14.7599 10.4025 15.0881 11.2048 15.0881 12.0405C15.0881 12.8762 14.7599 13.6785 14.1742 14.2747C13.5885 14.8709 12.7921 15.2132 11.9565 15.228C11.1112 15.228 10.3005 14.8923 9.70262 14.2946C9.10478 13.697 8.76879 12.8863 8.76853 12.041V12.0405C8.76853 11.6219 8.85099 11.2073 9.01121 10.8206C9.17143 10.4338 9.40626 10.0824 9.7023 9.78642C9.99834 9.49043 10.3498 9.25565 10.7366 9.09549C11.1234 8.93533 11.5379 8.85293 11.9565 8.853Z"
          stroke="black"
          stroke-width="0.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </div>
      <div class="bookclub-list">
        <!-- 북클럽 목록 -->
        <div
          class="bookclub-item"
          v-for="myclub in myclubList"
          :key="myclub.userClubId"
        >
        <RouterLink :to="{path : '/bookclub/community', 
        query : {cover : myclub.cover , title : myclub.title, author : myclub.author , bookClubId : myclub.bookClubId}}">
          <img :src="myclub.cover" alt="icon" class="note-icon" />
          <div class="bookclub-details">
            <p class="bookclub-name">{{ myclub.title.replace(/\(.*?\)/g, '').trim() }}</p>
            <img @click="removeClub(myclub.userClubId)" 
            src="@/assets/icons/close.png" 
            alt="remove club" 
            class="close-icon" 
            v-show="showEdit"
            />
          </div>
        </RouterLink>
        </div>
      </div>
    </div>

    <div class ="main">
      <header class="header">
        <div class="title-quotes">
            <img class="quotes" src="@/assets/icons/double-quotes-l.png" alt="doble-quotes-l">
            <h1>북적북적</h1>
            <img class="quotes" src="@/assets/icons/double-quotes-r.png" alt="double-quotes-r">
        </div>
        <p class="subtitle">
          책을 사랑하는 사람들이 의견을 나누고, 추천하고, 독서 경험을 공유하는 공간
        </p>
        <div class="search-bar">
            <input v-model="searchValue" class="search-guide" placeholder="찾으려는 커뮤니티의 도서명을 입력하세요"></input>
            <img class="search-community" @click="searchBookClub(searchValue)" src="@/assets/icons/searchW.png" alt="Search Community">
        </div>
      </header>
      <section v-if="searchList" class="search-content">
        <h2 class="list-title">“{{ searchValue }}” 에 해당하는 커뮤니티</h2>
        <div class="divider"></div>
      
      <article class="community-list">
        <div class="community-item" v-for="item, index in communities" :key="item.bookClubId">
          <div class="img-btn">
            <img :src="item.cover" :alt="item.title" class="item-image" />
            <span class="add-community" @click="addBookClub(index)">
                <img class="add-icon" src="@/assets/icons/plus.png"><p>커뮤니티 추가</p>
             </span>
          </div>  
          <div class="item-details">
            <h3 class="item-title">{{ item.title.replace(/\(.*?\)/g, '').trim() }}</h3>
            <p class="item-author">{{ item.author.replace(/\(.*?\)/g, '').trim() }}</p>
          </div>
        </div>
      </article>
      </section>
    </div>
    </div>
  </template>
  <script>
  import axios from "axios";
  import { onMounted, ref } from "vue";
  import { useAuthStore } from '@/stores/auth';
  import LeftSidebar from "@/components/layouts/LeftSidebar.vue";
  import { useRouter } from "vue-router";

  export default {
    components: {LeftSidebar},
    setup() {
      const authStore = useAuthStore();

      const communities = ref([]); // 서버에서 받아온 도서리스트
      const searchValue = ref(""); //검색어 
      const searchList= ref(false); // 검색버튼 누르면 리스트 보일 수 있게

      const searchBookClub = async(searchValue) => {
        console.log('찾으려는 북클럽 keyword : '+ searchValue);
        try{
          console.log('겟요청 보내기');
          const response = await axios.get("http://localhost:8081/api/bookclub/search", {
            params : {keyword : searchValue},
          });
            console.log('겟 요청 후');
            if(response.status == 200){
              communities.value = response.data;
              console.log(JSON.stringify(communities.value));
            }
        }catch(error){
          console.error(error, '오류 발생');
        }
        searchList.value = true;
      }

      //커뮤니티 추가
      const addBookClub = async(index) => {
        try{

          // 로그인 여부 확인
          if (!authStore.user || !authStore.user.userId) {
          alert("로그인이 필요합니다!");
          window.location.href = "http://localhost:3000/auth/login";
          return;
        }

        const response = await axios.post(
  `http://localhost:8081/api/userbookclub/add/bookclub?isbn13=${encodeURIComponent(communities.value[index].isbn13)}&userId=${authStore.user.userId}`
);
        console.log(response.data);
        if(response.data.data === true){
          alert("북클럽이 추가되었습니다.");
        }else{
          alert("이미 추가된 북클럽입니다.");
        }
      }catch(error){
        console.log(error, "!!!!!오류 발생");
      }
      readMyClubs();
    };

      // 유저 북클럽 리스트 가져오기 

    const  myclubList = ref([]);
    const readMyClubs = async() => {
      try{
      const response = await axios.get(
        "http://localhost:8081/api/userbookclub/mybookclubs",
       {params : {userId :authStore.user.userId}
      });
      myclubList.value = response.data.data;
      console.log(JSON.stringify(myclubList.value));
      }catch(error){
        console.error(error, "리스트 가져오는 중 에러 발생!")
      }
    };

      // 북클럽 리스트 삭제 버튼 오픈
    const showEdit = ref(false);
    const openEdit = () => {
      if(showEdit.value === true){
        showEdit.value=false;
      }else{
        showEdit.value = true;
      }
    }
      // 북클럽 삭제
    const removeClub = async(userClubId) => {
      const answer = confirm("북클럽을 삭제하시겠습니까?");
      console.log("삭제하려는 id :"+ userClubId);
      if(answer){
        try{
        const response = await axios.delete(
          "http://localhost:8081/api/userbookclub/remove/myclub", {
            params : {userClubId : userClubId},
          });
          if(response.status === 200){
            alert("북클럽이 삭제되었습니다.");
          }
      }catch(error){
        console.error(error, "에러 발생");
      }
      readMyClubs();
      }
    }
     


    onMounted(()=>{
      console.log('회원아이디 :'+ authStore.user.userId);
      readMyClubs();
    })
      return {
        communities,
        searchValue,
        searchList,
        searchBookClub,
        addBookClub,
        myclubList,
        readMyClubs,
        showEdit,
        openEdit,
        removeClub,
      };
    },
  };
  </script>
<style scoped>
/* Global Styles */
body {
  font-family: "Inter", sans-serif;
  margin: 0;
  padding: 0;

}

/* Main Container */
.community {
  height: 100vh;
  display: flex;
}

/* sidebar */

.svg-container {
    display: flex;
    justify-content: end;
  }

  .rotatable-svg {
    transition: transform 0.5s ease;
  }

  .svg-container:hover .rotatable-svg {
    transform: rotate(360deg);
    cursor: pointer;
  }



.sidebar3 {
  width: 250px;
  padding: 10px 20px;
}

.bookclub-list {
  margin-top: 20px;
}

.bookclub-item {
  display: flex;
  align-items: center;
  height: 50px;
  transition: background-color 0.3s ease;
}

.bookclub-item:hover {
  cursor: pointer;
  background-color: rgba(214, 214, 214, 0.6);
  border-radius: 5px;
  }

.note-icon {
  width: 25px;
  height: 25px;
  margin-right: 13px;
  border-radius: 50%;
}

.bookclub-details {
  display: flex;
  gap : 15px;
}

.bookclub-name {
  font-size: 16px;
  font-weight: bold;
}

.close-icon {
  width: 13px;
  height: 13px;
  margin-top: auto;
}

/* main */
.main{
  display: flex;
  margin-top: 50px;
  width: 60%;
  align-items: center;
  flex-direction: column;
}

/* Header Section */
.header {
 display: flex;
 flex-direction: column;
 align-items: center;
 justify-content: center;
  margin-bottom: 40px;
  line-height: 2;
}
.header h1 {
  font-size: 33px;
  font-weight: 400;
  margin: 20px 0;
}
.title-quotes {
    display: flex;
    align-items: center;
    gap : 20px;
}
.quotes {
    padding-bottom: 30px;
}
.header .subtitle {
  font-size: 18px;
  color: #444;
}

.search-bar {
    display: flex;
    align-items: center;
    background-color: #d9d9d9; /* 배경 색상 */
    border-radius: 50px;
    padding: 10px 20px;
    width: 100%;
    max-width: 600px;
    box-sizing: border-box;
    margin-top: 30px;
}
.search-bar input {
    flex: 1;
    border : none;
    outline: none;
    background-color: transparent;
    font-size: 16px;
    text-align: center;
    color: #909090;
}
.search-bar img {
    width: 24px;
    height: 24px;
    margin-left: 10px;
    cursor: pointer;
}

.search-guide:focus {
  text-align: left;
}


/* Community List Section */
.search-content {
  width: 100%;
  max-width: 900px;
  margin-top: 90px;
}
.community-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 한 줄에 3개 */
  gap: 20px; /* 아이템 간의 간격 */
}
.list-title {
  font-size: 24px;
  font-weight: 400;
  color: #000;
  text-align: left;
  margin-bottom: 10px;
}
.divider {
  width: 100%;
  height: 1px;
  background: #000;
  margin-bottom: 20px;
}

/* Community List */

.community-item {
  display: flex;
  flex-direction: row; /* 아이템을 세로로 정렬 */
  align-items: center;
  padding: 10px;
  background-color: #fff;
}

.item-image {
  width: 115px;
  height: 130px;
  border: 1px solid #c0c0c0;
  margin-right: 20px;
}
.item-details {
  flex: 1;
  text-align: left;
  line-height: 1.8;
  align-self: flex-start;
}
.item-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
}
.item-description {
  font-size: 16px;
  color: #444;
}
.item-author {
  font-size: 14px;
  color: #909090;
}
.img-btn {
  display: flex;
  flex-direction: column;
  gap: 5px;  
}
.add-icon {
    width: 15px;
    height: 15px;
    margin-right: 7px;
}
.add-icon:hover{
  width: 17px;
  height: 17px;
  transform: rotate(45deg);
  transition: all 0.2s ease;
  
}
.add-community:hover {
  transition: all 0.3s ease;
  transform: scale(1.1);
    cursor: pointer;
}
.add-community {
    display: flex;
}

</style>
  
  